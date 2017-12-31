package com.example.computerlab.projectaid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.computerlab.projectaid.database.NodeOpenHelper;
import com.example.computerlab.projectaid.database.NodesTable;
import com.example.computerlab.projectaid.database.ProjectOpenHelper;
import com.example.computerlab.projectaid.database.ProjectsTable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by computerlab on 4/5/17.
 */

// temporary setup for testing critical path algorithm before connecting to a database
public class HelperSingleton {

    private static HelperSingleton instance;

    public static HelperSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new HelperSingleton(context);
        }
        return instance;
    }
    private HelperSingleton(Context context) {
        projectHelper = new ProjectOpenHelper(context);
        projectHelper.onCreate(projectHelper.getWritableDatabase());
        database = projectHelper.getWritableDatabase();
        graph = new Graph();
        open(context);
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTimeInMillis();
        testNotification = false;
        closestUpcomingDueDate = Long.MAX_VALUE;
    }

    public void open(Context context) {
        nodeHelper = new NodeOpenHelper(context);
        nodeHelper.onCreate(nodeHelper.getWritableDatabase());
    }

    public void close() {
        nodeHelper.close();
        projectHelper.close();
    }

    public void findCriticalPath() {
        closestUpcomingDueDate = Long.MAX_VALUE;
        //criticalPath = graph.find_rudimentary_critical_path();
        criticalPath = graph.find_complete_critical_path();
        //ArrayList<String> project = projectHelper.getProject(projectId);
        ArrayList<String[]> projectTimePath = criticalPath.get("path0");
        //if(project.get(2).equals("TBD"))
        if(projectTimePath != null && projectTimePath.size() != 0) {
            try {
                endDate = PathsRecyclerListAdapter.dateFormat.format(
                        PathsRecyclerListAdapter.dateFormat.parse(startDate).getTime() +
                                Long.parseLong(projectTimePath.get(projectTimePath.size() - 1)[4]) *
                                        24 * 60 * 60 * 1000);
                projectHelper.updateEndDate(projectId, endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setProject(String id) {
        projectId = id;

        graph = new Graph();

        if(projectId != null) initiateGraph();
    }

    private void initiateGraph() {
        ArrayList<String> project = projectHelper.getProject(projectId);
        String[] nodes = project.get(3).split(",");
        startDate = project.get(1);
        endDate = project.get(2);
        projectName = project.get(0);
        for(String nodeId : nodes) {
            if(!nodeId.equals("")) {
                ArrayList<String> node = nodeHelper.getNode(nodeId);
                if(node != null && node.size() > 0) {
                    try {
                        graph.insert_node(Long.parseLong(node.get(2)), node.get(1),
                                node.subList(3, node.size()), node.get(0));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Duplicate name: "+node.get(1));
                    }
                }
            }
        }
    }

    public Graph graph;
    public SQLiteDatabase database;
    public NodeOpenHelper nodeHelper;
    //public ArrayList<String[]> criticalPath;
    public HashMap<String, ArrayList<String[]>> criticalPath;
    public ProjectOpenHelper projectHelper;
    public String projectId, startDate, endDate, projectName;
    public long date, closestUpcomingDueDate;
    public boolean testNotification;
}
