package com.wbtech.ums.bean;

import android.content.Context;

import com.wbtech.ums.common.CommonUtil;

/**
 * Created by hawk.zheng on 2017/7/3.
 */

public class ActivityInfo {
    public transient String id;
    public long eventTime;
    /**
     * session_id : 80fc2a81d9b84b0122f7c6c86acedeb5
     * start_millis : 2017-07-03 18:51:58
     * end_millis : 2017-07-03 18:52:00
     * duration : 1898
     * version : 4.6.0.debug
     * activities : SplashActivity
     * appkey : internal
     * deviceid : 861945034530497
     * userid :
     */

    public String session_id;
    public String start_millis;
    public String end_millis;
    public String duration;
    public String version;
    public String activities;
    public String appkey;
    public String deviceid;
    public String userid;


    public ActivityInfo(Context context, String sessionId, String startMillis, String endMillis, String duration, String activities, String appKey, String deviceId, String userId) {
        eventTime = System.currentTimeMillis();
        this.userid = userId;
        this.session_id = sessionId;
        this.start_millis = startMillis;
        this.end_millis = endMillis;
        this.duration = duration;
        this.version = CommonUtil.getVersion(context);
        this.activities = activities;
        this.appkey = appKey;
        this.deviceid = deviceId;
    }
}
