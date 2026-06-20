package se.kth.md.it.bcm.bugzilla;

import java.util.Date;

public class Comment {
    private int commentId;
    private int commentCount;
    private String who;
    private String whoName;
    private String thetext;
    private Date bugWhen;
    private boolean isPrivate;

    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public String getWho() { return who; }
    public void setWho(String who) { this.who = who; }

    public String getWhoName() { return whoName; }
    public void setWhoName(String whoName) { this.whoName = whoName; }

    public String getThetext() { return thetext; }
    public void setThetext(String thetext) { this.thetext = thetext; }

    public Date getBugWhen() { return bugWhen; }
    public void setBugWhen(Date bugWhen) { this.bugWhen = bugWhen; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
}