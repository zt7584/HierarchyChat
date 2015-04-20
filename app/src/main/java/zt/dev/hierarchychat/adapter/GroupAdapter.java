package zt.dev.hierarchychat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import zt.dev.hierarchychat.R;
import zt.dev.hierarchychat.entity.GroupWrapper;

public class GroupAdapter extends ArrayAdapter<GroupWrapper> {

    /*
     * Store the layout resource id to inflate the ui later.
     */

    private int resourceId;

    public GroupAdapter(Activity activity, int layout, List<GroupWrapper> groups) {
        super(activity, layout, groups);
        this.resourceId = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout todoView = null;
        try {
            final GroupWrapper curGroup = getItem(position);

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
            RelativeLayout groupItemRelativeLayout = (RelativeLayout) todoView.findViewById(R.id.group_item_relativelayout);
            TextView groupNameTextView = (TextView) todoView.findViewById(R.id.group_name_textview);


            // Set different color based on if the current user is allowed to enter this group.
            if (curGroup.isAllowed()) {
                groupItemRelativeLayout.setBackgroundResource(R.drawable.group_item_background);
            } else {
                groupItemRelativeLayout.setBackgroundResource(R.drawable.group_item_disabled_background);
            }

            // Set the group name
            groupNameTextView.setText(curGroup.getGroup().getGroupName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todoView;
    }
}