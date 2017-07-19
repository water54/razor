/**
 * Cobub Razor
 * <p/>
 * An open source analytics android sdk for mobile applications
 *
 * @package Cobub Razor
 * @author WBTECH Dev Team
 * @copyright Copyright (c) 2011 - 2012, NanJing Western Bridge Co.,Ltd.
 * @license http://www.cobub.com/products/cobub-razor/license
 * @link http://www.cobub.com/products/cobub-razor/
 * @filesource
 * @since Version 0.1
 */

package com.wbtech.ums;

import android.content.Context;
import android.text.TextUtils;

import com.wbtech.ums.bean.ActivityInfo;
import com.wbtech.ums.bean.EventInfo;
import com.wbtech.ums.common.CommonUtil;
import com.wbtech.ums.common.MD5Utility;
import com.wbtech.ums.common.UmsAgentStorage;
import com.wbtech.ums.common.UmsConstants;
import com.wbtech.ums.message.MessageCenter;
import com.wbtech.ums.objects.UploadAllData;

public class UmsAgent {

    public static boolean mUseLocationService = true;
    private static String start_millis = null;// The start time point
    private static long start = 0;
    private static String session_id = null;
    private static String activities = null;// currnet activity's name
    public static String appkey = "";
    private static String deviceID = null;

    private static int defaultReportMode = 0;// 0 send at next time's defaultmode，1 send at now

    private static boolean isPostFile = true;
    private static boolean isFirst = true;

    private static String pk_campaign = null; // add by lulu 2015-11-25
    private static String pk_kwd = null; // add by lulu  2015-11-25
    private static String user_identifier = null;// userid value -- add by lulu  2015-11-25
    private static long sessionGenerateTime;
    // 页面自定义变量
    private static String pageVariable_0 = null;

    /**
     * set base URL like http://localhost/razor/ums/index.php?
     */
    public static void setBaseURL(Context context, String url) {
        UmsConstants.preUrl = url;
        // StorageUtil.init(context);

        postClientData(context, null, null);
    }

    public static void setSessionContinueMillis(long interval) {
        if (interval > 0) {
            UmsConstants.kContinueSessionMillis = interval;
        }

    }


    /**
     * bind user
     */
    public static void bindUserIdentifier(Context context, String identifier) {
        UmsAgentStorage.getInstance(context).writeString("identifier", identifier);
    }


    public static void onEvent(final Context context, final String event_id) {
        onEvent(context, event_id, null, 1);
    }

    public static void onEvent(Context context, String eventId, String label) {
        onEvent(context, eventId, label, 1);
    }

    public static void onEvent(Context context, String event_id, String label, int acc) {
        EventInfo eventInfo = new EventInfo(context, event_id, label, acc + "", pk_campaign, pk_kwd);
        MessageCenter.getInstance().sendMessage(new PostEventThread(context, eventInfo));
    }


    public static void onEvent(Context context, String event_id, int acc) {
        onEvent(context, event_id, null, acc);
    }

    public static void onPause(final Context context, final String activityName) {
        saveSessionTime(context);
        String end_millis = CommonUtil.getTime();
        long end = System.currentTimeMillis();
        String duration = end - start + "";
        appkey = CommonUtil.getAppKey(context);
        ActivityInfo activityInfo = new ActivityInfo(context, session_id, start_millis, end_millis, duration, TextUtils.isEmpty(activityName) ? activities : activityName, appkey, deviceID, user_identifier);
        MessageCenter.getInstance().sendMessage(new PostActivityThread(activityInfo, context));
    }


    /**
     * Add by  LULU 2015-12-18
     */
    public static void onResume(final Context context, final String pageTitle, final String... customerVariables) {
        Runnable postOnResumeInfoRunnable = new Runnable() {

            @Override
            public void run() {
                postOnResume(context, pageTitle, customerVariables);
            }
        };
        MessageCenter.getInstance().sendMessage(postOnResumeInfoRunnable);

    }


    public static void setDebugEnabled(boolean isEnableDebug) {
        UmsConstants.DebugMode = isEnableDebug;
    }


