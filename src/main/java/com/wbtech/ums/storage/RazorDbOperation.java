package com.wbtech.ums.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.wbtech.ums.bean.ActivityInfo;
import com.wbtech.ums.bean.ClientData;
import com.wbtech.ums.bean.EventInfo;
import com.wbtech.ums.objects.DataBaseResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hawk.zheng on 2017/7/4.
 */

public class RazorDbOperation {
    private static RazorDbOperation dbOperation;
    private AtomicInteger dataBaseCounts;
    private RazorDbOpenHelper razorDbOpenHelper;
    private SQLiteDatabase mDatabase;

    private RazorDbOperation() {
        dataBaseCounts = new AtomicInteger(0);

    }

    public static RazorDbOperation getInstance() {
        if (dbOperation == null) {
            synchronized (RazorDbOperation.class) {
                if (dbOperation == null) dbOperation = new RazorDbOperation();
            }
        }
        return dbOperation;
    }

    public synchronized RazorDbOperation openDataBase(Context context) {
        if (dataBaseCounts.incrementAndGet() == 1) {
            if (razorDbOpenHelper == null) {
                razorDbOpenHelper = new RazorDbOpenHelper(context);
            }
            mDatabase = razorDbOpenHelper.getWritableDatabase();
        }
        return this;
    }

    public synchronized void closeDataBase() {
        if (dataBaseCounts.decrementAndGet() == 0) {
            if (mDatabase != null && mDatabase.isOpen()) mDatabase.close();
        }
    }

//    public void logContent(String content) {
//        Log.e(getClass().getSimpleName(), content);
//    }

    public void insert(Context context, EventInfo eventInfo) {
        openDataBase(context);
        ContentValues contentValues = new ContentValues(20);
        contentValues.put("time", eventInfo.time);
        contentValues.put("version", eventInfo.version);
        contentValues.put("event_identifier", eventInfo.event_identifier);
        contentValues.put("appkey", eventInfo.appkey);
        contentValues.put("activity", eventInfo.activity);
        contentValues.put("deviceid", eventInfo.deviceid);
        contentValues.put("useridentifier", eventInfo.useridentifier);
        contentValues.put("label", eventInfo.label);
        contentValues.put("acc", eventInfo.acc);
        contentValues.put("pk_campaign", eventInfo.pk_campaign);
        contentValues.put("pk_kwd", eventInfo.pk_kwd);
        contentValues.put("eventTime", eventInfo.eventTime);

        mDatabase.insert("eventInfo", null, contentValues);
        closeDataBase();
    }

    public void insert(Context context, ActivityInfo activityInfo) {
        openDataBase(context);
        ContentValues contentValues = new ContentValues(20);
        contentValues.put("session_id", activityInfo.session_id);
        contentValues.put("start_millis", activityInfo.start_millis);
        contentValues.put("end_millis", activityInfo.end_millis);
        contentValues.put("duration", activityInfo.duration);
        contentValues.put("version", activityInfo.version);
        contentValues.put("appkey", activityInfo.appkey);
        contentValues.put("deviceid", activityInfo.deviceid);
        contentValues.put("userid", activityInfo.userid);
        contentValues.put("activities", activityInfo.activities);
        contentValues.put("eventTime", activityInfo.eventTime);

        mDatabase.insert("activityInfo", null, contentValues);
        closeDataBase();
    }

