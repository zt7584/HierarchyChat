package zt.dev.hierarchychat.entity;

public class Request {

    /*
     * Properties
     */

    private String userName;
    private boolean isAllowed;
    private String groupName;
    private String groupId;

    /*
     * Constructors
     */

    public Request() {
    }

    public Request(String userName, boolean isAllowed, String groupName, String groupId) {
        this.userName = userName;
        this.isAllowed = isAllowed;
        this.groupName = groupName;
        this.groupId = groupId;
    }

    /*
     * Setters
     */

    public void setUserName(String userName) { this.userName = userName; }
    public void setAllowed(boolean isAllowed) { this.isAllowed = isAllowed; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    /*
     * Getters
     */

    public String getUserName() { return userName; }
    public boolean getAllowed() { return isAllowed; }
    public String getGroupName() { return groupName; }
    public String getGroupId() { return groupId; }
}
