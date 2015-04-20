package zt.dev.hierarchychat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import zt.dev.hierarchychat.adapter.RequestAdapter;
import zt.dev.hierarchychat.data.FirebaseHandler;
import zt.dev.hierarchychat.entity.Request;

public class RequestListFragment extends Fragment {

    /*
     * Properties
     */

    private AbsListView listView;
    private RequestAdapter requestAdapter;

    private String username;
    private List<Request> requests;
    private Firebase requestFirebaseRef;
    private ChildEventListener requestChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
            Map<String, Object> request = null;
            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                request = (Map<String, Object>) childSnapshot.getValue();
                if (false == (Boolean) request.get(FirebaseHandler.IS_ALLOWED_KEY)) {
                    requests.add(
                            new Request(
                                    (String) request.get(FirebaseHandler.USER_NAME_KEY),
                                    (Boolean) request.get(FirebaseHandler.IS_ALLOWED_KEY),
                                    (String) request.get(FirebaseHandler.GROUP_NAME_KEY),
                                    (String) request.get(FirebaseHandler.GROUP_ID_KEY)
                            )
                    );
                }
            }

            requestAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
            Log.e("onChildChanged", snapshot.getKey() + ", " + previousChildKey);
            Map<String, Object> newRequest = null;
            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                newRequest = (Map<String, Object>) childSnapshot.getValue();
                for (Request request : requests) {
                    if (request.getUserName().equals(childSnapshot.getKey())) {
                        request.setGroupName((String) newRequest.get(FirebaseHandler.GROUP_NAME_KEY));
                        request.setAllowed((Boolean) newRequest.get(FirebaseHandler.IS_ALLOWED_KEY));
                        request.setUserName((String) newRequest.get(FirebaseHandler.USER_NAME_KEY));
                        request.setGroupId((String) newRequest.get(FirebaseHandler.GROUP_ID_KEY));
                    }
                }
            }

            requestAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            Log.e("onChildRemoved", snapshot.getKey());
            Request tempRequest = null;
            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                for (Request request : requests) {
                    if (request.getUserName().equals(childSnapshot.getKey())) {
                        tempRequest = request;
                    }
                }
                if (tempRequest != null) {
                    requests.remove(tempRequest);
                }
            }

            requestAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String key) {
        }
    };

    public static RequestListFragment newInstance(String username) {
        RequestListFragment fragment = new RequestListFragment();
        fragment.username = username;
        fragment.requestFirebaseRef = FirebaseHandler.getInstance().getPermissionRef().child(username);
        return fragment;
    }

    public RequestListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);

        listView = (AbsListView) view.findViewById(android.R.id.list);
        resetAdapterAndListView();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (requestFirebaseRef != null) {
            requestFirebaseRef.removeEventListener(requestChildEventListener);
        }
    }

    public void resetAdapterAndListView() {
        requests = new LinkedList<Request>();
        requestAdapter = new RequestAdapter(getActivity(), R.layout.request_item, requests, username);
        ((AdapterView<ListAdapter>) listView).setAdapter(requestAdapter);

        if (requestFirebaseRef == null) {
            requestFirebaseRef = FirebaseHandler.getInstance().getPermissionRef().child(username);
        }
        requestFirebaseRef.addChildEventListener(requestChildEventListener);
    }
}
