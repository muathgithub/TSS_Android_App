package com.example.muath.tss;

/**
 * Created by Muath on 4/1/2018.
 */

public class HomeworkAdapter {
    String homeworkID;
    String subject;
    String homeworkContext;
    String deadline;
    String homeworkLastUpdate;
    String userID;

    public HomeworkAdapter(){

    }

    public HomeworkAdapter(String _subject, String _homeworkContext, String _deadline, String _homeworkLastUpdate, String _homeworkID, String _userID) {
        this.subject = _subject;
        this.homeworkContext = _homeworkContext;
        this.deadline = _deadline;
        this.homeworkLastUpdate = _homeworkLastUpdate;
        this.homeworkID = _homeworkID;
        this.userID = _userID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHomeworkContext() {
        return homeworkContext;
    }

    public void setHomeworkContext(String homeworkContext) {
        this.homeworkContext = homeworkContext;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getHomeworkLastUpdate() {
        return homeworkLastUpdate;
    }

    public void setHomeworkLastUpdate(String homeworkLastUpdate) {
        this.homeworkLastUpdate = homeworkLastUpdate;
    }

    public String getHomeworkID() {
        return homeworkID;
    }

    public void setHomeworkID(String homeworkID) {
        this.homeworkID = homeworkID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