    public void insert(Context context, ClientData clientData) {
        openDataBase(context);

        ContentValues contentValues = new ContentValues(80);
        contentValues.put("phonetype", clientData.phonetype);
        contentValues.put("os_version", clientData.os_version);
        contentValues.put("platform", clientData.platform);
        contentValues.put("productkey", clientData.productkey);
        contentValues.put("language", clientData.language);
        contentValues.put("deviceid", clientData.deviceid);
        contentValues.put("tdId", clientData.tdId);
        contentValues.put("appkey", clientData.appkey);
        contentValues.put("resolution", clientData.resolution);
        contentValues.put("ismobiledevice", clientData.ismobiledevice);
        contentValues.put("network", clientData.network);
        contentValues.put("time", clientData.time);
        contentValues.put("version", clientData.version);
        contentValues.put("userid", clientData.userid);
        contentValues.put("androidid", clientData.androidid);
        contentValues.put("mccmnc", clientData.mccmnc);
        contentValues.put("cellid", clientData.cellid);
        contentValues.put("lac", clientData.lac);
        contentValues.put("moduleName", clientData.modulename);
        contentValues.put("devicename", clientData.devicename);
        contentValues.put("wifimac", clientData.wifimac);
        contentValues.put("havewifi", clientData.havewifi);
        contentValues.put("haveGps", clientData.havegps);
        contentValues.put("havegps", clientData.havegravity);
        contentValues.put("installedApp", clientData.installedApp);
        contentValues.put("latitude", clientData.latitude);
        contentValues.put("longitude", clientData.longitude);
        contentValues.put("imsi", clientData.imsi);
        contentValues.put("havebt", clientData.havebt);
        contentValues.put("pk_campaign", clientData.pk_campaign);
        contentValues.put("pk_kwd", clientData.pk_kwd);
        contentValues.put("pageVariable_0", clientData.pageVariable_0);

        long id = mDatabase.insertWithOnConflict("clientData", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
//        logContent("id=" + id + " " + contentValues);
        if (id == -1) {
            mDatabase.update("clientData", contentValues, "platform=?", new String[]{"android"});
        }

        closeDataBase();
    }

    public DataBaseResult getEventInfo(Context context) {
        DataBaseResult dataBaseResult = new DataBaseResult();
        JSONArray jsonArray = new JSONArray();
        openDataBase(context);
        Cursor cursor = mDatabase.query("eventInfo", null, null, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> ids = new ArrayList<>(cursor.getCount() * 2);
        int idIndex = cursor.getColumnIndexOrThrow("id");

        int eventTimeIndex = cursor.getColumnIndexOrThrow("eventTime");
        int timeIndex = cursor.getColumnIndex("time");
        int versionIndex = cursor.getColumnIndex("version");
        int eventIdentifierIndex = cursor.getColumnIndex("event_identifier");

        int appKeyIndex = cursor.getColumnIndex("appkey");
        int activityIndex = cursor.getColumnIndex("activity");
        int deviceIdIndex = cursor.getColumnIndex("deviceid");
        int userIdentifierIndex = cursor.getColumnIndex("useridentifier");
        int labelIndex = cursor.getColumnIndex("label");
        int accIndex = cursor.getColumnIndex("acc");
        int pk_campaignIndex = cursor.getColumnIndex("pk_campaign");
        int pk_kwdIndex = cursor.getColumnIndex("pk_kwd");
        for (; !cursor.isAfterLast(); cursor.moveToNext()) {
            ids.add(cursor.getInt(idIndex) + "");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("eventTime", cursor.getLong(eventTimeIndex));
                jsonObject.put("time", cursor.getString(timeIndex));
                jsonObject.put("version", cursor.getString(versionIndex));
                jsonObject.put("event_identifier", cursor.getString(eventIdentifierIndex));
                jsonObject.put("appkey", cursor.getString(appKeyIndex));
                jsonObject.put("activity", cursor.getString(activityIndex));
                jsonObject.put("deviceid", cursor.getString(deviceIdIndex));
                jsonObject.put("useridentifier", cursor.getString(userIdentifierIndex));
                jsonObject.put("label", cursor.getString(labelIndex));
                jsonObject.put("acc", cursor.getString(accIndex));
                jsonObject.put("pk_campaign", cursor.getString(pk_campaignIndex));
                jsonObject.put("pk_kwd", cursor.getString(pk_kwdIndex));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        dataBaseResult.ids = ids;
        dataBaseResult.jsonArrayData = jsonArray;

        cursor.close();

        closeDataBase();
        return dataBaseResult;
    }


    public DataBaseResult getActivityInfo(Context context) {
        DataBaseResult dataBaseResult = new DataBaseResult();
        JSONArray jsonArray = new JSONArray();
        openDataBase(context);
        Cursor cursor = mDatabase.query("activityInfo", null, null, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> ids = new ArrayList<>(cursor.getCount() * 2);
        int idIndex = cursor.getColumnIndexOrThrow("id");
        int eventTimeIndex = cursor.getColumnIndexOrThrow("eventTime");
        int sessionIdIndex = cursor.getColumnIndex("session_id");
        int startMillisIndex = cursor.getColumnIndex("start_millis");
        int endMillisIndex = cursor.getColumnIndex("end_millis");

        int durationIndex = cursor.getColumnIndex("duration");
        int versionIndex = cursor.getColumnIndex("version");
        int activitiesIndex = cursor.getColumnIndex("activities");
        int appKeyIndex = cursor.getColumnIndex("appkey");
        int deviceIdIndex = cursor.getColumnIndex("deviceid");
        int userIdIndex = cursor.getColumnIndex("userid");

        for (; !cursor.isAfterLast(); cursor.moveToNext()) {
            ids.add(cursor.getInt(idIndex) + "");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("eventTime", cursor.getLong(eventTimeIndex));
                jsonObject.put("session_id", cursor.getString(sessionIdIndex));
                jsonObject.put("start_millis", cursor.getString(startMillisIndex));
                jsonObject.put("end_millis", cursor.getString(endMillisIndex));
                jsonObject.put("duration", cursor.getString(durationIndex));
                jsonObject.put("version", cursor.getString(versionIndex));
                jsonObject.put("activities", cursor.getString(activitiesIndex));
                jsonObject.put("appkey", cursor.getString(appKeyIndex));
                jsonObject.put("deviceid", cursor.getString(deviceIdIndex));
                jsonObject.put("userid", cursor.getString(userIdIndex));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);

        }
        dataBaseResult.ids = ids;
        dataBaseResult.jsonArrayData = jsonArray;

        cursor.close();
        closeDataBase();
        return dataBaseResult;
    }


    public DataBaseResult getClientData(Context context) {
        DataBaseResult dataBaseResult = new DataBaseResult();
        JSONArray jsonArray = new JSONArray();
        openDataBase(context);
        Cursor cursor = mDatabase.query("clientData", null, null, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> ids = new ArrayList<>(2);
        int idIndex = cursor.getColumnIndexOrThrow("id");

        ids.add(cursor.getInt(idIndex) + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phonetype", cursor.getInt(cursor.getColumnIndex("phonetype")));
            jsonObject.put("os_version", cursor.getString(cursor.getColumnIndex("os_version")));
            jsonObject.put("platform", "android");
            jsonObject.put("productkey", cursor.getString(cursor.getColumnIndex("productkey")));
            jsonObject.put("language", cursor.getString(cursor.getColumnIndex("language")));
            jsonObject.put("deviceid", cursor.getString(cursor.getColumnIndex("deviceid")));
            jsonObject.put("tdId", cursor.getString(cursor.getColumnIndex("tdId")));
            jsonObject.put("appkey", cursor.getString(cursor.getColumnIndex("appkey")));
            jsonObject.put("resolution", cursor.getString(cursor.getColumnIndex("resolution")));
            jsonObject.put("ismobiledevice", true);
            jsonObject.put("network", cursor.getString(cursor.getColumnIndex("network")));
            jsonObject.put("time", cursor.getString(cursor.getColumnIndex("time")));
            jsonObject.put("version", cursor.getString(cursor.getColumnIndex("version")));
            jsonObject.put("userid", cursor.getString(cursor.getColumnIndex("userid")));
            jsonObject.put("androidid", cursor.getString(cursor.getColumnIndex("androidid")));
            jsonObject.put("mccmnc", cursor.getString(cursor.getColumnIndex("mccmnc")));
            jsonObject.put("cellid", cursor.getString(cursor.getColumnIndex("cellid")));
            String lac = cursor.getString(cursor.getColumnIndex("lac"));
            if (TextUtils.isEmpty(lac)) {
                lac = "";
            }
            jsonObject.put("lac", lac);
            jsonObject.put("modulename", cursor.getString(cursor.getColumnIndex("modulename")));
            jsonObject.put("devicename", cursor.getString(cursor.getColumnIndex("devicename")));
            jsonObject.put("wifimac", cursor.getString(cursor.getColumnIndex("wifimac")));
            jsonObject.put("havewifi", cursor.getInt(cursor.getColumnIndex("havewifi")) == 1);
            jsonObject.put("havegps", cursor.getInt(cursor.getColumnIndex("havegps")) == 1);
            jsonObject.put("havegravity", cursor.getInt(cursor.getColumnIndex("havegravity")) == 1);
            jsonObject.put("installedApp", cursor.getString(cursor.getColumnIndex("installedApp")));
            jsonObject.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
            jsonObject.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
            String imsi = cursor.getString(cursor.getColumnIndex("imsi"));
            if (TextUtils.isEmpty(imsi))
                imsi = "";
            jsonObject.put("imsi", imsi);
            jsonObject.put("havebt", cursor.getInt(cursor.getColumnIndex("havebt")) == 1);
            String pk_campaign = cursor.getString(cursor.getColumnIndex("pk_campaign"));
            if (TextUtils.isEmpty(pk_campaign)) pk_campaign = "";
            jsonObject.put("pk_campaign", pk_campaign);
            String pk_kwd = cursor.getString(cursor.getColumnIndex("pk_kwd"));
            if (TextUtils.isEmpty(pk_kwd)) pk_kwd = "";
            jsonObject.put("pk_kwd", pk_kwd);
            String pageVariable_0 = cursor.getString(cursor.getColumnIndex("pageVariable_0"));
            if (TextUtils.isEmpty(pageVariable_0)) pageVariable_0 = "";
            jsonObject.put("pageVariable_0", pageVariable_0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jsonObject);

        dataBaseResult.ids = ids;
        dataBaseResult.jsonArrayData = jsonArray;

        cursor.close();
        closeDataBase();
        return dataBaseResult;
    }

    public void deleteDataById(Context context, String tableName, ArrayList<String> ids) {
        openDataBase(context);
//        mDatabase.beginTransaction();
        for (String id : ids) {
            mDatabase.delete(tableName, " id=? ", new String[]{String.valueOf(id)});
        }
//        mDatabase.endTransaction();
        closeDataBase();
    }
}
