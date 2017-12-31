package com.example.computerlab.projectaid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by achen on 4/29/2017.
 */

public class ProjectOpenHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "scheduler.db";
    public static final int DB_VERSION = 1;
    private Context context;

    public ProjectOpenHelper(Context context) {

        super(context, DB_FILE_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ProjectsTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ProjectsTable.SQL_DELETE);
        onCreate(sqLiteDatabase);
    }

    public boolean insertProject(String id, String name, String startDate, String endDate, String nodes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProjectsTable.COLUMN_ID, id);
        values.put(ProjectsTable.COLUMN_NAME, name);
        values.put(ProjectsTable.COLUMN_STARTDATE, startDate);
        values.put(ProjectsTable.COLUMN_ENDDATE, endDate);
        values.put(ProjectsTable.COLUMN_NODES, nodes);

        db.insert(ProjectsTable.PROJECTS_TABLE, null, values);
        return true;
    }

    public ArrayList<String> getProject(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format(Locale.ENGLISH,
                "SELECT * FROM %s WHERE %s=\"%s\"",
                ProjectsTable.PROJECTS_TABLE, ProjectsTable.COLUMN_ID, id), null);
        cursor.moveToFirst();

        ArrayList<String> project = new ArrayList<>();
        project.add(cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_NAME)));
        project.add(cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_STARTDATE)));
        project.add(cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_ENDDATE)));
        project.add(cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_NODES)));

        cursor.close();
        return project;
    }

    public int updateProject(String id, String name, String startDate, String endDate, String nodes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProjectsTable.COLUMN_NAME, name);
        values.put(ProjectsTable.COLUMN_STARTDATE, startDate);
        values.put(ProjectsTable.COLUMN_ENDDATE, endDate);
        values.put(ProjectsTable.COLUMN_NODES, nodes);

        return db.update(ProjectsTable.PROJECTS_TABLE, values, String.format(Locale.ENGLISH,
                "%s = ? ", ProjectsTable.COLUMN_ID), new String[] {id});
    }

    public int deleteProject(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor cursor = rdb.rawQuery(String.format(Locale.ENGLISH,
                "SELECT * FROM %s WHERE %s=\"%s\"",
                ProjectsTable.PROJECTS_TABLE, ProjectsTable.COLUMN_ID, id), null);
        cursor.moveToFirst();
        String[] nodes = cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_NODES)).split(",");
        cursor.close();

        //delete all nodes associated with this project
        NodeOpenHelper nodeOpenHelper = new NodeOpenHelper(context);
        for(String node : nodes) {
            if (!node.equals("")) {
                nodeOpenHelper.deleteNode(node);
            }
        }
        nodeOpenHelper.close();

        return db.delete(ProjectsTable.PROJECTS_TABLE, String.format(Locale.ENGLISH,
                "%s = ? ", ProjectsTable.COLUMN_ID),
                new String[] {id});
    }

    public int updateEndDate(String projectId, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProjectsTable.COLUMN_ENDDATE, endDate);
        return db.update(ProjectsTable.PROJECTS_TABLE, values, String.format(Locale.ENGLISH,
                "%s = ? ", ProjectsTable.COLUMN_ID), new String[] {projectId});
    }

    public int deleteNode(String projectId, String nodes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProjectsTable.COLUMN_NODES, nodes);
        return db.update(ProjectsTable.PROJECTS_TABLE, values, String.format(Locale.ENGLISH,
                "%s = ? ", ProjectsTable.COLUMN_ID), new String[] {projectId});
    }

    public int updateNodes(String projectId, String nodeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase rdb = this.getReadableDatabase();
        Cursor cursor = rdb.rawQuery(String.format(Locale.ENGLISH,
                "SELECT * FROM %s WHERE %s=\"%s\"",
                ProjectsTable.PROJECTS_TABLE, ProjectsTable.COLUMN_ID, projectId), null);
        cursor.moveToFirst();
        String nodes = cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_NODES));
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(ProjectsTable.COLUMN_NODES, nodes+","+nodeId);
        return db.update(ProjectsTable.PROJECTS_TABLE, values, String.format(Locale.ENGLISH,
                "%s = ? ", ProjectsTable.COLUMN_ID), new String[] {projectId});

    }

    public ArrayList<String[]> getAllProjects() {
        ArrayList<String[]> projects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format(Locale.ENGLISH,
                "SELECT * FROM %s", ProjectsTable.PROJECTS_TABLE), null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            projects.add(new String[] {
                    cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_STARTDATE)),
                    cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_ENDDATE)),
                    cursor.getString(cursor.getColumnIndex(ProjectsTable.COLUMN_NODES))
            });
            cursor.moveToNext();
        }
        cursor.close();
        return projects;
    }
}
