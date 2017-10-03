package com.codepath.apps.twitter.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "TwitterDatabase";

    public static final int VERSION = 2;
}
