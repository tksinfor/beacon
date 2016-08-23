package com.ik9.beacon.repository;

/**
 * Created by Alexandre on 14/07/2016.
 */

public class ScriptSQL {

    private static int scriptVersion = 1;

    public static int getScriptVersion() {
        return scriptVersion;
    }

    public static String getScriptCreateProfile() {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("CREATE TABLE profile ( ");
        sqlBuilder.append("_id       INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder.append("name      TEXT NOT NULL, ");
        sqlBuilder.append("email     TEXT, ");
        sqlBuilder.append("birth     TEXT NOT NULL, ");
        sqlBuilder.append("gender    TEXT NOT NULL, ");
        sqlBuilder.append("photo     TEXT, ");
        sqlBuilder.append("phone     TEXT, ");
        sqlBuilder.append("city      TEXT, ");
        sqlBuilder.append("state     TEXT, ");
        sqlBuilder.append("device_id TEXT NOT NULL ");
        sqlBuilder.append("); ");

        return sqlBuilder.toString();
    }

    public static String getScriptCreateCampaign() {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("CREATE TABLE campaign ( ");
        sqlBuilder.append("_id          INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder.append("name         TEXT, ");
        sqlBuilder.append("description  TEXT, ");
        sqlBuilder.append("image        TEXT, ");
        sqlBuilder.append("icon         TEXT, ");
        sqlBuilder.append("liked        INTEGER, ");
        sqlBuilder.append("vizualized   INTEGER, ");
        sqlBuilder.append("final_period TEXT ");
        sqlBuilder.append("); ");

        return sqlBuilder.toString();
    }

    public static String getScriptUpgrade() {
        String[] sql = new String[]{};
        return sql[scriptVersion - 1];
    }
}
