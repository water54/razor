package com.wbtech.ums;

import android.content.Context;

import com.wbtech.ums.bean.EventInfo;
import com.wbtech.ums.common.CommonUtil;
import com.wbtech.ums.common.NetworkUitlity;
import com.wbtech.ums.common.UmsConstants;
import com.wbtech.ums.net.BaseGsonAgent;
import com.wbtech.ums.objects.MyMessage;
import com.wbtech.ums.storage.RazorDbOperation;

/**
 * Created by hawk.zheng on 2017/7/6.
 */

class PostEventThread extends Thread {
    private EventInfo eventInfo;
    private Context context;

    PostEventThread(Context context, EventInfo eventInfo) {
        if (context != null) {
            this.context = context.getApplicationContext();
        } else {
            this.context = context;
        }
        this.eventInfo = eventInfo;
    }

    @Override
    public void run() {
        if (!eventInfo.verification()) {
            CommonUtil.printLog("UMSAgent", "Illegal value of acc in postEventInfo");
            return;
        }

        if (1 == CommonUtil.getReportPolicyMode(context) && CommonUtil.isNetworkAvailable(context)) {
            try {
                String localJSONObject = BaseGsonAgent.getInstance().gson.toJson(eventInfo);
                MyMessage info = NetworkUitlity.sendPostRequestByHttpConnections(UmsConstants.preUrl + UmsConstants.eventUrl, localJSONObject);
                if (!info.isFlag()) {
                    insert(context, eventInfo);
//                    MessageCenter.getInstance().sendMessage(new SaveBean(context, "eventInfo", eventInfo));
                }
            } catch (Exception e) {
                CommonUtil.printLog("UmsAgent", "fail to post eventContent");
            }
        } else {
            insert(context, eventInfo);
//            MessageCenter.getInstance().sendMessage(new SaveBean(context, "eventInfo", eventInfo));
        }

    }


    private void insert(Context context, EventInfo info) {
        RazorDbOperation razorDbOperation = RazorDbOperation.getInstance();
        razorDbOperation.insert(context, info);
    }
}
