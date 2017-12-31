package com.example.computerlab.projectaid;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public class PathViewerActivity extends AppCompatActivity {
    private ImageButton createNodeActivityButton, refreshPathButton, returnToProjectsButton;
    private RecyclerView nodesRecyclerList;
    private TextView projectNameTextView, startDateTextView, endDateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_viewer);

        HelperSingleton singleton = HelperSingleton.getInstance(this);

        Set<String> durationsList = singleton.nodeHelper.getAllNodes(singleton.projectId).keySet();

        // RECYCLER VIEW
        nodesRecyclerList = (RecyclerView) findViewById(R.id.nodes_recycler_list);
        //HORIZONTAL
//        LinearLayoutManager layoutManager =
//                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        long endDate = Long.MAX_VALUE;
        try {
            endDate = PathsRecyclerListAdapter.dateFormat.parse(singleton.endDate).getTime();
        } catch (ParseException e) {
            Log.wtf("End Date", "Couldn't parse End Date!");
        }
        endDateTextView = (TextView) findViewById(R.id.path_end_date_text_view);
        if (singleton.date <= endDate) {
            //VERTICAL
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            nodesRecyclerList.setLayoutManager(layoutManager);

            //need to test for if finding critical path on activity creation affects performance on high node counts
            singleton.findCriticalPath();
            if (singleton.criticalPath != null)
                nodesRecyclerList.setAdapter(new PathsRecyclerListAdapter(singleton.criticalPath));
            if(endDateTextView != null) endDateTextView.setText(String.format(Locale.ENGLISH,
                    "End Date: %s",singleton.endDate));
        } else {
            if(endDateTextView != null) endDateTextView.setText(String.format(Locale.ENGLISH, "Ended On: %s", singleton.endDate));
        }

        createNodeActivityButton = (ImageButton) findViewById(R.id.create_node_activity_button);
        if (createNodeActivityButton != null) {
            createNodeActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), CreateNodeActivity.class));
                }
            });
        }

        //REFRESH PATHS BUTTON
        refreshPathButton = (ImageButton) findViewById(R.id.refresh_path_button);
        if (refreshPathButton != null) {
            refreshPathButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Thread refreshPathThread = new Thread() {
                        public void run() {
//                            singleton.findCriticalPath();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    nodesRecyclerList.setAdapter(
//                                            new PathsRecyclerListAdapter(singleton.criticalPath));
//                                }
//                            });
                            startActivity(new Intent(getApplicationContext(), TestNotificationActivity.class));
                        }
                    };
                    refreshPathThread.start();
                }
            });
        }

        //RETURN TO PROJECTS BUTTON
        returnToProjectsButton = (ImageButton) findViewById(R.id.return_to_projects_button);
        if (returnToProjectsButton != null) {
            returnToProjectsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), ProjectActivity.class));
                }
            });
        }

        //TEXT VIEWS
        projectNameTextView = (TextView) findViewById(R.id.path_project_name_text_view);
        if(projectNameTextView != null) projectNameTextView.setText(singleton.projectName);
        startDateTextView = (TextView) findViewById(R.id.path_start_date_text_view);
        if(startDateTextView != null) startDateTextView.setText(String.format(Locale.ENGLISH,
                "Start Date: %s", singleton.startDate));
        //notification managers can mess with any notifications made by this app
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //can cancel notifcations according to button_notification_id from CreateNodeActivity
    }
}
