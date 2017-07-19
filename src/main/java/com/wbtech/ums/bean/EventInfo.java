package com.wbtech.ums.bean;

import android.content.Context;

import com.wbtech.ums.common.CommonUtil;

/**
 * Created by hawk.zheng on 2017/7/3.
 */

public class EventInfo {
    public transient String id;
    public long eventTime;
    /**
     * time : 2017-07-03 18:51:58
     * version : 4.6.0.debug
     * event_identifier : MESSETTING-acceptpush
     * appkey : internal
     * activity : .activity.SplashActivity
     * deviceid : 861945034530497
     * useridentifier :
     * label : 1
     * acc : 1
     */

    public String time;
    public String version;

    public String event_identifier;

    public String appkey;
    public String activity;

    public String deviceid;

    public String useridentifier;
    public String label;
    public String acc;

    public String pk_campaign;
    public String pk_kwd;


    public boolean verification() {
        if (this.acc.contains("-") || this.acc == null || "".equals(this.acc)) {
            return false;
        } else {
            return true;
        }
    }

    public EventInfo(Context context, String eventIdentifier, String label, String acc, String pk_campaign, String pk_kwd) {
        eventTime = System.currentTimeMillis();
        this.version = CommonUtil.getVersion(context);
        this.time = CommonUtil.getTime();
        this.event_identifier = eventIdentifier;
        this.appkey = CommonUtil.getAppKey(context);
        this.activity = CommonUtil.getActivityName(context);
        this.deviceid = CommonUtil.getDeviceId(context);
        this.useridentifier = CommonUtil.getUserIdentifier(context);
        this.label = label;
        this.acc = acc;
        this.pk_campaign = pk_campaign;
        this.pk_kwd = pk_kwd;
    }
}
