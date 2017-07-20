package com.wbtech.ums;

import android.content.Context;

import com.wbtech.ums.bean.ClientData;
import com.wbtech.ums.common.CommonUtil;
import com.wbtech.ums.common.NetworkUitlity;
import com.wbtech.ums.common.UmsConstants;
import com.wbtech.ums.net.BaseGsonAgent;
import com.wbtech.ums.objects.MyMessage;
import com.wbtech.ums.storage.RazorDbOperation;

/**
 * Created by hawk.zheng on 2017/7/6.
 */

public class PostClientData extends Thread {
    private Context context;
    private String pk_campaign;
    private String pk_kwd;
    private String pageVariable_0;

    public PostClientData(Context context, String pk_campaign, String pk_kwd, String pageVariable_0) {
        if (context != null) {
            this.context = context.getApplicationContext();
        } else {
            this.context = context;
        }
        this.pk_campaign = pk_campaign;
        this.pk_kwd = pk_kwd;
        this.pageVariable_0 = pageVariable_0;
    }

    @Override
    public void run() {
        ClientData clientData = new ClientData(context, pk_campaign, pk_kwd, UmsAgent.mUseLocationService, pageVariable_0);
        if (1 == CommonUtil.getReportPolicyMode(context) && CommonUtil.isNetworkAvailable(context)) {
            MyMessage message = NetworkUitlity.sendPostRequestByHttpConnection(UmsConstants.preUrl + UmsConstants.clientDataUrl, BaseGsonAgent.getInstance().gson.toJson(clientData));
            if (!message.isFlag()) {
                RazorDbOperation razorDbOperation = RazorDbOperation.getInstance();
                razorDbOperation.insert(context, clientData);

            }
        } else {
            RazorDbOperation razorDbOperation = RazorDbOperation.getInstance();
            razorDbOperation.insert(context, clientData);
        }
    }
}
