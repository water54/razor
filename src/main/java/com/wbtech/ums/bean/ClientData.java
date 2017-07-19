package com.wbtech.ums.bean;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.wbtech.ums.common.CommonUtil;
import com.wbtech.ums.objects.LatitudeAndLongitude;
import com.wbtech.ums.objects.SCell;

/**
 * Created by hawk.zheng on 2017/7/3.
 */

public class ClientData {

    /**
     * phonetype : 1
     * os_version : 7.0
     * platform : android
     * productkey : internal
     * language : zh
     * deviceid : 861945034530497
     * tdId : 3a13687b675cb17b47769525e0b581458
     * appkey : internal
     * resolution : 1080x1920
     * ismobiledevice : true
     * network : wifi
     * time : 2017-07-03 18:51:57
     * version : 4.6.0.debug
     * userid :
     * androidid : e63bc9d1b94c50b2
     * mccmnc :
     * cellid :
     * lac :
     * modulename : gemini
     * devicename : Xiaomi MI 5
     * wifimac : 02:00:00:00:00:00
     * havewifi : true
     * havegps : true
     * havegravity : true
     * installedApp : 语音设置,花生地铁WiFi,小米风行播放插件,微信
     * latitude : 31.211009
     * longitude : 121.46712
     */

    public int phonetype;

    public String os_version;
    public String platform = "android";
    public String productkey;
    public String language;
    public String deviceid;
    public String tdId;
    public String appkey;
    public String resolution;
    public boolean ismobiledevice = true;
    public String network;
    public String time;
    public String version;
    public String userid;
    public String androidid;
    public String mccmnc;
    public String cellid;
    public String lac;
    public String modulename;
    public String devicename;
    public String wifimac;
    public boolean havewifi;
    public boolean havegps;
    public boolean havegravity;
    public String installedApp;
    public String latitude;
    public String longitude;

    public String imsi;
    public boolean havebt;
    public String pk_campaign;
    public String pk_kwd;
    public String pageVariable_0;

    public ClientData(Context context, String pk_campaign, String pk_kwd, boolean mUseLocationService, String pageVariable) {
        pageVariable_0 = pageVariable;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displaysMetrics);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            if (CommonUtil.checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
                TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
                imsi = tm.getSubscriberId();
                phonetype = tm.getPhoneType();
            }
            productkey = CommonUtil.getAppKey(context);
            os_version = Build.VERSION.RELEASE;

            language = context.getResources().getConfiguration().locale.getLanguage();
            deviceid = CommonUtil.getDeviceId(context);//
            tdId = "talkingDataId";//TalkingDataAppCpa.getDeviceId(context);
            appkey = CommonUtil.getAppKey(context);
            resolution = displaysMetrics.widthPixels + "x" + displaysMetrics.heightPixels;

            network = CommonUtil.getNetworkTypeWIFI2G3G(context);
            time = CommonUtil.getTime();
            version = CommonUtil.getVersion(context);
            userid = CommonUtil.getUserIdentifier(context);
            androidid = CommonUtil.getAndroidIdentifier(context);

            SCell sCell = CommonUtil.getCellInfo(context);

            mccmnc = sCell != null ? "" + sCell.MCCMNC : "";
            cellid = sCell != null ? sCell.CID + "" : "";
            lac = sCell != null ? sCell.LAC + "" : "";
            modulename = Build.PRODUCT;
            devicename = CommonUtil.getDeviceName();
            wifimac = wifiManager.getConnectionInfo().getMacAddress();
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                havebt = adapter != null;
            }
            if (!TextUtils.isEmpty(pk_campaign) || !TextUtils.isEmpty(pk_kwd)) {
                this.pk_campaign = pk_campaign;
                this.pk_kwd = pk_kwd;
            }
            havewifi = CommonUtil.isWiFiActive(context);
            havegps = locationManager != null;
            havegravity = CommonUtil.isHaveGravity(context);
            installedApp = CommonUtil.getInstalledApp(context);
            LatitudeAndLongitude coordinates = CommonUtil.getLatitudeAndLongitude(context, mUseLocationService);
            latitude = coordinates.latitude;
            longitude = coordinates.longitude;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
