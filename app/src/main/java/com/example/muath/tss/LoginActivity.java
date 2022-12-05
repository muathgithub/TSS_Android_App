package com.example.muath.tss;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener{
    EditText usernameEditText;
    EditText userPasswordEditText;
    WebView fAndGWebView;
    boolean isWebViewVisible = false;
    ImageView sFImageView;
    ImageView sGImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        userPasswordEditText = (EditText) findViewById(R.id.userPasswordEditText);
        sFImageView = (ImageView) findViewById(R.id.sFImageView);
        sFImageView.setOnClickListener(this);
        sGImageView = (ImageView) findViewById(R.id.sGImageView);
        sGImageView.setOnClickListener(this);
        fAndGWebView = (WebView) findViewById(R.id.fAndGWebView);




    }

    public void login(View view) {
        final String username = usernameEditText.getText().toString().toLowerCase();
        final String password = userPasswordEditText.getText().toString();

        if (username.length() == 10 && Character.isLetter(username.charAt(0)) && !password.equals("")) {
            final char userType = Character.toLowerCase(username.charAt(0));
            if(userType != 't' && userType != 's'){
                Toast.makeText(getApplicationContext(), "User Type Error", Toast.LENGTH_LONG).show();
                return;
            }

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null && user != null) {
                        if (user.getEmail() == null) {
                            Intent intent = new Intent(getApplicationContext(), UserInfo.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == sFImageView.getId()){
            fAndGWebView.setVisibility(View.VISIBLE);
            isWebViewVisible = true;
            fAndGWebView.loadUrl("https://www.facebook.com/tsjerusalem/");
            fAndGWebView.getSettings().setJavaScriptEnabled(true);
            fAndGWebView.setWebViewClient(new WebViewClient());
            fAndGWebView.setWebChromeClient(new WebChromeClient());
        } else if (v.getId() == sGImageView.getId()){
            Intent sendMessageIntent= new Intent(Intent.ACTION_SEND);
            sendMessageIntent.setType("message/rfc822");
            sendMessageIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"tss@gmail.com"});
            try {
                startActivity(Intent.createChooser(sendMessageIntent, "toSchoolMessage"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(LoginActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(isWebViewVisible == true){
            fAndGWebView.stopLoading();
            fAndGWebView.setVisibility(View.GONE);
            isWebViewVisible = false;
        } else {
            finish();
        }
    }
}
