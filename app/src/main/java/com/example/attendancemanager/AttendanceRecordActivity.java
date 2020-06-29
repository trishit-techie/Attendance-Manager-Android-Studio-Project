package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class AttendanceRecordActivity extends AppCompatActivity {

    Button totalClassesButton;
    Button daysPresentButton;
    TextView attendancePercentageTextView;
    int totalClasses = 0;
    int presentClasses = 0;
    int percentage = 0;
    String subjectName;
    public void updateRecord(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("subjectName", subjectName);
        query.orderByDescending("updatedAt");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null){
                    totalClasses = Integer.parseInt(String.valueOf(object.get("totalNoOfDays")));
                    presentClasses = Integer.parseInt(String.valueOf(object.get("noOfDaysPresent")));
                    percentage = Integer.parseInt(String.valueOf(object.get("attendancePercentage")));
                    totalClassesButton.setText(String.valueOf(object.get("totalNoOfDays")));
                    daysPresentButton.setText(String.valueOf(object.get("noOfDaysPresent")));
                    attendancePercentageTextView.setText("You have "+String.valueOf(object.get("attendancePercentage"))+"% attendance");
                }
                else if(e == null && object == null){
                    totalClassesButton.setText("0");
                    daysPresentButton.setText("0");
                    attendancePercentageTextView.setText("You have 0% attendance");
                }
                else{
                    e.printStackTrace();
                }
            }
        });

    }

    public void saveRecord()
    {
        ParseObject record = new ParseObject(ParseUser.getCurrentUser().getUsername());
        record.put("subjectName", subjectName);
        record.put("totalNoOfDays", totalClasses);
        record.put("noOfDaysPresent",presentClasses);
        record.put("attendancePercentage", percentage);
        record.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(AttendanceRecordActivity.this, "Attendance record has been saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    e.printStackTrace();
                }
            }
        });
    }
    public void markPresent(View view) {
        totalClasses++;
        presentClasses++;
        percentage = (presentClasses * 100) / totalClasses;
        totalClassesButton.setText(Integer.toString(totalClasses));
        daysPresentButton.setText(Integer.toString(presentClasses));
        attendancePercentageTextView.setText("You have "+Integer.toString(percentage)+"% attendance");

        saveRecord();
    }

    public void markAbsent(View view) {
        totalClasses++;
        percentage = (presentClasses * 100) / totalClasses;
        totalClassesButton.setText(Integer.toString(totalClasses));
        daysPresentButton.setText(Integer.toString(presentClasses));
        attendancePercentageTextView.setText("You have "+Integer.toString(percentage)+"% attendance");

        saveRecord();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_record);
        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
        setTitle(subjectName);
        totalClassesButton = (Button) findViewById(R.id.totalClassesButton);
        daysPresentButton = (Button) findViewById(R.id.daysPresentButton);
        attendancePercentageTextView = (TextView) findViewById(R.id.attendancePercentageTextView);

         updateRecord();

    }
}
