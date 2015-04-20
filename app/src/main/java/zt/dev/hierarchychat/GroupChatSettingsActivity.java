package zt.dev.hierarchychat;

import android.content.Intent;
import android.os.Bundle;

import zt.dev.hierarchychat.data.FirebaseHandler;

public class GroupChatSettingsActivity extends BaseActivity implements GroupListFragment.GroupListItemClickListener  {

    private GroupListFragment groupListFragment;
    private RequestListFragment requestListFragment;
    private String username;
    private String groupId;

    @Override
    public void onGroupListItemClick(String groupId) {
        openGroupChatActivity(groupId, username);
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.group_chat_settings);

        Intent data = getIntent();
        if (data != null) {
            if (data.getExtras()!= null) {
                username = data.getExtras().getString(BaseActivity.USERNAME_EXTRA);
                groupId = data.getExtras().getString(BaseActivity.GROUP_ID_EXTRA);
            }
        }

        if (findViewById(R.id.group_list_fragment) != null) {

            if (savedInstance != null) {
                return;
            }

            if (groupId == null) {
                groupId = FirebaseHandler.ROOT_GROUP_KEY;
            }
            groupListFragment = GroupListFragment.newInstance(groupId, username);
            groupListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.group_list_fragment, groupListFragment).commit();
        }

        if (findViewById(R.id.request_list_fragment) != null) {

            if (savedInstance != null) {
                return;
            }

            requestListFragment = RequestListFragment.newInstance(username);
            requestListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.request_list_fragment, requestListFragment).commit();
        }
    }
}
