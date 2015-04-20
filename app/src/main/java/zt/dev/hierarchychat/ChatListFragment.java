package zt.dev.hierarchychat;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import zt.dev.hierarchychat.adapter.ChatAdapter;
import zt.dev.hierarchychat.data.FirebaseHandler;

public class ChatListFragment extends Fragment {

    /*
     * Properties
     */

    private AbsListView chatListView;
    private ChatAdapter chatAdapter;
    private EditText messageEditText;
    private Button sendButton;
    private CheckBox isPaintBoardCheckBox;

    /*
     * Path name to a group
     */

    private Firebase chatFirebaseRef;
    private String username;

    public static ChatListFragment newInstance(String chatGroupName, String username) {
        ChatListFragment fragment = new ChatListFragment();
        fragment.username = username;
        fragment.chatFirebaseRef = FirebaseHandler.getInstance().getChatRef().child(chatGroupName);
        return fragment;
    }

    public ChatListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatListView = (AbsListView) view.findViewById(android.R.id.list);
        chatListView.requestDisallowInterceptTouchEvent(true);
        chatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        resetAdapterAndListView();

        messageEditText = (EditText) view.findViewById(R.id.message_edittext);
        isPaintBoardCheckBox = (CheckBox) view.findViewById(R.id.is_paint_board_checkbox);
        sendButton = (Button) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(username, messageEditText.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void resetAdapterAndListView() {
        if (chatFirebaseRef != null) {
            chatAdapter = new ChatAdapter(chatFirebaseRef, getActivity(), R.layout.chat_item, username);
            ((AdapterView<ListAdapter>) chatListView).setAdapter(chatAdapter);
            chatAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    chatListView.setSelection(chatAdapter.getCount() - 1);
                }
            });
        } else {
            ((BaseActivity) getActivity()).displayToastMessage(R.string.firebase_error, Toast.LENGTH_SHORT);
        }
    }

    /*
     * When user clicks Send button
     */

    public void sendMessage(String author, String message) {
        Map<String, Object> newMessage = new HashMap<String, Object>();
        newMessage.put(FirebaseHandler.AUTHOR_KEY, author);
        newMessage.put(FirebaseHandler.MESSAGE_KEY, message);
        newMessage.put(FirebaseHandler.TIMESTAMP_KEY, new Date().getTime());
        if (isPaintBoardCheckBox != null && isPaintBoardCheckBox.isChecked()) {
            newMessage.put(FirebaseHandler.IS_PAINT_BOARD_KEY, true);
        } else {
            newMessage.put(FirebaseHandler.IS_PAINT_BOARD_KEY, false);
        }

        chatFirebaseRef.push().setValue(newMessage, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                clearUI();
            }
        });
    }

    /*
     * Clear UI Components
     */

    public void clearUI() {
        if (messageEditText != null) {
            messageEditText.setText("");
        }
    }
}
