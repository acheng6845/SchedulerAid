package com.example.computerlab.projectaid.database;

import java.util.Locale;

/**
 * Created by achen on 4/26/2017.
 */

public class NodesTable {
    public static final String NODES_TABLE = "nodes";
    public static final String COLUMN_ID = "nodeId";
    public static final String COLUMN_NAME = "nodeName";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PARENT0 = "parent0";
    public static final String COLUMN_PARENT1 = "parent1";
    public static final String COLUMN_PARENT2 = "parent2";
    public static final String COLUMN_PARENT3 = "parent3";
    public static final String COLUMN_PARENT4 = "parent4";
    public static final String COLUMN_PARENT5 = "parent5";
    public static final String COLUMN_PARENT6 = "parent6";
    public static final String COLUMN_PARENT7 = "parent7";
    public static final String COLUMN_PARENT8 = "parent8";

    public static final String SQL_CREATE = String.format(Locale.ENGLISH,
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s TEXT PRIMARY KEY,%s TEXT,%s INTEGER," +
                    "%s TEXT, %s TEXT,%s TEXT,%s TEXT,%s TEXT," +
                    "%s TEXT,%s TEXT,%s TEXT,%s TEXT," +
                    "FOREIGN KEY (%s) REFERENCES %s (%s),FOREIGN KEY (%s) REFERENCES %s (%s)," +
                    "FOREIGN KEY (%s) REFERENCES %s (%s),FOREIGN KEY (%s) REFERENCES %s (%s)," +
                    "FOREIGN KEY (%s) REFERENCES %s (%s),FOREIGN KEY (%s) REFERENCES %s (%s)," +
                    "FOREIGN KEY (%s) REFERENCES %s (%s),FOREIGN KEY (%s) REFERENCES %s (%s)," +
                    "FOREIGN KEY (%s) REFERENCES %s (%s))",
            NODES_TABLE, COLUMN_ID, COLUMN_NAME, COLUMN_DURATION,
            COLUMN_PARENT0, COLUMN_PARENT1, COLUMN_PARENT2, COLUMN_PARENT3, COLUMN_PARENT4,
            COLUMN_PARENT5, COLUMN_PARENT6, COLUMN_PARENT7, COLUMN_PARENT8,
            COLUMN_PARENT0, NODES_TABLE,COLUMN_PARENT0,
            COLUMN_PARENT1, NODES_TABLE,COLUMN_PARENT1,
            COLUMN_PARENT2, NODES_TABLE,COLUMN_PARENT2,
            COLUMN_PARENT3, NODES_TABLE,COLUMN_PARENT3,
            COLUMN_PARENT4, NODES_TABLE,COLUMN_PARENT4,
            COLUMN_PARENT5, NODES_TABLE,COLUMN_PARENT5,
            COLUMN_PARENT6, NODES_TABLE,COLUMN_PARENT6,
            COLUMN_PARENT7, NODES_TABLE,COLUMN_PARENT7,
            COLUMN_PARENT8, NODES_TABLE,COLUMN_PARENT8);

    public static final String SQL_DELETE = String.format(Locale.ENGLISH,
            "DROP TABLE IF EXISTS %s", NODES_TABLE);

}
