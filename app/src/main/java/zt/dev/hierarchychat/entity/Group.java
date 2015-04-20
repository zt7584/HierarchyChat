package zt.dev.hierarchychat.entity;

public class Group {
    /*
     * Properties
     */

    private String parentId;
    private String groupId;
    private String groupName;
    private String groupOwner;

    /*
     * Constructor
     */

    public Group() {
    }

    public Group(String pid, String gid, String name, String owner) {
        parentId = pid;
        groupId = gid;
        groupName = name;
        groupOwner = owner;
    }

    /*
     * Setters
     */

    public void setGroupName(String name) {
        groupName = name;
    }

    public void setParentId(String pid) { parentId = pid; }

    public void setGroupId(String gid) { groupId = gid; }

    public void setGroupOwner(String owner) { groupOwner = owner; }

    /*
     * Getters
     */

    public String getGroupName() {
        return groupName;
    }

    public String getParentId() { return parentId; }

    public String getGroupId() { return groupId; }

    public String getGroupOwner() { return groupOwner; }
}
