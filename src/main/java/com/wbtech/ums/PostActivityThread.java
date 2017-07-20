package com.wbtech.ums;

import android.content.Context;

import com.wbtech.ums.bean.ActivityInfo;
import com.wbtech.ums.common.CommonUtil;
import com.wbtech.ums.common.NetworkUitlity;
import com.wbtech.ums.common.UmsConstants;
import com.wbtech.ums.net.BaseGsonAgent;
import com.wbtech.ums.objects.MyMessage;
import com.wbtech.ums.storage.RazorDbOperation;

/**
 * Created by hawk.zheng on 2017/7/6.
 */

class PostActivityThread extends Thread {
    ActivityInfo activityInfo;
    private Context context;

    PostActivityThread(ActivityInfo activityInfo, Context context) {
        this.activityInfo = activityInfo;
        if (context != null) {
            this.context = context.getApplicationContext();
        } else {
            this.context = context;
        }
    }

    @Override
    public void run() {
        if (1 == CommonUtil.getReportPolicyMode(context) && CommonUtil.isNetworkAvailable(context)) {
            String info = BaseGsonAgent.getInstance().gson.toJson(activityInfo);
            CommonUtil.printLog("activityInfo", info);

            MyMessage message = NetworkUitlity.sendPostRequestByHttpConnection(UmsConstants.preUrl + UmsConstants.activityUrl, info);
            if (!message.isFlag()) {
//                MessageCenter.getInstance().sendMessage(new SaveBean(context, "activityInfo", activityInfo));
                insert(context, activityInfo);
            }
        } else {
            insert(context, activityInfo);
//            MessageCenter.getInstance().sendMessage(new SaveBean(context, "activityInfo", activityInfo));
        }
    }

    private void insert(Context context, ActivityInfo info) {
        RazorDbOperation razorDbOperation = RazorDbOperation.getInstance();
        razorDbOperation.insert(context, info);
    }
}
