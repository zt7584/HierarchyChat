package zt.dev.hierarchychat.adapter;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

import zt.dev.hierarchychat.R;
import zt.dev.hierarchychat.custom.PaintBoardCustomView;
import zt.dev.hierarchychat.data.FirebaseHandler;
import zt.dev.hierarchychat.entity.Chat;

public class ChatAdapter extends FirebaseListAdapter<Chat> {

    /*
     * curUsername is used to identify current user, which will determine the chat layout.
     */

    private String curUsername;

    public ChatAdapter(Query ref, Activity activity, int layout, String username) {
        super(ref, Chat.class, layout, activity);
        curUsername = username;
    }

    @Override
    protected void populateView(View view, Chat chat) {
        // Get all chat related information for later easy use.
        String chatMessage = chat.getMessage();
        String chatAuthor = chat.getAuthor();
        long chatTimeStamp = chat.getTimeStamp();

        // Get all required UI references to the current chatItem layout.
        TextView messageTextView = (TextView) view.findViewById(R.id.message_textview);
        RelativeLayout messageRelativeLayout = (RelativeLayout) view.findViewById(R.id.message_relativelayout);
        LinearLayout hostLinearLayout = (LinearLayout) view.findViewById(R.id.host_linearlayout);
        LinearLayout guestLinearLayout = (LinearLayout) view.findViewById(R.id.guest_linearlayout);
        LinearLayout paintBoardLayout = (LinearLayout) view.findViewById(R.id.paint_board_linearlayout);

        // If the current user is the author of this message, the username will locate on the right.
        if (chatAuthor.equals(curUsername)) {
            hostLinearLayout.setVisibility(View.VISIBLE);
            guestLinearLayout.setVisibility(View.GONE);
            messageRelativeLayout.setHorizontalGravity(Gravity.RIGHT);
            TextView hostTextView = (TextView) view.findViewById(R.id.host_username_textview);
            hostTextView.setText(chatAuthor);
        }
        // If the current user is not the author of this message, the username will locate on the left.
        else {
            hostLinearLayout.setVisibility(View.GONE);
            guestLinearLayout.setVisibility(View.VISIBLE);
            messageRelativeLayout.setHorizontalGravity(Gravity.LEFT);
            TextView guestTextView = (TextView) view.findViewById(R.id.guest_username_textview);
            guestTextView.setText(chatAuthor);
        }

        // Set the text of this chat message.
        messageTextView.setText(chatMessage + ". @" +
                new SimpleDateFormat("yyyy-MM-dd").format(new Date(chatTimeStamp)));

        // If the current chat contains a paint board, then just display it.
        if (chat.getIsPaintBoard()) {
            paintBoardLayout.setVisibility(View.VISIBLE);
            final PaintBoardCustomView paintBoard = (PaintBoardCustomView) view.findViewById(R.id.paint_board);
            paintBoard.setPaintRef(FirebaseHandler.getInstance().getPaintBoardRef().child(chatTimeStamp + ""));
            final CheckBox eraserCheckBox = (CheckBox) view.findViewById(R.id.eraser_checkbox);
            eraserCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    paintBoard.switchTool();
                }
            });
        } else {
            paintBoardLayout.setVisibility(View.GONE);
        }
    }
}
