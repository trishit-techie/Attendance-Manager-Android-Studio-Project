package com.example.attendancemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {

    ListView listView;
    final ArrayList<String> subjects = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    static ParseObject attendanceRecord;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.attendance_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.subject){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add a Subject");
            final EditText subjectEditText = new EditText(this);
            builder.setView(subjectEditText);
            builder.setPositiveButton("Add subject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    attendanceRecord = new ParseObject("AttendanceRecord");
                    attendanceRecord.put("username", ParseUser.getCurrentUser().getUsername());
                    attendanceRecord.put("subject", subjectEditText.getText().toString());
                    attendanceRecord.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText(UserDashboardActivity.this,"Subject added successfully", Toast.LENGTH_SHORT).show();
                                subjects.add(subjectEditText.getText().toString());
                                arrayAdapter.notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(UserDashboardActivity.this, "Sorry! Subject could not be added", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("Info","I dont want to add any subject");
                }
            });
            builder.show();


        }
        else if(item.getItemId() == R.id.logout){
            ParseUser.logOut();
            Toast.makeText(UserDashboardActivity.this, "You have successfully logged out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        setTitle(ParseUser.getCurrentUser().getUsername()+"'s Dashboard");
        listView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,subjects);
        listView.setAdapter(arrayAdapter);
        //showSubjectList();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("AttendanceRecord");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size()>0){
                    for(ParseObject object:objects){
                        subjects.add(String.valueOf(object.getString("subject")));
                    }
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
                else if(e == null && objects.size()==0){
                    Toast.makeText(UserDashboardActivity.this,"It seems you have not yet added any subjects in your dashboard", Toast.LENGTH_LONG).show();
                }
                else{
                    e.printStackTrace();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AttendanceRecordActivity.class);
                intent.putExtra("subjectName", subjects.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int subjectToBeDeleted = position;
                new AlertDialog.Builder(UserDashboardActivity.this)
                           .setIcon(android.R.drawable.ic_delete)
                           .setTitle("Delete subject")
                           .setMessage("Are you sure you want to permanently delete this subject?")
                           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(UserDashboardActivity.this, "Subject deleted successfully", Toast.LENGTH_SHORT).show();

                                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("AttendanceRecord");
                                    query1.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
                                    query1.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if(e == null && objects.size()>0){
                                                for(ParseObject object1:objects){
                                                    if(String.valueOf(object1.getString("subject")).equals(subjects.get(subjectToBeDeleted))){
                                                        try {
                                                            object1.delete();
                                                        } catch (ParseException ex) {
                                                            ex.printStackTrace();
                                                        }

                                                        Log.i("subject to be deleted:", subjects.get(subjectToBeDeleted));
                                                    }
                                                }
                                                subjects.remove(subjectToBeDeleted);
                                                arrayAdapter.notifyDataSetChanged();
                                            }
                                            else{
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                               }
                           })
                           .setNegativeButton("No", null)
                           .show();

                return true;
            }
        });

    }
}
