package com.labs.svaithin.goalplanner_v0_01.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.svaithin.goalplanner.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String GOAL = "goal";
        public static final String MILESTONE = "milestone";

        public static final String GOALTITLE = "goaltitle";
        public static final String GOALDONE = "goaldone";
        public static final String MILESTONETITLE = "milestonetitle";
        public static final String MILESTONEDONE = "milestonedone";
        public static final String MGOALID = "goalid";

    }
}