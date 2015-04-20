package zt.dev.hierarchychat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import zt.dev.hierarchychat.data.FirebaseHandler;

public class LoginActivity extends BaseActivity {

    /*
     * UI Elements
     */

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.login);

        init();
    }

    /*
     * Initialize UI Elements
     */

    public void init() {
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterButtonClick(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    /*
     * When users click register button
     */

    public void onRegisterButtonClick(String username, String password) {
        if (isValidInput(username, password)) {
            displayToastMessage(R.string.invalid_input, Toast.LENGTH_SHORT);
            return;
        }

        final String fUsername = username;
        final String fPassword = password;

        registerButton.setEnabled(false);

        FirebaseHandler.getInstance().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    if (snapshot.hasChild(fUsername)) {
                        clearUI();
                        displayToastMessage(R.string.username_not_available, Toast.LENGTH_SHORT);
                        registerButton.setEnabled(true);
                    } else {
                        registerUser(fUsername, fPassword);
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public boolean isValidInput(String username, String password) {
        return username == null || password == null || "".equals(username) || "".equals(password);
    }

    public void registerUser(String username, String password) {
        Map<String, String> newUser = new HashMap<String, String>();
        newUser.put(FirebaseHandler.USER_NAME_KEY, username);
        newUser.put(FirebaseHandler.PASSWORD_KEY, password);

        FirebaseHandler.getInstance().getUserRef().child(username).setValue(newUser, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    displayToastMessage(R.string.register_succeeded, Toast.LENGTH_SHORT);
                } else {
                    clearUI();
                    displayToastMessage(R.string.register_failed, Toast.LENGTH_SHORT);
                }
                registerButton.setEnabled(true);
            }
        });
    }

    /*
     * When users click login button
     */

    public void onLoginButtonClick(String username, String password) {
        if (isValidInput(username, password)) {
            displayToastMessage(R.string.invalid_input, Toast.LENGTH_SHORT);
            return;
        }

        final String fUsername = username;
        final String fPassword = password;

        loginButton.setEnabled(false);

        FirebaseHandler.getInstance().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || !snapshot.hasChild(fUsername)) {
                    displayToastMessage(R.string.login_failed, Toast.LENGTH_SHORT);
                } else {
                    if (fPassword.equals(snapshot.child(fUsername).child(FirebaseHandler.PASSWORD_KEY).getValue())) {
                        displayToastMessage(R.string.login_succeeded, Toast.LENGTH_SHORT);
                        openGroupChatActivity(FirebaseHandler.ROOT_GROUP_KEY, fUsername);
                    } else {
                        displayToastMessage(R.string.login_failed, Toast.LENGTH_SHORT);
                    }
                }
                clearUI();
                loginButton.setEnabled(true);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    /*
     * Clear UI Components
     */

    public void clearUI() {
        if (usernameEditText != null) {
            usernameEditText.setText("");
        }
        if (passwordEditText != null) {
            passwordEditText.setText("");
        }
    }
}
