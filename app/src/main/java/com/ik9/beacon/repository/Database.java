package com.ik9.beacon.repository;

import android.content.Context;
import android.database.sqlite.*;

public class Database extends SQLiteOpenHelper {
    //public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    private static final String NAME_DB = "TagPush";

    public Database(Context context) {
        super(context, NAME_DB, null, ScriptSQL.getScriptVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ScriptSQL.getScriptCreateProfile());
        db.execSQL(ScriptSQL.getScriptCreateCampaign());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table profile");
        db.execSQL("drop table campaign");
        onCreate(db);
        //db.execSQL(ScriptSQL.getScriptUpgrade());
    }
}
