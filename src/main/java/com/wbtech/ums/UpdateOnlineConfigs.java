package com.wbtech.ums;

import android.content.Context;

import com.wbtech.ums.common.CommonUtil;
import com.wbtech.ums.common.NetworkUitlity;
import com.wbtech.ums.common.UmsAgentStorage;
import com.wbtech.ums.common.UmsConstants;
import com.wbtech.ums.objects.MyMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by hawk.zheng on 2017/7/6.
 */

class UpdateOnlineConfigs extends Thread {
    private Context context;

    UpdateOnlineConfigs(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        } else {
            this.context = context;
        }
    }

    @Override
    public void run() {
        String appkey = CommonUtil.getAppKey(context);
        JSONObject map = new JSONObject();
        try {
            map.put("appkey", appkey);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        String appkeyJSON = map.toString();
        if (!CommonUtil.isNetworkAvailable(context)) {
            return;
        }
        try {
            MyMessage message = NetworkUitlity.sendPostRequestByHttpConnection(UmsConstants.preUrl + UmsConstants.onlineConfigUrl, appkeyJSON);
            CommonUtil.printLog("message", message.getMsg());
            if (message.isFlag()) {
                JSONObject object = new JSONObject(message.getMsg());

                if (UmsConstants.DebugMode) {
                    CommonUtil.printLog("uploadJSON", object.toString());
                }

                Iterator<String> iterator = object.keys();

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = object.getString(key);
                    UmsAgentStorage.getInstance(context).writeString(key, value);
                    if (key.equals("autogetlocation") && (!value.equals("1"))) {
                        UmsAgent.mUseLocationService = (false);
                    }
                    if (key.equals("reportpolicy") && (value.equals("1"))) {
                        UmsAgent.setDefaultReportPolicy(context, 1);
                    }
                    if (key.equals("sessionmillis")) {
                        UmsConstants.kContinueSessionMillis = Integer.parseInt(value) * 1000;
                    }
                }

            } else {
                CommonUtil.printLog("error", message.getMsg());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