    private static void postOnResume(Context context, String pageTitle, String... customerVariables) {
        if (!CommonUtil.isNetworkAvailable(context)) {
            setDefaultReportPolicy(context, 0);
        } else {//2017/7/4 开启第一个页面，上传所有的数据
            if (isPostFile) {
                uploadLog(context);
                isPostFile = false;
            }
        }

        isCreateNewSessionID(context);
        // add by lulu 2015-11-26
        // if pk compaign expired, then clean pkCompaign and pkKwd
        isPkCompaignExpired(context);

//        activities = CommonUtil.getActivityName(context);
        activities = pageTitle;

        if (customerVariables != null && customerVariables.length > 0) {
            pageVariable_0 = customerVariables[0];
        }
        try {
            if (session_id == null) {
                session_id = generateSession(context);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        start_millis = CommonUtil.getTime();
        start = System.currentTimeMillis();

        // add by lulu 2015-11-25
        deviceID = CommonUtil.getDeviceId(context);
        user_identifier = CommonUtil.getUserIdentifier(context);
    }

    private static void isCreateNewSessionID(Context context) {
        long currenttime = System.currentTimeMillis();
//        long session_save_time = sessionGenerateTime;//msAgentStorage.getInstance(context).readLong("session_save_time", 0);
        if (currenttime - sessionGenerateTime > UmsConstants.kContinueSessionMillis) {
            session_id = generateSession(context);
        }
    }

    // add by lulu 2015-11-26
    private static void isPkCompaignExpired(Context context) {
        long currenttime = System.currentTimeMillis();
        long pk_save_time = UmsAgentStorage.getInstance(context).readLong("pk_save_time", currenttime);
        if (currenttime - pk_save_time > UmsConstants.kContinueSessionMillis) {
            pk_campaign = null;
            pk_kwd = null;
        }
    }

    public static void updateOnlineConfig(final Context context) {

        MessageCenter.getInstance().sendMessage(new UpdateOnlineConfigs(context));
    }


    /**
     * Setting data transmission mode
     */
    public static void setDefaultReportPolicy(Context context, int reportModel) {
        CommonUtil.printLog("reportType", reportModel + "");
        if ((reportModel == 0) || (reportModel == 1)) {

            UmsAgent.defaultReportMode = reportModel;
            UmsAgentStorage.getInstance(context).writeInt("ums_local_report_policy", reportModel);
        }
    }

    private static String generateSession(Context context) {
        String sessionId = "";
        String str = TextUtils.isEmpty(appkey) ? CommonUtil.getAppKey(context) : appkey;
        if (str != null) {
            String localDate = CommonUtil.getTime();
            str = str + localDate;
            sessionId = MD5Utility.md5Appkey(str);

            saveSessionTime(context);
            session_id = sessionId;
            return sessionId;
        }
        return sessionId;
    }

    private static void saveSessionTime(Context context) {
        sessionGenerateTime = System.currentTimeMillis();
//        UmsAgentStorage.getInstance(context).writeLong("session_save_time", );
    }

    /*
     * add by lulu 2015-11-26
     */
    private static void savePkTime(Context context) {
        UmsAgentStorage.getInstance(context).writeLong("pk_save_time", System.currentTimeMillis());
    }

    /**
     * Upload all data
     */
    public static void uploadLog(final Context context) {
        MessageCenter.getInstance().sendMessage(new UploadAllData(context));
    }


    // add by lulu 2015-11-26
    public static void postClientData(final Context context) {
        if (isFirst) {
            postClientData(context, null, null);
            isFirst = false;
        }
    }

    /**
     * \ upload client device information
     */
    public static void postClientData(final Context context, String pkCompaigns, String pkKwds) { // add 'pkCompaign' && 'pkKwd' -- by lulu 2015-11-25
        pk_campaign = pkCompaigns;
        pk_kwd = pkKwds;
        if (pk_campaign != null) {
            savePkTime(context);
        }
        MessageCenter.getInstance().sendMessage(new PostClientData(context, pkCompaigns, pkKwds, pageVariable_0));
    }


    public static void stops() {
        MessageCenter.getInstance().quite();
    }

}
