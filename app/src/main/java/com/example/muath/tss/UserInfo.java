package com.example.muath.tss;

import android.Manifest;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;

import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class UserInfo extends AppCompatActivity {
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText idEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    EditText passwordEditText;
    Spinner classSpinner;
    ParseUser currentUser;
    ImageView userImageView;
    Button saveUserInfoButton;
    AlertDialog enterPasswordAlertDialog;
    ArrayList<EditText> editTextsArrayList;

    AlertDialog deleteCommentAlert;

    boolean isLogIn;
    char userType;
    String _class = "";
    String[] grades = {"", "12-A", "12-B", "12-C", "11-A", "11-B", "11-C", "10-A", "10-B", "10-C", "9-A", "9-B", "9-C"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getSupportActionBar().hide();

        Intent intent = getIntent();

        isLogIn = intent.getBooleanExtra("isLogIn", false);

        currentUser = ParseUser.getCurrentUser();

        userType = intent.getCharExtra("userType", 's');


        EditText[] editTexts = {
                (EditText) findViewById(R.id.firstNameEditText),
                (EditText) findViewById(R.id.lastNameEditText),
                (EditText) findViewById(R.id.idEditText),
                (EditText) findViewById(R.id.emailEditText),
                (EditText) findViewById(R.id.phoneNumberEditText),
                (EditText) findViewById(R.id.passwordEditText)
        };

        editTextsArrayList = new ArrayList<EditText>(Arrays.asList(editTexts));

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        idEditText = (EditText) findViewById(R.id.idEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        classSpinner = (Spinner) findViewById(R.id.classSpinner);
        userImageView = (ImageView) findViewById(R.id.userImageView);
        saveUserInfoButton = (Button) findViewById(R.id.saveUserInfoButton);

        userImageView.setImageResource(R.drawable.user_profile_image);
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        getPhoto();
                    }
                } else {
                    getPhoto();
                }
            }
        });

        if (isLogIn) {
            saveUserInfoButton.setText("Edit");
            for (EditText currentEditText : editTextsArrayList) {
                currentEditText.setEnabled(false);
            }
            userImageView.setEnabled(false);
        }

        if (userType == 's' && !isLogIn) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grades);

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    _class = grades[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            classSpinner.setAdapter(spinnerAdapter);
        } else {
            classSpinner.setVisibility(View.GONE);
        }


        idEditText.setEnabled(false);
        idEditText.setText(currentUser.getString("username").substring(1));
    }

    public void saveUserInfo(View view) {
        if (saveUserInfoButton.getText().toString().toLowerCase().equals("edit")) {

            final EditText passwordInputEditText = new EditText(UserInfo.this);
            passwordInputEditText.setBackgroundColor(Color.GRAY);
            passwordInputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordInputEditText.setGravity(Gravity.CENTER_HORIZONTAL);

            enterPasswordAlertDialog = new AlertDialog.Builder(UserInfo.this)
                    .setTitle("Password")
                    .setMessage("Enter The Password").setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), passwordInputEditText.getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    //Toast.makeText(UserInfo.this, e.toString(), Toast.LENGTH_SHORT).show();

                                    if (e == null) {
                                        loadUserData();
                                        saveUserInfoButton.setText("Save");
                                        for (EditText currentEditText : editTextsArrayList) {
                                            if (currentEditText != idEditText) {
                                                currentEditText.setEnabled(true);
                                            }
                                        }
                                        userImageView.setEnabled(true);
                                    } else {

                                    }
                                }
                            });
                        }
                    }).create();


            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            lp.setMargins(15, 15, 15, 15);
            passwordInputEditText.setLayoutParams(lp);
            enterPasswordAlertDialog.setView(passwordInputEditText);


            enterPasswordAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    enterPasswordAlertDialog.getButton(enterPasswordAlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                }
            });

            enterPasswordAlertDialog.show();
            return;
        }

        if(userType == 't'){
            _class = "Teacher";
        }

        for (EditText editText : editTextsArrayList) {
            if (editText == passwordEditText && isLogIn) {
                continue;
            }
            if (editText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Fill All The Fields Please", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (!isLogIn && _class == "") {
            Toast.makeText(getApplicationContext(), "Fill All The Fields Please", Toast.LENGTH_LONG).show();
            return;
        }

        if (!passwordEditText.getText().toString().equals("")) {
            currentUser.setPassword(passwordEditText.getText().toString());
        }

        currentUser.setEmail(emailEditText.getText().toString().toLowerCase());
        currentUser.put("firstName", firstNameEditText.getText().toString());
        currentUser.put("lastName", lastNameEditText.getText().toString());
        currentUser.put("ID", idEditText.getText().toString());
        currentUser.put("phoneNumber", phoneNumberEditText.getText().toString());

        if (!isLogIn) {
            currentUser.put("class", _class);
        }

        try {
            currentUser.save();
            Toast.makeText(getApplicationContext(), "Information Have Been Saved", Toast.LENGTH_LONG).show();
            if (isLogIn) {
                ParseUser.logOut();
                Intent goToLogInActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToLogInActivityIntent);
                finish();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        if (!isLogIn) {
            ParseUser.logInInBackground(currentUser.getUsername(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("currentUser", currentUser);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                userImageView.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.PNG", byteArray);

                currentUser.put("profileImage", file);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void logOut(View view) {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent goToLogInActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(goToLogInActivity);
                    finish();
                }
            }
        });
    }

    public void loadUserData() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseFile file = currentUser.getParseFile("profileImage");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null && data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    userImageView.setImageBitmap(bitmap);
                }
            }
        });

        firstNameEditText.setText(currentUser.getString("firstName"));
        lastNameEditText.setText(currentUser.getString("lastName"));
        emailEditText.setText(currentUser.getEmail());
        phoneNumberEditText.setText(currentUser.getString("phoneNumber"));
    }

    @Override
    public void onBackPressed() {
        Intent goToNextIntent;

        if(isLogIn){
            goToNextIntent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            goToNextIntent = new Intent(getApplicationContext(), LoginActivity.class);
            ParseUser.logOut();
        }
        startActivity(goToNextIntent);
        finish();
    }
}
