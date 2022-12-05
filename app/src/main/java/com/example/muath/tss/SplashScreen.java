package com.example.muath.tss;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.parse.Parse;
import com.parse.ParseUser;

public class SplashScreen extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("")
                .clientKey("")
                .server("")
                .build()
        );

        imageView = (ImageView) findViewById(R.id.logoImageView);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent;
                ParseUser currentUser = ParseUser.getCurrentUser();
                if(currentUser == null) {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    goToNext(intent);
                } else {
                    if(currentUser.getEmail() == null) {
                        ParseUser.logOut();
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        goToNext(intent);
                    } else {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        goToNext(intent);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        imageView.startAnimation(animation);
    }

    public void goToNext(Intent intent){
        startActivity(intent);
        finish();
    }
}
