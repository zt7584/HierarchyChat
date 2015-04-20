package zt.dev.hierarchychat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class BaseActivity extends ActionBarActivity {

    public static final String USERNAME_EXTRA = "username_extra";
    public static final String GROUP_ID_EXTRA = "group_name_extra";

    /*
     * Properties
     */

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Firebase.setAndroidContext(context);
    }

    /*
     * Activity Transitions
     */

    public void openGroupChatActivity(String groupId, String username) {
        Intent intent = new Intent(this, GroupChatActivity.class);
        intent.putExtra(USERNAME_EXTRA, username);
        intent.putExtra(GROUP_ID_EXTRA, groupId);
        startActivity(intent);
    }

    public void openGroupChatSettingsActivity(String groupId, String username) {
        Intent intent = new Intent(this, GroupChatSettingsActivity.class);
        intent.putExtra(USERNAME_EXTRA, username);
        intent.putExtra(GROUP_ID_EXTRA, groupId);
        startActivity(intent);
    }

    /*
     * Accessory Methods
     */

    public void displayToastMessage(int messageResId, int length) {
        Toast.makeText(context, messageResId, length).show();
    }
}
