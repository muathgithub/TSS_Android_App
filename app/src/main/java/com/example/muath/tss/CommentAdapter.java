package com.example.muath.tss;

import android.graphics.Bitmap;

/**
 * Created by Muath on 11/15/2017.
 */

public class CommentAdapter {

    Bitmap userImageBitmap;
    String userName;
    String userClassAndCommentDate;
    String commentContext;
    String userID;
    String commentID;

    public CommentAdapter() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public CommentAdapter(Bitmap _userImageBitmap, String _userName, String _userClassAndCommentDate, String _commentContext, String _userID, String _commentID) {

        this.userImageBitmap = _userImageBitmap;
        this.userName = _userName;
        this.userClassAndCommentDate = _userClassAndCommentDate;
        this.commentContext = _commentContext;
        this.userID = _userID;
        this.commentID = _commentID;


    }

    public Bitmap getUserImageBitmap() {
        return userImageBitmap;
    }

    public void setUserImageBitmap(Bitmap userImageBitmap) {
        this.userImageBitmap = userImageBitmap;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserClassAndCommentDate() {
        return userClassAndCommentDate;
    }

    public void setUserClassAndCommentDate(String userClassAndCommentDate) {
        this.userClassAndCommentDate = userClassAndCommentDate;
    }

    public String getCommentContext() {
        return commentContext;
    }

    public void setCommentContext(String commentContext) {
        this.commentContext = commentContext;
    }
}
