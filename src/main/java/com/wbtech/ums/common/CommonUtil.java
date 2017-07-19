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
package com.wbtech.ums.common;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import com.wbtech.ums.BuildConfig;
import com.wbtech.ums.UmsAgent;
import com.wbtech.ums.objects.LatitudeAndLongitude;
import com.wbtech.ums.objects.SCell;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CommonUtil {


    /**
     * checkPermissions
     */
    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(permission,
                context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * return UserIdentifier
     */
    public static String getUserIdentifier(Context context) {
        return UmsAgentStorage.getInstance(context).readString("identifier", "");
    }

    /*
     * add 2015-11-13
     */
    public static String getAndroidIdentifier(Context context) {
        if (context == null) return "";
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Judge wifi is available
     */
    public static boolean isWiFiActive(Context inContext) {
        if (checkPermissions(inContext, "android.permission.ACCESS_WIFI_STATE")) {
            Context context = inContext.getApplicationContext();
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getTypeName().equals("WIFI")
                                && info[i].isConnected()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {
            printLog("lost permission", "lost--->android.permission.ACCESS_WIFI_STATE");
            return false;
        }
    }

    /**
     * Testing equipment networking and networking WIFI
     */
    public static boolean isNetworkAvailable(Context context) {
        if (checkPermissions(context, "android.permission.ACCESS_NETWORK_STATE")) {
            ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            } else {
                printLog("error", "Network error");
                return false;
            }

        } else {
            printLog(" lost  permission", "lost----> android.permission.ACCESS_NETWORK_STATE");
            return false;
        }

    }

    /**
     * Get the current time format yyyy-MM-dd HH:mm:ss
     */
    public static String getTime() {
        Date date = new Date();
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return localSimpleDateFormat.format(date);
    }

    /**
     * get APPKEY
     */
    public static String getAppKey(Context paramContext) {
        if (!TextUtils.isEmpty(UmsAgent.appkey)) {
            return UmsAgent.appkey;
        }
        if (paramContext == null) {
            return "";
        }
        try {
            PackageManager localPackageManager = paramContext
                    .getPackageManager();
            ApplicationInfo localApplicationInfo = localPackageManager
                    .getApplicationInfo(paramContext.getPackageName(), PackageManager.GET_META_DATA);
            if (localApplicationInfo != null) {
                Object str = localApplicationInfo.metaData.get("UMENG_CHANNEL");  // UMS_APPKEY -> "UMENG_CHANNEL"
                if (str != null) {
                    UmsAgent.appkey = str.toString();
                    return UmsAgent.appkey;
                }

                printLog("UmsAgent", "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml."); // UMS_APPKEY -> "UMENG_CHANNEL"
            }
        } catch (Exception localException) {
            printLog("UmsAgent",
                    "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml."); // UMS_APPKEY -> "UMENG_CHANNEL"
            localException.printStackTrace();

        }
        return "";
    }

    /**
     * get current activity's name
     */
    public static String getActivityName(Context context) {
        if (context == null) {
            return "";
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (checkPermissions(context, "android.permission.GET_TASKS")) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getShortClassName();
        } else {
            printLog("lost permission", "android.permission.GET_TASKS");
            return "";
        }

    }


    /**
     * Get the current send model
     */
    public static int getReportPolicyMode(Context context) {
        return UmsAgentStorage.getInstance(context).readInt("ums_local_report_policy", 0);
    }

    /**
     * Get the base station information
     */
    public static SCell getCellInfo(Context context) throws Exception {
        SCell cell = new SCell();
        TelephonyManager mTelNet = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
        if (location == null) {

            printLog("GsmCellLocation Error", "GsmCellLocation is null");

            return null;
        }

        String operator = mTelNet.getNetworkOperator();
        if (TextUtils.isEmpty(operator) || operator.length() < 3) return null;
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        int cid = location.getCid();
        int lac = location.getLac();

        cell.MCC = mcc;
        cell.MCCMNC = Integer.parseInt(operator);
        cell.MNC = mnc;
        cell.LAC = lac;
        cell.CID = cid;

        return cell;
    }

    public static LatitudeAndLongitude getLatitudeAndLongitude(Context context, boolean mUseLocationService) {
        LatitudeAndLongitude latitudeAndLongitude = new LatitudeAndLongitude();
        latitudeAndLongitude.latitude = "";
        latitudeAndLongitude.longitude = "";
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return latitudeAndLongitude;
        }

        if (mUseLocationService) {
            LocationManager loctionManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            List<String> matchingProviders = loctionManager.getAllProviders();
            for (String prociderString : matchingProviders) {
                // Log.d("provider",prociderString);
                Location location = loctionManager
                        .getLastKnownLocation(prociderString);
                if (location != null) {
                    // Log.d("ss", location.getLatitude()+"");
                    latitudeAndLongitude.latitude = location.getLatitude() + "";
                    latitudeAndLongitude.longitude = location.getLongitude() + "";
                }
            }
        }
        return latitudeAndLongitude;

    }

    /**
     * To determine whether it contains a gyroscope
     */
    public static boolean isHaveGravity(Context context) {
        SensorManager manager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        return manager != null;
    }


    /**
     * Get the current application version number
     */
    public static String getVersion(Context context) {
        String versionName = "";
        try {
            if (context == null) {
                return "";
            }
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            printLog("Exception", e.getMessage());

        }
        return versionName;
    }

    /**
     * Set the output log
     */

    public static void printLog(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Log.e("umsAgent", tag + ">" + log);
        }
    }

    public static String getNetworkTypeWIFI2G3G(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();
        String type = info.getTypeName().toLowerCase();
        if (!type.equals("wifi")) {
            type = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getExtraInfo();
        }
        return type;

    }


    /**
     * Get device name, manufacturer + model
     *
     * @return device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    /**
     * Capitalize the first letter
     *
     * @param s model,manufacturer
     * @return Capitalize the first letter
     */
    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * 获取设备的唯一标识，不一定是deviceId
     */
    public static String getDeviceId(Context context) {
        String deviceId;
        if (context != null && checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
            if (!TextUtils.isEmpty(deviceId)) {
                return deviceId;
            }
            deviceId = telephonyManager.getSimSerialNumber();
            if (!TextUtils.isEmpty(deviceId)) {
                return deviceId;
            }
            deviceId = telephonyManager.getSubscriberId();
            if (!TextUtils.isEmpty(deviceId)) {
                return deviceId;
            }
        }
        deviceId = Build.SERIAL;
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        deviceId = getAndroidIdentifier(context);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        return UUID.randomUUID().toString();
    }

    public static File getCacheFile(Context context) {
        File file = new File(context.getFilesDir(), "mobclick_agent_cached");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String getInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        StringBuilder stringBuilder = new StringBuilder(200);
        for (ApplicationInfo applicationInfo : installedApplications) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                stringBuilder.append(applicationInfo.loadLabel(packageManager)).append(",");
            }
        }
        if (stringBuilder.length() > 1) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
