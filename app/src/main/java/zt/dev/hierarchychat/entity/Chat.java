package zt.dev.hierarchychat.entity;

public class Chat {
    /*
     * Properties
     */

    private String author;
    private String message;
    private long timeStamp;
    private boolean isPaintBoard;

    /*
     * Constructor
     */

    public Chat() {
    }

    public Chat(String messageAuthor, String chatMessage, long chatTimeStamp, boolean isPaintBoard) {
        author = messageAuthor;
        message = chatMessage;
        timeStamp = chatTimeStamp;
        this.isPaintBoard = isPaintBoard;
    }

    /*
     * Setters
     */

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsPaintBoard(boolean isPaintBoard) { this.isPaintBoard = isPaintBoard; }

    /*
     * Getters
     */

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getAuthor() {
        return author;
    }

    public boolean getIsPaintBoard() { return isPaintBoard; }
}

