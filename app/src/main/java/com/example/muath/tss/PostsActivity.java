package com.example.muath.tss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class PostsActivity extends AppCompatActivity {
    ArrayList<PostAdapter> postsArrayList;
    ProgressBar progressBar;

    TextView noPostsTextView;
    Dialog commentsDialog;
    LinearLayout editAndDeleteLinearLayout;
    int chosenPostPosition;
    List<ParseObject> postsList;
    MyCustomAdapter myAdapter;
    AlertDialog deleteCommentAlert;
    ArrayList<CommentAdapter> commentsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        editAndDeleteLinearLayout = (LinearLayout) findViewById(R.id.editAndDeleteLinearLayout);

        commentsDialog = new Dialog(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.parseColor("#ffffff"), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

        noPostsTextView = (TextView) findViewById(R.id.noPostsTextView);

        final ListView postsListView = (ListView) findViewById(R.id.postsListView);


        postsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                chosenPostPosition = position;
                if (postsArrayList.get(position).getUserID().equals(ParseUser.getCurrentUser().getString("ID"))) {
                    editAndDeleteLinearLayout.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            postsListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    editAndDeleteLinearLayout.setVisibility(View.GONE);
                }
            });
        }

        postsArrayList = new ArrayList<PostAdapter>();
        myAdapter = new MyCustomAdapter(postsArrayList);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts").addDescendingOrder("updatedAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                if (e == null && posts.size() > 0) {
                    postsList = posts;
                    for (ParseObject post : posts) {
                        final PostAdapter postAdapter = new PostAdapter();

                        postAdapter.setPostId(post.getObjectId());
                        postAdapter.setUserID(post.getString("userID"));

                        // Poster Info Start//
                        ParseQuery<ParseUser> usersQuery = ParseUser.getQuery();
                        try {
                            List<ParseUser> users = usersQuery.whereEqualTo("ID", post.getString("userID")).find();
                            postAdapter.setUserName(users.get(0).getString("firstName") + " " + users.get(0).getString("lastName"));
                            postAdapter.setUserClassAndPostDate(users.get(0).getString("class"));

                            ParseFile userImageFile = users.get(0).getParseFile("profileImage");
                            try {
                                byte[] data = userImageFile.getData();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                postAdapter.setUserImageBitmap(bitmap);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }


                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        ParseFile postImageFile = post.getParseFile("postImage");
                        if (postImageFile != null) {
                            try {
                                byte[] data = postImageFile.getData();
                                data = postImageFile.getData();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                postAdapter.setPostImageBitmap(bitmap);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }

                        postAdapter.setPostTitle(post.getString("postTitle"));
                        postAdapter.setPostContext(post.getString("postContext"));
                        List<String> postLikes = post.getList("postLikes");

                        if (postLikes != null && postLikes.size() > 0) {
                            if (postLikes.contains(ParseUser.getCurrentUser().getString("ID"))) {
                                postAdapter.setLike(true);
                            }
                        }

                        if (postLikes != null) {
                            postAdapter.setLikesCounter(postLikes.size());
                        } else {
                            postAdapter.setLikesCounter(0);
                        }

                        ParseQuery<ParseObject> commentsQuery = ParseQuery.getQuery("Comments");
                        commentsQuery.whereEqualTo("postID", post.getObjectId());

                        try {
                            postAdapter.setCommentsCounter(commentsQuery.find().size());
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }


                        postsArrayList.add(postAdapter);
                    }
                    progressBar.setVisibility(GONE);
                    myAdapter.notifyDataSetChanged();
                } else if (posts.size() == 0) {
                    progressBar.setVisibility(View.GONE);
                    noPostsTextView.setVisibility(View.VISIBLE);
                }
            }
        });
        postsListView.setAdapter(myAdapter);

    }

    public void addPost(View view) {
        Intent intent = new Intent(getApplicationContext(), PostAdder.class);
        startActivity(intent);
        finish();
    }

    public void editPost(View view) {
        Intent editPostIntent = new Intent(getApplicationContext(), PostAdder.class);
        editPostIntent.putExtra("postID", postsArrayList.get(chosenPostPosition).getPostId());
        editPostIntent.putExtra("isToEdit", true);
        startActivity(editPostIntent);
        finish();
    }

    public void deletePost(View view) {
        final AlertDialog deletePostAlert= new AlertDialog.Builder(PostsActivity.this).setTitle("Delete Post").setMessage("Are you sure that you want to delete this Post?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseQuery.getQuery("Comments").whereEqualTo("postID", postsArrayList.get(chosenPostPosition).getPostId()).findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                List<ParseObject> deletedComments = objects;

                                ParseObject.deleteAllInBackground(deletedComments);
                            }
                        });

                        ParseQuery.getQuery("Posts").whereEqualTo("objectId", postsArrayList.get(chosenPostPosition).getPostId()).findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                List<ParseObject> deletedPost = objects;

                                ParseObject.deleteAllInBackground(deletedPost, new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        postsArrayList.remove(chosenPostPosition);
                                        myAdapter.notifyDataSetChanged();
                                        if (postsArrayList.size() == 0) {
                                            noPostsTextView.setVisibility(View.VISIBLE);
                                        }
                                        Toast.makeText(PostsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).setNegativeButton("No", null).create();

        deletePostAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                deletePostAlert.getButton(deletePostAlert.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                deletePostAlert.getButton(deletePostAlert.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
            }
        });
        deletePostAlert.show();

    }


    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<PostAdapter> listNewsDataAdapter;

        public MyCustomAdapter(ArrayList<PostAdapter> listNewsDataAdapter) {
            this.listNewsDataAdapter = listNewsDataAdapter;
        }


        @Override
        public int getCount() {
            return listNewsDataAdapter.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            final View myView = mInflater.inflate(R.layout.post_ticket, null);

            final PostAdapter s = listNewsDataAdapter.get(position);

            CircleImageView userImageView = (CircleImageView) myView.findViewById(R.id.userImageView);
            userImageView.setImageBitmap(s.userImageBitmap);

            TextView userNameTextView = (TextView) myView.findViewById(R.id.userNameTextView);
            userNameTextView.setText(s.userName);

            TextView userClassAndPostDateTextView = (TextView) myView.findViewById(R.id.userClassAndPostDateTextView);
            userClassAndPostDateTextView.setText(s.userClassAndPostDate);

            ImageView postImageView = (ImageView) myView.findViewById(R.id.postImageView);
            if (s.getPostImageBitmap() != null) {
                postImageView.setVisibility(View.VISIBLE);
                postImageView.setImageBitmap(s.postImageBitmap);
            }

            TextView postTitleTextView = (TextView) myView.findViewById(R.id.postTitleTextView);
            if (s.getPostTitle() != null && !s.getPostTitle().equals("")) {
                postTitleTextView.setVisibility(View.VISIBLE);
                postTitleTextView.setText(s.postTitle);
            }


            TextView postContextTextView = (TextView) myView.findViewById(R.id.postContextTextView);
            if (s.getPostContext() != null && !s.getPostContext().equals("")) {
                postContextTextView.setVisibility(View.VISIBLE);
                postContextTextView.setText(s.postContext);
            }

            final TextView likesCounterTextView = (TextView) myView.findViewById(R.id.likesCounterTextView);
            likesCounterTextView.setText(Integer.toString(s.likesCounter));

            final TextView commentsCounterTextView = (TextView) myView.findViewById(R.id.commentsCounterTextView);
            commentsCounterTextView.setText(Integer.toString(s.commentsCounter));

            LinearLayout likeLinearLayout = (LinearLayout) myView.findViewById(R.id.likeLinearLayout);
            //likeLinearLayout.setTag(s.getPostId());
            if (s.isLike()) {
                ((TextView) myView.findViewById(R.id.likeTextView)).setTextColor(Color.parseColor("#00CEFF"));
                ((ImageView) myView.findViewById(R.id.likeImageView)).setImageResource(R.drawable.liked_logo);
            }
            likeLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseObject currentPost = null;
                    List<String> postLikes;

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
                    query.whereEqualTo("objectId", s.getPostId());
                    try {
                        List<ParseObject> currentPostList = query.find();
                        if (currentPostList.size() > 0) {
                            currentPost = currentPostList.get(0);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (currentPost == null) {
                        Toast.makeText(PostsActivity.this, "This post have been deleted", Toast.LENGTH_SHORT).show();
                        postsArrayList.remove(position);
                        myAdapter.notifyDataSetChanged();
                    } else if (!s.isLike() && currentPost != null) {
                        if (currentPost.getList("postLikes") == null) {
                            postLikes = new ArrayList<String>();
                            postLikes.add(ParseUser.getCurrentUser().getString("ID"));
                            currentPost.put("postLikes", postLikes);
                            updateLike(true, myView);
                            likesCounterTextView.setText(Integer.toString(Integer.parseInt(likesCounterTextView.getText().toString()) + 1));
                            s.likesCounter++;
                            s.setLike(true);
                        } else {
                            postLikes = currentPost.getList("postLikes");
                            postLikes.add(ParseUser.getCurrentUser().getString("ID"));
                            currentPost.put("postLikes", postLikes);
                            updateLike(true, myView);
                            likesCounterTextView.setText(Integer.toString(Integer.parseInt(likesCounterTextView.getText().toString()) + 1));
                            s.likesCounter++;
                            s.setLike(true);
                        }
                        currentPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(PostsActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i("Server Error", e.toString());
                                    Toast.makeText(PostsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        postLikes = currentPost.getList("postLikes");
                        postLikes.remove(ParseUser.getCurrentUser().getString("ID"));
                        currentPost.put("postLikes", postLikes);
                        updateLike(false, myView);
                        likesCounterTextView.setText(Integer.toString(Integer.parseInt(likesCounterTextView.getText().toString()) - 1));
                        s.likesCounter--;
                        s.setLike(false);
                        currentPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(PostsActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i("Server Error", e.toString());
                                    Toast.makeText(PostsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            LinearLayout commentLinearLayout = (LinearLayout) myView.findViewById(R.id.commentLinearLayout);
            commentLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseQuery<ParseObject> mSureQuery = ParseQuery.getQuery("Posts");
                    mSureQuery.whereEqualTo("objectId", s.getPostId());
                    try {
                        List<ParseObject> currentPostList = mSureQuery.find();
                        if (currentPostList.size() > 0) {
                            commentsDialog.setContentView(R.layout.comments_layout);
                            final ProgressBar commentsProgressBar = (ProgressBar) commentsDialog.findViewById(R.id.commentsProgressBar);
                            final TextView noCommentsTextView = (TextView) commentsDialog.findViewById(R.id.noCommentsTextView);
                            commentsDialog.show();
                            commentsArrayList = new ArrayList<CommentAdapter>();
                            final MyCommentsAdapter myCommentsAdapter = new MyCommentsAdapter(commentsArrayList);
                            final Button commentSendButton = (Button) commentsDialog.findViewById(R.id.commentSendButton);
                            final EditText commentEditText = (EditText) commentsDialog.findViewById(R.id.commentEditText);
                            ListView commentsListView = (ListView) commentsDialog.findViewById(R.id.commentsListView);

//                            commentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    Log.i("position", position + "");
//                                    Log.i("positionx", commentsArrayList.size() + "");
//                                }
//                            });

                            commentsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                    if (commentsArrayList.get(position).getUserID().equals(ParseUser.getCurrentUser().getString("ID"))) {

                                        deleteCommentAlert = new AlertDialog.Builder(PostsActivity.this)
                                                .setTitle("Delete Comment").setMessage("Are you sure that you want to delete this Comment?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ParseQuery.getQuery("Comments").whereEqualTo("objectId", commentsArrayList.get(position).getCommentID()).findInBackground(new FindCallback<ParseObject>() {
                                                            @Override
                                                            public void done(List<ParseObject> objects, ParseException e) {
                                                                if (e == null && objects.size() > 0) {
                                                                    ParseObject deletedComment = objects.get(0);
                                                                    deletedComment.deleteInBackground(new DeleteCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                Toast.makeText(PostsActivity.this, "Comment Deleted", Toast.LENGTH_SHORT).show();
                                                                                commentsArrayList.remove(position);
                                                                                commentsCounterTextView.setText(Integer.toString(Integer.parseInt(commentsCounterTextView.getText().toString()) - 1));
                                                                                s.commentsCounter--;
                                                                                myCommentsAdapter.notifyDataSetChanged();
                                                                                if (commentsArrayList.size() == 0) {
                                                                                    noCommentsTextView.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        deleteCommentAlert.dismiss();
                                                    }
                                                }).create();

                                        deleteCommentAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialog) {
                                                deleteCommentAlert.getButton(deleteCommentAlert.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                                                deleteCommentAlert.getButton(deleteCommentAlert.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
                                            }
                                        });

                                        deleteCommentAlert.show();
                                    }

                                    return false;
                                }
                            });

                            commentEditText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (s.length() > 0) {
                                        commentSendButton.setVisibility(View.VISIBLE);
                                    } else {
                                        commentSendButton.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });
                            commentSendButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final ParseObject comment = new ParseObject("Comments");
                                    comment.put("userID", ParseUser.getCurrentUser().getString("ID"));
                                    comment.put("postID", s.getPostId());
                                    comment.put("commentContext", commentEditText.getText().toString());
                                    comment.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                CommentAdapter currentUserCommentAdapter = new CommentAdapter();
                                                ParseUser currentUser = ParseUser.getCurrentUser();
                                                ParseFile userImageFile = currentUser.getParseFile("profileImage");
                                                try {
                                                    byte[] data = userImageFile.getData();
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                    currentUserCommentAdapter.setUserImageBitmap(bitmap);
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }

                                                currentUserCommentAdapter.setUserID(currentUser.getString("ID"));
                                                currentUserCommentAdapter.setCommentID(comment.getObjectId());
                                                currentUserCommentAdapter.setUserName(currentUser.getString("firstName") + " " + currentUser.getString("lastName"));
                                                currentUserCommentAdapter.setUserClassAndCommentDate(currentUser.getString("class"));
                                                currentUserCommentAdapter.setCommentContext(commentEditText.getText().toString());
                                                commentsCounterTextView.setText(Integer.toString(Integer.parseInt(commentsCounterTextView.getText().toString()) + 1));
                                                s.commentsCounter++;
                                                commentEditText.setText("");
                                                commentsProgressBar.setVisibility(View.GONE);
                                                noCommentsTextView.setVisibility(View.GONE);
                                                commentsArrayList.add(currentUserCommentAdapter);
                                                myCommentsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                                }
                            });

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
                            query.whereEqualTo("postID", s.getPostId());
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() > 0) {
                                            for (ParseObject object : objects) {
                                                CommentAdapter commentAdapter = new CommentAdapter();

                                                commentAdapter.setCommentID(object.getObjectId());

                                                ParseQuery<ParseUser> userInfoQuery = ParseUser.getQuery();

                                                commentAdapter.setUserID(object.getString("userID"));

                                                userInfoQuery.whereEqualTo("ID", object.getString("userID"));
                                                try {
                                                    List<ParseUser> parseUsers = userInfoQuery.find();

                                                    ParseUser currentUser = parseUsers.get(0);

                                                    ParseFile userProfileImageFile = currentUser.getParseFile("profileImage");
                                                    try {
                                                        byte[] data = userProfileImageFile.getData();
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                        commentAdapter.setUserImageBitmap(bitmap);
                                                    } catch (ParseException e1) {
                                                        e1.printStackTrace();
                                                    }

                                                    commentAdapter.setUserName(currentUser.getString("firstName") + " " + currentUser.getString("lastName"));
                                                    commentAdapter.setUserClassAndCommentDate(currentUser.getString("class"));

                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                                commentAdapter.setCommentContext(object.getString("commentContext"));
                                                commentsArrayList.add(commentAdapter);
                                            }
                                            commentsProgressBar.setVisibility(GONE);
                                            myCommentsAdapter.notifyDataSetChanged();
                                        } else if (objects.size() == 0) {
                                            commentsProgressBar.setVisibility(View.GONE);
                                            noCommentsTextView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            });
                            commentsListView.setAdapter(myCommentsAdapter);

                        } else {
                            Toast.makeText(PostsActivity.this, "This post have been deleted", Toast.LENGTH_SHORT).show();
                            postsArrayList.remove(position);
                            myAdapter.notifyDataSetChanged();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            });


            return myView;
        }

        public void updateLike(boolean doLike, View myView) {
            if (doLike) {
                ((TextView) myView.findViewById(R.id.likeTextView)).setTextColor(Color.parseColor("#00CEFF"));
                ((ImageView) myView.findViewById(R.id.likeImageView)).setImageResource(R.drawable.liked_logo);
            } else {
                ((TextView) myView.findViewById(R.id.likeTextView)).setTextColor(Color.parseColor("#FFFFFF"));
                ((ImageView) myView.findViewById(R.id.likeImageView)).setImageResource(R.drawable.like_logo);
            }
        }
    }

    private class MyCommentsAdapter extends BaseAdapter {
        public ArrayList<CommentAdapter> listNewsDataAdapter;

        public MyCommentsAdapter(ArrayList<CommentAdapter> listNewsDataAdapter) {
            this.listNewsDataAdapter = listNewsDataAdapter;
        }


        @Override
        public int getCount() {
            return listNewsDataAdapter.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.comment_ticket, null);

            final CommentAdapter s = listNewsDataAdapter.get(position);

            CircleImageView userImageView = (CircleImageView) myView.findViewById(R.id.userImageView);
            userImageView.setImageBitmap(s.userImageBitmap);

            TextView userNameTextView = (TextView) myView.findViewById(R.id.userNameTextView);
            userNameTextView.setText(s.userName);

            TextView userClassAndPostDateTextView = (TextView) myView.findViewById(R.id.userClassAndCommentDateTextView);
            userClassAndPostDateTextView.setText(s.userClassAndCommentDate);

            TextView commentContext = (TextView) myView.findViewById(R.id.commentContextTextView);
            commentContext.setText(s.commentContext);

            return myView;
        }

    }
}
