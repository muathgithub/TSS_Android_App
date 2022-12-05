package com.example.muath.tss;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    String[] options_names = {
            "Last News",
            "Tables",
            "Homework",
            "Message"};

    int[] options_logos_resources = {
            R.drawable.news_logo,
            R.drawable.table_logo,
            R.drawable.homework_logo,
            R.drawable.message_logo};


    CircleImageView profileImage;
    ParseUser currentUser;
    TextView helloUserTextView;
    String userFLName;

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent goToUserInfoIntent = new Intent(getApplicationContext(), UserInfo.class);
            goToUserInfoIntent.putExtra("isLogIn", true);
            startActivity(goToUserInfoIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        final Intent[] intents = {new Intent(this, PostsActivity.class)
                , new Intent(this, TablesActivity.class)
                , new Intent(this, HomeworkActivity.class)
                , new Intent(this, MessageActivity.class)};

        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        helloUserTextView = (TextView) findViewById(R.id.helloUserTextView);

        currentUser = ParseUser.getCurrentUser();

        userFLName = currentUser.getString("firstName") + " " + currentUser.getString("lastName");
        helloUserTextView.setText(userFLName);

        profileImage.setOnClickListener(myOnClickListener);
        helloUserTextView.setOnClickListener(myOnClickListener);

        ParseFile file = currentUser.getParseFile("profileImage");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null && data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    profileImage.setImageBitmap(bitmap);
                }
            }
        });


        MyCustomAdapter myAdapter;
        ArrayList<OptionAdapter> options = new ArrayList<OptionAdapter>();
        ListView optionsListView = (ListView) findViewById(R.id.optionsListView);

        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(intents[position]);
            }
        });

        for (int i = 0; i < options_names.length; i++) {
            options.add(new OptionAdapter(options_names[i], options_logos_resources[i]));
        }

        myAdapter = new MyCustomAdapter(options);
        optionsListView.setAdapter(myAdapter);


    }

    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<OptionAdapter> listNewsDataAdapter;

        public MyCustomAdapter(ArrayList<OptionAdapter> listNewsDataAdapter) {
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
            View myView = mInflater.inflate(R.layout.listview_ticket, null);

            final OptionAdapter s = listNewsDataAdapter.get(position);

            TextView optionTextView = (TextView) myView.findViewById(R.id.optionTextView);
            optionTextView.setText(s.option);

            ImageView optionImageView = (ImageView) myView.findViewById(R.id.optionImageView);
            optionImageView.setImageResource(s.optionImageResource);

            return myView;
        }

    }
}
