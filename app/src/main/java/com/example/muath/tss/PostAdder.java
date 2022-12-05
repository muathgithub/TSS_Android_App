package com.example.muath.tss;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class PostAdder extends AppCompatActivity {

    EditText postTitleEditText;
    EditText postContextEditText;
    Button addPictureButton;
    Button postButton;
    ParseFile postImageFile = null;
    boolean isToEdit;
    ParseObject editPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_adder);

        getSupportActionBar().hide();

        postTitleEditText = (EditText) findViewById(R.id.postTitleEditText);
        postContextEditText = (EditText) findViewById(R.id.postContextEditText);
        addPictureButton = (Button) findViewById(R.id.addPictureButton);
        postButton = (Button) findViewById(R.id.postButton);

        isToEdit = false;
        Intent toEditIntent = getIntent();
        isToEdit = toEditIntent.getBooleanExtra("isToEdit", false);

        if (isToEdit) {

            Log.i("arrive", "isToEdit");
            postButton.setText("Save Changes");
            //postButton.setEnabled(false);
            ParseQuery.getQuery("Posts").whereEqualTo("objectId", toEditIntent.getStringExtra("postID")).findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    Toast.makeText(PostAdder.this, objects.size() + "", Toast.LENGTH_SHORT).show();

                    if (e == null && objects.size() > 0) {

                        Toast.makeText(PostAdder.this, "Im Here", Toast.LENGTH_SHORT).show();
                        editPost = objects.get(0);

                        postTitleEditText.setText(editPost.getString("postTitle"));
                        postContextEditText.setText(editPost.getString("postContext"));

                        if (editPost.getParseFile("postImage") != null) {
                            addPictureButton.setText("Click to (Change) / Hold to (Delete)");
                            postImageFile = editPost.getParseFile("postImage");
                        }

                        postButton.setEnabled(true);
                    }
                }
            });


        }

        addPictureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (isToEdit) {
                    if (editPost.getParseFile("postImage") != null) {
                        editPost.remove("postImage");
                        postImageFile = null;
                        addPictureButton.setText("Add Picture");

                    }
                }

                if (postImageFile != null) {
                    postImageFile = null;
                    addPictureButton.setText("Add Picture");
                }

                return false;
            }
        });

    }

    public void addPicture(View view) {
        getPhoto();
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            addPictureButton.setText("Click to (Change) / Hold to (Delete)");
            Uri selectedImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] byteArray = stream.toByteArray();

                postImageFile = new ParseFile("image.PNG", byteArray);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void saveThePost(View view) {

        if (!postTitleEditText.getText().toString().equals("") || !postContextEditText.getText().toString().equals("") || postImageFile != null) {

            postTitleEditText.setEnabled(false);
            postContextEditText.setEnabled(false);
            addPictureButton.setEnabled(false);
            postButton.setEnabled(false);

            final Dialog progressDialog= new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_DarkActionBar);

//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setInverseBackgroundForced(false);
//            progressDialog.setMessage("Uploading. Please wait...");
//            progressDialog.show();
                    //show(PostAdder.this, "",
                  //  "Uploading. Please wait...", true);


            ParseObject postObject;

            if (!isToEdit) {
                postObject = new ParseObject("Posts");
            } else {
                postObject = editPost;
            }

            postObject.put("userID", ParseUser.getCurrentUser().getString("ID"));

            //if (!postTitleEditText.getText().toString().equals("")) {
            postObject.put("postTitle", postTitleEditText.getText().toString());
            //}

            //if (!postContextEditText.getText().toString().equals("")) {
            postObject.put("postContext", postContextEditText.getText().toString());
            //}

            if (postImageFile != null) {
                postObject.put("postImage", postImageFile);
            }

            postObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        goToPostsActivity();
                        progressDialog.hide();
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        goToPostsActivity();
    }

    public void goToPostsActivity() {
        Intent intent = new Intent(getApplicationContext(), PostsActivity.class);
        startActivity(intent);
        finish();
    }
}
