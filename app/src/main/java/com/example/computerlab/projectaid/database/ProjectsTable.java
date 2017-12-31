package com.example.computerlab.projectaid.database;

import java.util.Locale;

/**
 * Created by achen on 4/29/2017.
 */

public class ProjectsTable {
    public static final String PROJECTS_TABLE = "projects";
    public static final String COLUMN_ID = "projectId";
    public static final String COLUMN_NAME = "projectName";
    public static final String COLUMN_STARTDATE = "startDate";
    public static final String COLUMN_ENDDATE = "endDate";
    public static final String COLUMN_NODES = "nodes";

    public static final String SQL_CREATE = String.format(Locale.ENGLISH,
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s TEXT PRIMARY KEY,%s TEXT,%s TEXT," +
                    "%s TEXT, %s TEXT)",
            PROJECTS_TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_STARTDATE, COLUMN_ENDDATE, COLUMN_NODES);

    public static final String SQL_DELETE = String.format(Locale.ENGLISH,
            "DROP TABLE IF EXISTS %s", PROJECTS_TABLE);
}
