package com.example.muath.tss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeworkActivity extends AppCompatActivity {

    Spinner subjectsSpinner;
    Spinner gradesSpinner;
    ListView homeworkListView;
    TextView homeworkStatusTextView;
    MyCustomAdapter myAdapter;
    boolean teacherFirstTime = true;
    LinearLayout editAndDeleteLinearLayout;
    int chosenHomeworkPosition;
    String chosenSubject = "All";
    ProgressBar homeworkListViewProgressBar;
    Button goToHomeworkAdderButton;
    String chosenGrade = "Chose A Grade";
    ArrayList<HomeworkAdapter> homeworkArrayList;
    char userType = ParseUser.getCurrentUser().getUsername().charAt(0);
    String[] subjects = {"All", "Arabic", "Hebrew", "English", "History", "Civilizations", "Math", "Physics", "Chemistry", "Biology", "Religion", "Scientific research"};
    String[] grades = {"Chose A Grade", "12-A", "12-B", "12-C", "11-A", "11-B", "11-C", "10-A", "10-B", "10-C", "9-A", "9-B", "9-C"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work);

        getSupportActionBar().setTitle("Homework");

        subjectsSpinner = (Spinner) findViewById(R.id.subjectsSpinner);
        goToHomeworkAdderButton = (Button) findViewById(R.id.goToHomeworkAdderButton);
        gradesSpinner = (Spinner) findViewById(R.id.gradesSpiner);
        editAndDeleteLinearLayout = (LinearLayout) findViewById(R.id.editAndDeleteLinearLayout);
        homeworkStatusTextView = (TextView) findViewById(R.id.homeworkStatusTextView);

        homeworkListView = (ListView) findViewById(R.id.homeworkListView);
        homeworkListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                chosenHomeworkPosition = position;
                if (homeworkArrayList.get(position).getUserID().equals(ParseUser.getCurrentUser().getString("ID"))) {
                    editAndDeleteLinearLayout.setVisibility(View.VISIBLE);
                    goToHomeworkAdderButton.setVisibility(View.GONE);
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && userType == 't') {
            homeworkListView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    editAndDeleteLinearLayout.setVisibility(View.GONE);
                    goToHomeworkAdderButton.setVisibility(View.VISIBLE);
                }
            });
        }

        homeworkListViewProgressBar = (ProgressBar) findViewById(R.id.homeworkListViewProgressBar);
        Drawable progressDrawable = homeworkListViewProgressBar.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.parseColor("#ffffff"), android.graphics.PorterDuff.Mode.SRC_IN);
        homeworkListViewProgressBar.setProgressDrawable(progressDrawable);

        if(userType == 't'){
            gradesSpinner.setVisibility(View.VISIBLE);
            homeworkStatusTextView.setText("Chose a Grade");
            homeworkStatusTextView.setVisibility(View.VISIBLE);
            homeworkListViewProgressBar.setVisibility(View.GONE);
            goToHomeworkAdderButton.setVisibility(View.VISIBLE);
        }


        ArrayAdapter<String> subjectsArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, subjects);
        subjectsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsSpinner.setAdapter(subjectsArrayAdapter);
        subjectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenSubject = subjects[position];
                loadHomework();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> gradesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, grades);
        gradesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradesSpinner.setAdapter(gradesArrayAdapter);
        gradesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    teacherFirstTime = false;
                    homeworkStatusTextView.setVisibility(View.GONE);
                } else {
                    teacherFirstTime = true;
                    homeworkStatusTextView.setText("Chose a Grade");
                    homeworkStatusTextView.setVisibility(View.VISIBLE);
                }
                chosenGrade = grades[position];
                loadHomework();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeworkArrayList = new ArrayList<HomeworkAdapter>();
        myAdapter = new MyCustomAdapter(homeworkArrayList);
        homeworkListView.setAdapter(myAdapter);


    }

    public void addHomework(View view) {
        Intent intent = new Intent(this, HomeworkAdder.class);
        startActivity(intent);
        finish();
    }

    public void editHomework(View view) {
        Intent editHomeworkIntent = new Intent(getApplicationContext(), HomeworkAdder.class);
        editHomeworkIntent.putExtra("HomeworkID", homeworkArrayList.get(chosenHomeworkPosition).getHomeworkID());
        editHomeworkIntent.putExtra("isToEdit", true);
        startActivity(editHomeworkIntent);
        finish();
    }

    public void deleteHomework(View view) {

        final AlertDialog deletePostAlert= new AlertDialog.Builder(HomeworkActivity.this).setTitle("Delete Homework").setMessage("Are you sure that you want to delete this Homework?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i("deletedH0omework", homeworkArrayList.get(chosenHomeworkPosition).getUserID() + "sss");

                        ParseQuery.getQuery("Homework").whereEqualTo("objectId", homeworkArrayList.get(chosenHomeworkPosition).getHomeworkID()).findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                List<ParseObject> deletedHomework = objects;


                                ParseObject.deleteAllInBackground(deletedHomework, new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        homeworkArrayList.remove(chosenHomeworkPosition);
                                        myAdapter.notifyDataSetChanged();
                                        if (homeworkArrayList.size() == 0) {
                                            noHomeworkTextSetter();
                                        }
                                        Toast.makeText(HomeworkActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
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
        public ArrayList<HomeworkAdapter> listNewsDataAdapter;

        public MyCustomAdapter(ArrayList<HomeworkAdapter> listNewsDataAdapter) {
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
            View myView = mInflater.inflate(R.layout.homework_listview_ticket, null);

            final HomeworkAdapter s = listNewsDataAdapter.get(position);

            TextView subjectTextView = (TextView) myView.findViewById(R.id.subjectTextView);
            subjectTextView.setText(s.getSubject());

            TextView homeworkContextTextView = (TextView) myView.findViewById(R.id.homeworkContextTextView);
            homeworkContextTextView.setText(s.getHomeworkContext());

            TextView deadlineTextView = (TextView) myView.findViewById(R.id.deadLineTextView);
            deadlineTextView.setText(s.getDeadline());

            TextView homeworkLastUpdateTextView = (TextView) myView.findViewById(R.id.homeworkLastUpdateTextView);
            homeworkLastUpdateTextView.setText(s.getHomeworkLastUpdate());

            return myView;
        }

    }

    public void loadHomework() {
        homeworkArrayList.clear();

        if((userType == 't' && teacherFirstTime == false) || userType == 's') {
            homeworkListViewProgressBar.setVisibility(View.VISIBLE);
            homeworkStatusTextView.setVisibility(View.GONE);

        }
        ParseQuery<ParseObject> loadHomeworkQuery = new ParseQuery<ParseObject>("Homework").addDescendingOrder("updatedAt");;

        switch (userType) {

            case 's':
                if (chosenSubject.equals("All")) {
                    loadHomeworkQuery.whereContains("grades", ParseUser.getCurrentUser().getString("class")).findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null && objects.size() > 0) {
                                arrangeHomeworkData(objects);
                            }else if (objects.size() == 0){
                                noHomeworkTextSetter();
                            }
                        }
                    });
                } else {
                    loadHomeworkQuery.whereEqualTo("subject", chosenSubject).whereContains("grades", ParseUser.getCurrentUser().getString("class")).findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null && objects.size() > 0) {
                                arrangeHomeworkData(objects);
                            } else if (objects.size() == 0){
                                noHomeworkTextSetter();
                            }
                        }
                    });
                }
                break;

            case 't':
                if(!chosenGrade.equals("Chose A Grade")){
                    if(!chosenSubject.equals("All")){
                        loadHomeworkQuery.whereEqualTo("subject",chosenSubject).whereContains("grades", chosenGrade).findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null && objects.size() > 0) {
                                    arrangeHomeworkData(objects);
                                    homeworkStatusTextView.setVisibility(View.GONE);
                                } else if (objects.size() == 0){
                                    noHomeworkTextSetter();
                                }
                            }
                        });
                    } else {
                        loadHomeworkQuery.whereContains("grades", chosenGrade).findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null && objects.size() > 0) {
                                    arrangeHomeworkData(objects);
                                } else if (objects.size() == 0){
                                    noHomeworkTextSetter();
                                }
                            }
                        });
                    }
                } else {
                    homeworkArrayList.clear();
                    myAdapter.notifyDataSetChanged();
                }

                break;

        }
    }

    public void arrangeHomeworkData(List<ParseObject> objects) {

        for (ParseObject homework : objects) {

            HomeworkAdapter currentHomework = new HomeworkAdapter();

            currentHomework.setHomeworkID(homework.getObjectId());

            currentHomework.setUserID(homework.getString("userID"));

            currentHomework.setSubject(homework.getString("subject"));

            currentHomework.setHomeworkContext(homework.getString("homeworkContext"));

            currentHomework.setDeadline(homework.getString("deadline"));

            String[] homeworkLastUpdateDate = homework.getUpdatedAt().toString().split(" ");

            currentHomework.setHomeworkLastUpdate(
                    homeworkLastUpdateDate[0] +
                            " " + homeworkLastUpdateDate[1] +
                            " " + homeworkLastUpdateDate[2] +
                            " " + homeworkLastUpdateDate[homeworkLastUpdateDate.length - 1]);


            homeworkArrayList.add(currentHomework);
            homeworkListViewProgressBar.setVisibility(View.GONE);
            myAdapter.notifyDataSetChanged();
        }

    }

    public void noHomeworkTextSetter(){
        homeworkStatusTextView.setText("No Homework");
        homeworkListViewProgressBar.setVisibility(View.GONE);
        homeworkStatusTextView.setVisibility(View.VISIBLE);
    }
}
