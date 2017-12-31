package com.example.computerlab.projectaid;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by achen on 4/29/2017.
 */

public class PathsRecyclerListAdapter extends RecyclerView.Adapter<PathsRecyclerListAdapter.ViewHolder> {

    private HashMap<String, ArrayList<String[]>> dataset;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public PathsRecyclerListAdapter(HashMap<String, ArrayList<String[]>> dataset) {
        this.dataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate layout from Activity parent which has a Context to inflate
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate
                (R.layout.path_horizontal_layout, parent, false);
        //holds the view
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView pathInformationTextView =
                (TextView) holder.layout.findViewById(R.id.path_information_text_view);
        TextView pathNameTextView = (TextView) holder.layout.findViewById(R.id.path_name_text_view);
        RecyclerView nodesList = (RecyclerView) holder.layout.findViewById(R.id.nodes_path_list);
        ArrayList<String[]> nodesDataSet = dataset.get("path"+position);
        HelperSingleton singleton = HelperSingleton.getInstance(holder.layout.getContext());

        if(!nodesDataSet.isEmpty()) {
            long timePassed = 0;
            long upcomingDueDate = Long.MAX_VALUE;
            try {
                timePassed = (singleton.date -
                        dateFormat.parse(singleton.startDate).getTime())/ (24 * 60 * 60 * 1000);
                upcomingDueDate = (singleton.date -
                        dateFormat.parse(singleton.startDate).getTime()) / (24 * 60 * 60 * 1000)
                        + Long.parseLong(nodesDataSet.get(0)[4]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(singleton.closestUpcomingDueDate > upcomingDueDate) {
                singleton.closestUpcomingDueDate = upcomingDueDate;
            }
            String pathName = position == 0 ? "Critical Path" : "Path No." + position;
            pathNameTextView.setText(pathName);
            long daysLeft = Long.MIN_VALUE;
            int index = 0;

            while(daysLeft <= 0 && index < nodesDataSet.size()) {

                String earlyStart = nodesDataSet.get(index)[1];
                //String earlyFinish = nodesDataSet.get(nodesDataSet.size() - 1)[2];
                //String lateStart = nodesDataSet.get(0)[3];
                String lateFinish = nodesDataSet.get(index)[4];
                //String dayRange = earlyStart + "-" + lateStart;
                String startsOrEnds = Long.parseLong(earlyStart) > timePassed ? "Starts" : "Next due";
                daysLeft = Long.parseLong(earlyStart) > timePassed ?
                        Long.parseLong(earlyStart) - timePassed :
                        Long.parseLong(lateFinish) - timePassed;
                pathInformationTextView.setText(String.format(Locale.ENGLISH,
                        "%s in: %d day(s)", startsOrEnds, daysLeft));
                if(daysLeft < 0) pathInformationTextView.setText(String.format(Locale.ENGLISH,
                        "Ended: %d day(s) ago", Math.abs(daysLeft)));
                ++index;
            }

            if(daysLeft < 0) index += 1;
            if(index > 1) {
                ArrayList<String[]> oldDataSet = nodesDataSet;
                nodesDataSet = new ArrayList<>();
                for(int i = index-1; i < oldDataSet.size(); ++i) {
                    nodesDataSet.add(oldDataSet.get(i));
                }
            }
        }
        NodesRecyclerListAdapter adapter = new NodesRecyclerListAdapter(nodesDataSet);
        nodesList.setAdapter(adapter);
        nodesList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(holder.layout.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        nodesList.setLayoutManager(manager);
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
