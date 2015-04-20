package zt.dev.hierarchychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import zt.dev.hierarchychat.data.FirebaseHandler;

public class GroupChatActivity extends BaseActivity{

    private ChatListFragment chatListFragment;
    private String username;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.group_chat);

        Intent data = getIntent();
        if (data != null) {
            if (data.getExtras()!= null) {
                username = data.getExtras().getString(BaseActivity.USERNAME_EXTRA);
                groupName = data.getExtras().getString(BaseActivity.GROUP_ID_EXTRA);
            }
        }

        if (findViewById(R.id.chat_list_fragment) != null) {

            if (savedInstance != null) {
                return;
            }
            if (groupName == null) {
                groupName = FirebaseHandler.ROOT_GROUP_KEY;
            }
            chatListFragment = ChatListFragment.newInstance(groupName, username);
            chatListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chat_list_fragment, chatListFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                openGroupChatSettingsActivity(groupName, username);
                break;
            case R.id.log_out:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
