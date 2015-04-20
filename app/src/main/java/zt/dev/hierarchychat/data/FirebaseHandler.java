package zt.dev.hierarchychat.data;

import com.firebase.client.Firebase;

public class FirebaseHandler {

    /*
     * Firebase References
     */

    public static final String ROOT_URL = "https://hierarchy-chat.firebaseio.com/";
    public static final String USER_URL = ROOT_URL + "user/";
    public static final String GROUP_URL = ROOT_URL + "group/";
    public static final String CHAT_URL = ROOT_URL + "chat/";
    public static final String PERMISSION_URL = ROOT_URL + "permission/";
    public static final String PAINT_BOARD_URL = ROOT_URL + "paint-board/";

    /*
     * Firebase User Constant Keys
     */

    public static final String USER_NAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";

    /*
     * Firebase Group Constant Keys
     */

    public static final String ROOT_GROUP_KEY = "root-group";
    public static final String PARENT_ID_KEY = "parentId";
    public static final String GROUP_NAME_KEY = "groupName";
    public static final String GROUP_OWNER_KEY = "groupOwner";

    /*
     * Firebase Chat Constant Keys
     */

    public static final String AUTHOR_KEY = "author";
    public static final String MESSAGE_KEY = "message";
    public static final String TIMESTAMP_KEY = "timeStamp";
    public static final String IS_PAINT_BOARD_KEY = "isPaintBoard";

    /*
     * Firebase Paint Board Constant Keys
     */

    public static final String X_POS_KEY = "xPos";
    public static final String Y_POS_KEY = "yPos";
    public static final String COLOR_KEY = "color";
    public static final String STATE_KEY = "state";
    public static final String STROKE_WIDTH_KEY = "strokeWidth";

    /*
     * Firebase Permission Constant Keys
     */

    public static final String GROUP_ID_KEY = "groupId";
    public static final String IS_ALLOWED_KEY = "isAllowed";

    /*
     * Properties
     */

    private static FirebaseHandler hanlder;
    private Firebase userRef;
    private Firebase groupRef;
    private Firebase chatRef;
    private Firebase permissionRef;
    private Firebase paintBoardRef;

    /*
     * Singleton Pattern
     */

    private FirebaseHandler() {
        userRef = new Firebase(USER_URL);
        groupRef = new Firebase(GROUP_URL);
        chatRef = new Firebase(CHAT_URL);
        permissionRef = new Firebase(PERMISSION_URL);
        paintBoardRef = new Firebase(PAINT_BOARD_URL);
    }

    public static FirebaseHandler getInstance() {
        if (hanlder == null) {
            hanlder = new FirebaseHandler();
        }
        return hanlder;
    }

    /*
     * Getters
     */

    public Firebase getUserRef() {
        return userRef;
    }

    public Firebase getGroupRef() {
        return groupRef;
    }

    public Firebase getChatRef() {
        return chatRef;
    }

    public Firebase getPermissionRef() { return permissionRef; }

    public Firebase getPaintBoardRef() { return paintBoardRef; }
}
