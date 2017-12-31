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
 * Created by achen on 4/26/2017.
 */

public class NodeOpenHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "scheduler.db";
    public static final int DB_VERSION = 1;

    public NodeOpenHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NodesTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(NodesTable.SQL_DELETE);
        onCreate(sqLiteDatabase);
    }

    public boolean insertNode(String id, String name, long duration, String parent0, String parent1,
                              String parent2, String parent3, String parent4, String parent5,
                              String parent6, String parent7, String parent8) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NodesTable.COLUMN_ID, id);
        values.put(NodesTable.COLUMN_NAME, name);
        values.put(NodesTable.COLUMN_DURATION, duration);
        values.put(NodesTable.COLUMN_PARENT0, parent0);
        values.put(NodesTable.COLUMN_PARENT1, parent1);
        values.put(NodesTable.COLUMN_PARENT2, parent2);
        values.put(NodesTable.COLUMN_PARENT3, parent3);
        values.put(NodesTable.COLUMN_PARENT4, parent4);
        values.put(NodesTable.COLUMN_PARENT5, parent5);
        values.put(NodesTable.COLUMN_PARENT6, parent6);
        values.put(NodesTable.COLUMN_PARENT7, parent7);
        values.put(NodesTable.COLUMN_PARENT8, parent8);
        //System.out.println(values.toString());

        db.insert(NodesTable.NODES_TABLE, null, values);
        return true;
    }

    public ArrayList<String> getNode(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format(Locale.ENGLISH,
                "SELECT * FROM %s WHERE %s=\"%s\"",
                NodesTable.NODES_TABLE, NodesTable.COLUMN_ID, id), null);
        ArrayList<String> list = new ArrayList<>();

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_ID)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_NAME)));
            list.add(Integer.toString(cursor.getInt(cursor.getColumnIndex(NodesTable.COLUMN_DURATION))));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT0)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT1)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT2)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT3)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT4)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT5)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT6)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT7)));
            list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT8)));
        }
        cursor.close();

        return list;
    }

    public int deleteNode(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(NodesTable.NODES_TABLE, String.format(Locale.ENGLISH,
                "%s = ? ", NodesTable.COLUMN_ID),
                new String[] {id});
    }

    public HashMap<String, ArrayList<String>> getAllNodes(String projectId) {
        HashMap<String, ArrayList<String>> nodes = new HashMap<>();
        //ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        //get the project from the database
        Cursor projectCursor = db.rawQuery(String.format(Locale.ENGLISH,
                "SELECT * FROM %s WHERE %s=\"%s\"",
                ProjectsTable.PROJECTS_TABLE, ProjectsTable.COLUMN_ID, projectId), null);
        projectCursor.moveToFirst();

        String[] nodeIdsArray = projectCursor.getString(
                projectCursor.getColumnIndex(ProjectsTable.COLUMN_NODES)).split(",");
        projectCursor.close();
        for(String nodeId : nodeIdsArray) {
            //list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_NAME)));
            Cursor cursor = db.rawQuery(String.format(Locale.ENGLISH, "SELECT * FROM %s WHERE %s=\"%s\"",
                    NodesTable.NODES_TABLE, NodesTable.COLUMN_ID, nodeId), null);
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                ArrayList<String> list = new ArrayList<>();
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_ID)));
                list.add(Integer.toString(cursor.getInt(cursor.getColumnIndex(NodesTable.COLUMN_DURATION))));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT0)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT1)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT2)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT3)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT4)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT5)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT6)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT7)));
                list.add(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_PARENT8)));
                nodes.put(cursor.getString(cursor.getColumnIndex(NodesTable.COLUMN_NAME)),
                        list);
                cursor.close();
            }
        }
        return nodes;
    }
}
