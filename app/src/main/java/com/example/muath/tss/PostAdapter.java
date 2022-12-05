package com.example.muath.tss;

import android.graphics.Bitmap;

/**
 * Created by Muath on 10/31/2017.
 */

public class PostAdapter {
    String userName;
    String userClassAndPostDate;
    String postTitle;
    String postContext;
    int likesCounter;
    int commentsCounter;
    Bitmap userImageBitmap;
    Bitmap postImageBitmap;
    boolean isLike;
    String postId;
    String userID;

    public PostAdapter() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public PostAdapter(String _userName, String _userClassAndPostDate, String _postTitle, String _postContext,
                       int _likesCounter, int _commentsCounter, Bitmap _userImageBitmap, Bitmap _postImageBitmap, String _postId, String _userID) {

        this.userName = _userName;
        this.userClassAndPostDate = _userClassAndPostDate;
        this.postTitle = _postTitle;
        this.postContext = _postContext;
        this.likesCounter = _likesCounter;
        this.commentsCounter = _commentsCounter;
        this.userImageBitmap = _userImageBitmap;
        this.postImageBitmap = _postImageBitmap;
        this.isLike = false;
        this.postId = _postId;
        this.userID = _userID;


    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserClassAndPostDate() {
        return userClassAndPostDate;
    }

    public void setUserClassAndPostDate(String userClassAndPostDate) {
        this.userClassAndPostDate = userClassAndPostDate;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContext() {
        return postContext;
    }

    public void setPostContext(String postContext) {
        this.postContext = postContext;
    }

    public int getLikesCounter() {
        return likesCounter;
    }

    public void setLikesCounter(int likesCounter) {
        this.likesCounter = likesCounter;
    }

    public int getCommentsCounter() {
        return commentsCounter;
    }

    public void setCommentsCounter(int commentsCounter) {
        this.commentsCounter = commentsCounter;
    }

    public Bitmap getUserImageBitmap() {
        return userImageBitmap;
    }

    public void setUserImageBitmap(Bitmap userImageBitmap) {
        this.userImageBitmap = userImageBitmap;
    }

    public Bitmap getPostImageBitmap() {
        return postImageBitmap;
    }

    public void setPostImageBitmap(Bitmap postImageBitmap) {
        this.postImageBitmap = postImageBitmap;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
