package com.example.computerlab.projectaid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

public class ProjectActivity extends AppCompatActivity {

    private RecyclerView projectRecyclerView;
    private ImageButton createProjectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        HelperSingleton singleton = HelperSingleton.getInstance(this);

        projectRecyclerView = (RecyclerView) findViewById(R.id.project_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        projectRecyclerView.setLayoutManager(layoutManager);

        ArrayList<String[]> projects = singleton.projectHelper.getAllProjects();
        if (projects != null) {
            projectRecyclerView.setAdapter(new ProjectsRecyclerListAdapter(projects));
        } else {
            Log.wtf("Projects", "getAllProjects() shouldn't return null!");
        }

        createProjectButton = (ImageButton) findViewById(R.id.create_project_button);
        createProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateProjectActivity.updateId = null;
                startActivity(new Intent(
                        getApplicationContext(), CreateProjectActivity.class
                ));
            }
        });


    }
}
