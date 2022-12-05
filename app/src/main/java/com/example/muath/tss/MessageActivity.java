package com.example.muath.tss;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MessageActivity extends AppCompatActivity {

    EditText messageTitleEditText;
    EditText messageContextEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().hide();

        messageTitleEditText = (EditText) findViewById(R.id.messageTitleEditText);
        messageContextEditText = (EditText) findViewById(R.id.messageContextEditText);


    }

    public void sendMessage(View view) {

        if (messageTitleEditText.getText().toString().equals("") && messageContextEditText.getText().toString().equals("")){
            Toast.makeText(this, "Fill At Least One Field", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent sendMessageIntent= new Intent(Intent.ACTION_SEND);
        sendMessageIntent.setType("message/rfc822");
        sendMessageIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"tss@gmail.com"});
        sendMessageIntent.putExtra(Intent.EXTRA_SUBJECT, messageTitleEditText.getText().toString());
        sendMessageIntent.putExtra(Intent.EXTRA_TEXT   , messageContextEditText.getText().toString());
        try {
            startActivity(Intent.createChooser(sendMessageIntent, "muathsoftware@gmail.com"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MessageActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
