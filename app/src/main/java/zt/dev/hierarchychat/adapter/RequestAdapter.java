package zt.dev.hierarchychat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import zt.dev.hierarchychat.BaseActivity;
import zt.dev.hierarchychat.R;
import zt.dev.hierarchychat.data.FirebaseHandler;
import zt.dev.hierarchychat.entity.Request;

public class RequestAdapter extends ArrayAdapter<Request> {

    /*
     * Store temporary data for later use.
     */

    private String username;
    private int resourceId;
    List<Request> requests;
    Activity activity;

    public RequestAdapter(Activity activity, int layout, List<Request> requests, String username) {
        super(activity, layout, requests);
        this.resourceId = layout;
        this.username = username;
        this.requests = requests;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout todoView = null;
        try {
            final Request curRequest = getItem(position);

            if (convertView == null) {
                todoView = new RelativeLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext()
                        .getSystemService(inflater);
                vi.inflate(resourceId, todoView, true);
            } else {
                todoView = (RelativeLayout) convertView;
            }

            // Get all required UI references.
            TextView requestTextView = (TextView) todoView.findViewById(R.id.request_textview);
            Button acceptButton = (Button) todoView.findViewById(R.id.accept_button);
            Button declineButton = (Button) todoView.findViewById(R.id.decline_button);

            // Hold the position index of current item
            final int fIdx = position;

            // Set onClickListner to acceptButton and declineButton
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRequest(curRequest);
                    requests.remove(fIdx);
                    notifyDataSetChanged();
                    ((BaseActivity) activity).displayToastMessage(R.string.accept_succeed, Toast.LENGTH_SHORT);
                }
            });
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    declineRequest(curRequest);
                    requests.remove(fIdx);
                    notifyDataSetChanged();
                    ((BaseActivity) activity).displayToastMessage(R.string.decline_succeed, Toast.LENGTH_SHORT);
                }
            });

            // Set the request text.
            requestTextView.setText(curRequest.getUserName() + " requests: " + curRequest.getGroupName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return todoView;
    }

    /*
     * Accept user request
     */

    public void acceptRequest(Request request) {
        FirebaseHandler.getInstance().getPermissionRef()
                .child(username).child(request.getGroupId()).child(request.getUserName()).child(FirebaseHandler.IS_ALLOWED_KEY).setValue(true);
    }

    /*
     * Decline user request
     */

    public void declineRequest(Request request) {
        FirebaseHandler.getInstance().getPermissionRef()
                .child(username).child(request.getGroupId()).child(request.getUserName()).removeValue();
    }
}
