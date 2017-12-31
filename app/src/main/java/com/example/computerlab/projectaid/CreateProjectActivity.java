package com.example.computerlab.projectaid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class CreateProjectActivity extends AppCompatActivity {

    private EditText projectNameEditText;
    private DatePicker projectStartDatePicker;
    private Button insertProjectButton;
    public static String updateId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        HelperSingleton singleton = HelperSingleton.getInstance(this);

        projectNameEditText = (EditText) findViewById(R.id.project_name_edit_text);
        projectStartDatePicker = (DatePicker) findViewById(R.id.project_start_date_picker);
        insertProjectButton = (Button) findViewById(R.id.insert_project_button);

        if (updateId == null) {
            Calendar calendar = Calendar.getInstance();
            //projectStartDatePicker.setMinDate(calendar.getTimeInMillis());

            insertProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Selected date in the form MM/dd/yyy
                    String selectedMonth = projectStartDatePicker.getMonth() + 1 < 10 ?
                            "0" + (projectStartDatePicker.getMonth() + 1) :
                            Integer.toString(projectStartDatePicker.getMonth() + 1);
                    String selectedDate = String.format(Locale.ENGLISH,
                            "%s/%d/%d", selectedMonth, projectStartDatePicker.getDayOfMonth(),
                            projectStartDatePicker.getYear());
                    String id = UUID.randomUUID().toString();
                    //String id = Integer.toString(singleton.projectHelper.getAllProjects().size());
                    singleton.projectHelper.insertProject(
                            id,
                            projectNameEditText.getText().toString(),
                            selectedDate,
                            "TBD",
                            ""
                    );

                    startActivity(new Intent(getApplicationContext(), ProjectActivity.class));
                }
            });
        } else {
            ArrayList<String> project = singleton.projectHelper.getProject(updateId);
            projectNameEditText.setText(project.get(0));
            try {
                projectStartDatePicker.setMinDate(
                        PathsRecyclerListAdapter.dateFormat.parse(project.get(1)).getTime());
            } catch (ParseException e) {
                Log.wtf("Parse Start Date Exception", e.toString());
            }

            insertProjectButton.setText("Update Project");
            insertProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedMonth = projectStartDatePicker.getMonth() + 1 < 10 ?
                            "0" + (projectStartDatePicker.getMonth() + 1) :
                            Integer.toString(projectStartDatePicker.getMonth() + 1);
                    String selectedDate = String.format(Locale.ENGLISH,
                            "%s/%d/%d", selectedMonth, projectStartDatePicker.getDayOfMonth(),
                            projectStartDatePicker.getYear());

                    singleton.projectHelper.updateProject(
                            updateId,
                            projectNameEditText.getText().toString(),
                            selectedDate,
                            project.get(2),
                            project.get(3)
                    );

                    startActivity(new Intent(getApplicationContext(), ProjectActivity.class));
                }
            });
        }
    }
}
