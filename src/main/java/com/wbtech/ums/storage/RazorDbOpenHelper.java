package com.wbtech.ums.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hawk.zheng on 2017/7/4.
 */

public class RazorDbOpenHelper extends SQLiteOpenHelper {
    public RazorDbOpenHelper(Context context) {
        super(context, "razor", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table eventInfo(id integer primary key,time text,version text,event_identifier text,appkey text,activity text,deviceid text,useridentifier text,label text,acc text,pk_campaign text,pk_kwd text,eventTime long);");

        db.execSQL("create table activityInfo(id integer primary key,session_id text,start_millis text,end_millis text,duration text,version text,activities text,appkey text,deviceid text,userid text,eventTime long);");
//        db.execSQL("create table clientData(id integer primary key,key text,value text);");

        db.execSQL("create table clientData(id integer primary key,phonetype integer,os_version text,platform text unique,productkey text,language text,deviceid text,tdId text,appkey text,resolution text,ismobiledevice boolean" +
                ",network text,time text,version text,userid text,androidid text,mccmnc text,cellid text,lac text,modulename text,devicename text,wifimac text,havewifi boolean,havegps boolean,havegravity boolean,installedApp text" +
                ",latitude text,longitude text,imsi text,havebt boolean,pk_campaign text,pk_kwd text,pageVariable_0 text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
