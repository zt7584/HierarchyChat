package zt.dev.hierarchychat;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import zt.dev.hierarchychat.adapter.GroupAdapter;
import zt.dev.hierarchychat.data.FirebaseHandler;
import zt.dev.hierarchychat.entity.Group;
import zt.dev.hierarchychat.entity.GroupWrapper;

public class GroupListFragment extends Fragment implements AbsListView.OnItemClickListener {

    /*
     * Properties
     */

    private GroupListItemClickListener listener;
    private AbsListView listView;
    private GroupAdapter groupAdapter;
    private EditText groupNameEditText;
    private Button createGroupButton;

    /*
     * Path name to a group
     */

    private String groupId;
    private String username;
    private List<GroupWrapper> groups;
    private Firebase groupFirebaseRef;
    private ChildEventListener groupChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
            final Map<String, String> group = (Map<String, String>) snapshot.getValue();

            if (groupId != null && groupId.equals(group.get(FirebaseHandler.PARENT_ID_KEY))) {

                if (username.equals(group.get(FirebaseHandler.GROUP_OWNER_KEY))) {
                    groups.add(
                        new GroupWrapper(
                            new Group(
                                group.get(FirebaseHandler.PARENT_ID_KEY),
                                snapshot.getKey(),
                                group.get(FirebaseHandler.GROUP_NAME_KEY),
                                group.get(FirebaseHandler.GROUP_OWNER_KEY))
                            , true));
                } else {
                    FirebaseHandler.getInstance().getPermissionRef().child(group.get(FirebaseHandler.GROUP_OWNER_KEY))
                            .child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot != null) {
                                if (snapshot.hasChild(username) &&
                                    true == snapshot.child(username).child(FirebaseHandler.IS_ALLOWED_KEY).getValue()) {
                                    groups.add(
                                        new GroupWrapper(
                                            new Group(
                                                group.get(FirebaseHandler.PARENT_ID_KEY),
                                                snapshot.getKey(),
                                                group.get(FirebaseHandler.GROUP_NAME_KEY),
                                                group.get(FirebaseHandler.GROUP_OWNER_KEY))
                                        , true));
                                } else {
                                    groups.add(
                                        new GroupWrapper(
                                            new Group(
                                                group.get(FirebaseHandler.PARENT_ID_KEY),
                                                snapshot.getKey(),
                                                group.get(FirebaseHandler.GROUP_NAME_KEY),
                                                group.get(FirebaseHandler.GROUP_OWNER_KEY))
                                            , false));
                                }
                            }
                            groupAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            }
            groupAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
            Map<String, String> newGroup = (Map<String, String>) snapshot.getValue();
            for (GroupWrapper group : groups) {
                if (group.getGroup().getGroupId().equals(previousChildKey)) {
                    group.getGroup().setGroupName(newGroup.get(FirebaseHandler.GROUP_NAME_KEY));
                    group.getGroup().setParentId(newGroup.get(FirebaseHandler.PARENT_ID_KEY));
                }
            }

            groupAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            GroupWrapper tempGroup = null;
            for (GroupWrapper group : groups) {
                if (group.getGroup().getGroupId().equals(snapshot.getKey())) {
                    tempGroup = group;
                }
            }
            if (tempGroup != null) {
                groups.remove(tempGroup);
            }

            groupAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String key) {
        }
    };

    public static GroupListFragment newInstance(String groupId, String username) {
        GroupListFragment fragment = new GroupListFragment();
        fragment.groupId = groupId;
        fragment.username = username;
        fragment.groupFirebaseRef = FirebaseHandler.getInstance().getGroupRef();
        return fragment;
    }

    public GroupListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        listView = (AbsListView) view.findViewById(android.R.id.list);
        resetAdapterAndListView();

        groupNameEditText = (EditText) view.findViewById(R.id.new_subgroup_name_edittext);
        createGroupButton = (Button) view.findViewById(R.id.new_subgroup_name_button);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup(groupNameEditText.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (GroupListItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement GroupListItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (groupFirebaseRef != null) {
            groupFirebaseRef.removeEventListener(groupChildEventListener);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupWrapper group = (GroupWrapper) groupAdapter.getItem(position);
        if (group.isAllowed()) {
            if (listener != null) {
                listener.onGroupListItemClick(group.getGroup().getGroupId());
            }
        } else {
            openRequestDialog(group);
        }
    }

    public void resetAdapterAndListView() {
        groups = new LinkedList<GroupWrapper>();
        groupAdapter = new GroupAdapter(getActivity(), R.layout.group_item, groups);
        ((AdapterView<ListAdapter>) listView).setAdapter(groupAdapter);
        listView.setOnItemClickListener(this);

        if (groupFirebaseRef == null) {
            groupFirebaseRef = FirebaseHandler.getInstance().getGroupRef();
        }
        groupFirebaseRef.addChildEventListener(groupChildEventListener);
    }

    public void createGroup(String curGroupName) {
        if (curGroupName != null && !curGroupName.equals("")) {
            Map<String, Object> newGroup = new HashMap<String, Object>();
            newGroup.put(FirebaseHandler.PARENT_ID_KEY, groupId);
            newGroup.put(FirebaseHandler.GROUP_NAME_KEY, curGroupName);
            newGroup.put(FirebaseHandler.GROUP_OWNER_KEY, username);
            FirebaseHandler.getInstance().getGroupRef().push().setValue(newGroup);

            // TODO: Will use in other places.
            /*Map<String, Object> newPermission = new HashMap<String, Object>();
            newPermission.put(FirebaseHandler.IS_ACTIVE_KEY, true);
            FirebaseHandler.getInstance().getPermissionRef().child(owner).child(groupId).child(username).setValue(newPermission);*/
        }
    }

    public void sendRequest(GroupWrapper group) {
        Map<String, Object> newPermission = new HashMap<String, Object>();
        newPermission.put(FirebaseHandler.USER_NAME_KEY, username);
        newPermission.put(FirebaseHandler.GROUP_NAME_KEY, group.getGroup().getGroupName());
        newPermission.put(FirebaseHandler.IS_ALLOWED_KEY, false);
        newPermission.put(FirebaseHandler.GROUP_ID_KEY, group.getGroup().getGroupId());
        FirebaseHandler.getInstance().getPermissionRef()
                .child(group.getGroup().getGroupOwner())
                .child(group.getGroup().getGroupId())
                .child(username).setValue(newPermission);
    }

    public void openRequestDialog(GroupWrapper group) {
        final GroupWrapper fGroup = group;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_list_request_dialog);
        Button takePhotoButton = (Button) dialog
                .findViewById(R.id.cancel_button);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        Button pickFromGalleryButton = (Button) dialog
                .findViewById(R.id.send_button);
        pickFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendRequest(fGroup);
                ((BaseActivity)getActivity()).displayToastMessage(R.string.request_sent, Toast.LENGTH_SHORT);
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public interface GroupListItemClickListener {
        public void onGroupListItemClick(String groupName);
    }
}
