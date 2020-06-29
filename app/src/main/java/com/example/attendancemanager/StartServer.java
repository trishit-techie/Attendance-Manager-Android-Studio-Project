package com.example.attendancemanager;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Application;
import android.util.Log;

import java.text.ParseException;

public class StartServer extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("7OcvjBxLEwviiausH1SjcV3Pb1GG7T1vj8mGkpO4")
                .clientKey("3j8z2AvMtlCprCgPvtrLP6WlvHrMFpxMrFpfWjiK")
                .server("https://parseapi.back4app.com")
                .build()
        );

       /* ParseObject object = new ParseObject("ExampleObject");
    object.put("myNumber", "1395");
    object.put("myString", "trishit");

    object.saveInBackground(new SaveCallback() {
        @Override
        public void done(com.parse.ParseException e) {
            if (e == null) {
                Log.i("Parse Result", "Successful!");
            } else {
                Log.i("Parse Result", "Failed" + e.toString());
            }
        }
    });

        */

         // ParseUser.enableAutomaticUser();
    }
}
