package com.wbtech.ums.objects;

import android.content.Context;

import com.wbtech.ums.common.CommonUtil;
import com.wbtech.ums.common.NetworkUitlity;
import com.wbtech.ums.common.UmsConstants;
import com.wbtech.ums.storage.RazorDbOperation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hawk.zheng on 2017/7/4 .
 */

public class UploadAllData implements Runnable {
    Context context;

    public UploadAllData(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        RazorDbOperation razorDbOperation = RazorDbOperation.getInstance();
        DataBaseResult activityInfo = razorDbOperation.getActivityInfo(context);

        DataBaseResult eventInfo = razorDbOperation.getEventInfo(context);

        DataBaseResult clientData = razorDbOperation.getClientData(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("activityInfo", activityInfo.jsonArrayData);
            jsonObject.put("eventInfo", eventInfo.jsonArrayData);
            jsonObject.put("appkey", CommonUtil.getAppKey(context));
            jsonObject.put("clientData", clientData.jsonArrayData);
            MyMessage message = NetworkUitlity.sendPostRequestByHttpConnection(UmsConstants.preUrl + UmsConstants.uploadUrl, jsonObject.toString());
            if (message.flag) {
                razorDbOperation.deleteDataById(context, "activityInfo", activityInfo.ids);

                razorDbOperation.deleteDataById(context, "eventInfo", eventInfo.ids);
//                razorDbOperation.deleteDataById(context, "clientData", eventInfo.ids);
            }
            context = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
