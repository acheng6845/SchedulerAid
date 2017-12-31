package com.example.computerlab.projectaid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;

import java.text.ParseException;
import java.util.Locale;

public class TestNotificationActivity extends AppCompatActivity {

    private ImageButton returnToPathsButton, testNotificationButton;
    private DatePicker testDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_notification);

        returnToPathsButton = (ImageButton) findViewById(R.id.test_return_to_paths_button);
        if(returnToPathsButton != null) {
            returnToPathsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), PathViewerActivity.class));
                }
            });
        }

        testDatePicker = (DatePicker) findViewById(R.id.test_date_picker);
        HelperSingleton singleton = HelperSingleton.getInstance(this);

        testNotificationButton = (ImageButton) findViewById(R.id.test_notifications_button);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        testNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedMonth = testDatePicker.getMonth() + 1 < 10 ?
                        "0" + (testDatePicker.getMonth() + 1) :
                        Integer.toString(testDatePicker.getMonth() + 1);
                String selectedDate = String.format(Locale.ENGLISH,
                        "%s/%d/%d", selectedMonth, testDatePicker.getDayOfMonth(),
                        testDatePicker.getYear());
                try {
                    singleton.date = PathsRecyclerListAdapter.dateFormat.parse(selectedDate).getTime();
                    long daysPassed = PathsRecyclerListAdapter.dateFormat.parse(selectedDate).getTime() /
                            (24 * 60 * 60 * 1000) -
                            PathsRecyclerListAdapter.dateFormat.parse(singleton.startDate).getTime() /
                                    (24 * 60 * 60 * 1000);
                    Log.d("Upcoming Due Date", Long.toString(singleton.closestUpcomingDueDate));
                    if(daysPassed >= singleton.closestUpcomingDueDate ||
                            daysPassed+1 == singleton.closestUpcomingDueDate) {
                        notificationBuilder.setSmallIcon(R.drawable.alarm)
                                .setContentTitle("ProjectAid Notification")
                                .setContentText("A Project Task has its due date approaching!")
                                .setContentIntent(PendingIntent.getActivity(getApplicationContext(),
                                        0, new Intent(getApplicationContext(), PathViewerActivity.class
                                ), 0));
                        notificationManager.notify(1, notificationBuilder.build());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
