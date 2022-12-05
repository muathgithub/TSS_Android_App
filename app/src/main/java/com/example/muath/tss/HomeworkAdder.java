package com.example.muath.tss;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.thomashaertel.widget.MultiSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HomeworkAdder extends AppCompatActivity {

    Spinner subjectsAdderSpinner;
    MultiSpinner gradesAdderMultiSpinner;
    EditText homeworkContextEditText;
    Button addHomeworkButton;
    TextView deadlineDateTextView;
    boolean isToEdit;
    ArrayList<String> forGradesArrayList;
    String chosenSubject = "Chose a Subject";
    String[] subjects = {"Chose a Subject", "Arabic", "Hebrew", "English", "History", "Civilizations", "Math", "Physics", "Chemistry", "Biology", "Religion", "Scientific research"};
    final String[] grades = {"All", "12-A", "12-B", "12-C", "11-A", "11-B", "11-C", "10-A", "10-B", "10-C", "9-A", "9-B", "9-C"};
    ParseObject editHomework;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_adder);

        deadlineDateTextView = (TextView) findViewById(R.id.deadlineDateTextView);
        homeworkContextEditText = (EditText) findViewById(R.id.homeworkContextEditText);
        addHomeworkButton = (Button) findViewById(R.id.addHomeworkButton);

        isToEdit = false;
        Intent toEditIntent = getIntent();
        isToEdit = toEditIntent.getBooleanExtra("isToEdit", false);

        forGradesArrayList = new ArrayList<String>();

        for (int i = 1; i < grades.length; i++) {
            forGradesArrayList.add(grades[i]);
        }

        //Log.i("theArray", Arrays.toString(forGradesArrayList.toArray()));


        subjectsAdderSpinner = (Spinner) findViewById(R.id.subjectsAdderSpinner);
        ArrayAdapter<String> subjectsArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, subjects);
        subjectsAdderSpinner.setAdapter(subjectsArrayAdapter);
        subjectsAdderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenSubject = subjects[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gradesAdderMultiSpinner = (MultiSpinner) findViewById(R.id.gradesAdderMultiSpinner);
        ArrayAdapter<String> gradesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, grades);
        gradesAdderMultiSpinner.setAdapter(gradesArrayAdapter, false, new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
               // forGradesArrayList.clear();

                if (selected[0] == true) {
                    Log.i("theArrayX", "Here3");
                    forGradesArrayList.clear();
                    for (int i = 1; i < selected.length; i++) {
                        selected[i] = false;
                        forGradesArrayList.add(grades[i]);
                        Log.i("theArrayX", " " + selected.length);
                    }
                    //Log.i("theArrayX", Arrays.toString(forGradesArrayList.toArray()) + "\n" + Arrays.toString(selected));
                    gradesAdderMultiSpinner.setSelected(selected);
                    return;
                }

                for (int i = 0; i < selected.length; i++) {
                    if (selected[i] == true && !forGradesArrayList.contains(grades[i])) {
                        forGradesArrayList.add(grades[i]);
                        //Log.i("theArray", Arrays.toString(forGradesArrayList.toArray()) + "\n" + Arrays.toString(selected));
                        continue;
                    } else if (selected[i] == false && forGradesArrayList.contains(grades[i])) {
                        forGradesArrayList.remove(grades[i]);
                        //Log.i("theArray", Arrays.toString(forGradesArrayList.toArray()) + "\n" + Arrays.toString(selected));
                    }
                }
            }
        });

        if (isToEdit) {

            //Log.i("arrive", "isToEdit");
            addHomeworkButton.setText("Save Changes");
            addHomeworkButton.setEnabled(false);
            ParseQuery.getQuery("Homework").whereEqualTo("objectId", toEditIntent.getStringExtra("HomeworkID")).findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    //  Toast.makeText(HomeworkAdder.this, objects.size() + "", Toast.LENGTH_SHORT).show();

                    if (e == null && objects.size() > 0) {

                        // Toast.makeText(PostAdder.this, "Im Here", Toast.LENGTH_SHORT).show();
                        editHomework = objects.get(0);

                        homeworkContextEditText.setText(editHomework.getString("homeworkContext"));
                        deadlineDateTextView.setText(editHomework.getString("deadline"));
                        subjectsAdderSpinner.setPrompt(editHomework.getString("subject"));
                        subjectsAdderSpinner.setSelection(Arrays.asList(subjects).indexOf(editHomework.getString("subject")));

                        List<String> homeworkGradesList = editHomework.getList("grades");

                        boolean[] chosenGrades = new boolean[grades.length];

                        for(int i = 0; i < chosenGrades.length; i++){
                            chosenGrades[i] = false;
                        }

                        Log.i("theArrayX", Arrays.toString(chosenGrades) + homeworkGradesList.size() + grades.length);


                        if (homeworkGradesList.size() == (grades.length - 1)) {
                            chosenGrades[0] = true;
                            Log.i("theArrayX", "Here1");
                        } else {
                            List<String> gradesList = Arrays.asList(grades);
                            Log.i("theArrayX", "Here2");

                            for (String grade : homeworkGradesList) {
                                chosenGrades[gradesList.indexOf(grade)] = true;
                            }
                        }

                        Log.i("theArray", Arrays.toString(chosenGrades));


                        gradesAdderMultiSpinner.setSelected(chosenGrades);


                        addHomeworkButton.setEnabled(true);
                    }
                }
            });


        } else {

            boolean[] selectedItems = new boolean[grades.length];
            selectedItems[0] = true;
            gradesAdderMultiSpinner.setSelected(selectedItems);
            forGradesArrayList.add(grades[0]);
        }

    }


    @Override
    public void onBackPressed() {
        goToHomeworkActivity();
    }

    public void setDeadline(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            datePickerDialog.show();

            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    deadlineDateTextView.setText(dayOfMonth + "." + (month + 1) + "." + year);
                }
            });
        }
    }

    public void uploadHomework(View view) {
        if (!chosenSubject.equals("Chose a Subject")
                && !homeworkContextEditText.getText().toString().equals("")
                && !deadlineDateTextView.getText().toString().equals("Click to set a Deadline")) {

            subjectsAdderSpinner.setEnabled(false);
            gradesAdderMultiSpinner.setEnabled(false);
            homeworkContextEditText.setEnabled(false);
            deadlineDateTextView.setEnabled(false);
            Log.i("fuck", forGradesArrayList.toString());

            if(isToEdit){
                Log.i("fuck", forGradesArrayList.toString());
                editHomework.put("subject", chosenSubject);
                editHomework.put("grades", forGradesArrayList);
                editHomework.put("homeworkContext", homeworkContextEditText.getText().toString());
                editHomework.put("deadline", deadlineDateTextView.getText().toString());
                editHomework.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null)
                        {
                            Log.i("fuck", forGradesArrayList.toString());
                            Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(HomeworkAdder.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return;
            }

            ParseObject homeworkParseObject = new ParseObject("Homework");
            homeworkParseObject.put("userID", ParseUser.getCurrentUser().getString("ID"));
            homeworkParseObject.put("subject", chosenSubject);
            homeworkParseObject.put("grades", forGradesArrayList);
            homeworkParseObject.put("homeworkContext", homeworkContextEditText.getText().toString());
            homeworkParseObject.put("deadline", deadlineDateTextView.getText().toString());

            homeworkParseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(HomeworkAdder.this, "Homework Uploaded", Toast.LENGTH_LONG).show();
                        goToHomeworkActivity();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Fill all The Options Please", Toast.LENGTH_LONG).show();
        }
    }

    public void goToHomeworkActivity() {
        Intent intent = new Intent(this, HomeworkActivity.class);
        startActivity(intent);
        finish();
    }
}
