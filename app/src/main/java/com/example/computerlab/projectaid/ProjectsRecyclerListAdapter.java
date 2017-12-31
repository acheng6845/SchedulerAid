package com.example.computerlab.projectaid;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by achen on 4/29/2017.
 */

public class ProjectsRecyclerListAdapter extends
        RecyclerView.Adapter<ProjectsRecyclerListAdapter.ViewHolder> {

    private ArrayList<String[]> dataset;

    public ProjectsRecyclerListAdapter(ArrayList<String[]> dataset) {
        this.dataset = dataset;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate
                (R.layout.projects_recycler_list_linear_layout, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] projectInformation = dataset.get(position);
        if (projectInformation != null) {
            TextView projectNameTextView =
                    (TextView) holder.layout.findViewById(R.id.project_name_text_view);
            TextView projectStartDateTextView =
                    (TextView) holder.layout.findViewById(R.id.project_start_time_text_view);
            TextView projectEndDateTextView =
                    (TextView) holder.layout.findViewById(R.id.project_end_time_text_view);
            Button editProjectButton =
                    (Button) holder.layout.findViewById(R.id.edit_project_button);
            Button goToProjectButton =
                    (Button) holder.layout.findViewById(R.id.go_to_project_button);
            ImageButton deleteProjectButton =
                    (ImageButton) holder.layout.findViewById(R.id.deleteProjectButton);

            projectNameTextView.setText(projectInformation[1]);
            projectStartDateTextView.setText(String.format(Locale.ENGLISH,
                    "Start Time: %s",projectInformation[2]));
            projectEndDateTextView.setText(String.format(Locale.ENGLISH,
                    "Finish Time: %s", projectInformation[3]));

            goToProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HelperSingleton singleton =
                            HelperSingleton.getInstance(holder.layout.getContext());
                    singleton.setProject(projectInformation[0]);
                    holder.layout.getContext().startActivity(new Intent(holder.layout.getContext(),
                            PathViewerActivity.class));
                }
            });

            editProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateProjectActivity.updateId = projectInformation[0];
                    holder.layout.getContext().startActivity(new Intent(holder.layout.getContext(),
                            CreateProjectActivity.class));
                }
            });

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(holder.layout.getContext());
            dialogBuilder.setMessage("Are you sure you want to delete this project?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HelperSingleton singleton = HelperSingleton.getInstance(
                                    holder.layout.getContext());
                            singleton.projectHelper.deleteProject(projectInformation[0]);
                            Toast.makeText(holder.layout.getContext(), "Project deleted.", Toast.LENGTH_SHORT).show();
                            holder.layout.getContext().startActivity(new Intent(
                                    holder.layout.getContext(), ProjectActivity.class
                            ));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            deleteProjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBuilder.create().show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout;

        private ViewHolder(LinearLayout itemView) {
            super(itemView);
            layout = itemView;
        }
    }
}
