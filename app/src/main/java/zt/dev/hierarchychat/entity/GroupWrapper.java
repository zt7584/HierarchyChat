package zt.dev.hierarchychat.entity;

public class GroupWrapper {

    /*
     * Properties
     */

    private Group group;
    private boolean isAllowed;

    /*
     * Constructors
     */

    public GroupWrapper() {
    }

    public GroupWrapper(Group group, boolean isAllowed) {
        this.group = group;
        this.isAllowed = isAllowed;
    }

    /*
     * Setters
     */

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setAllowed(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    /*
     * Getters
     */

    public Group getGroup() {
        return group;
    }

    public boolean isAllowed() {
        return isAllowed;
    }
}
