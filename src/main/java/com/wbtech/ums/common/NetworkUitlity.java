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

import com.wbtech.ums.net.OkHttpProxy;
import com.wbtech.ums.net.OkHttpRequestBuilder;
import com.wbtech.ums.objects.AbstractReturnObject;
import com.wbtech.ums.objects.MyMessage;

import org.json.JSONObject;

public class NetworkUitlity {
    private static OkHttpRequestBuilder requestBuilder;

    private static OkHttpRequestBuilder getRequestBuilder() {
        if (requestBuilder == null) {
            synchronized (NetworkUitlity.class) {
                if (requestBuilder == null) requestBuilder = new OkHttpRequestBuilder();
            }
        }
        return requestBuilder;
    }

    public static MyMessage sendPostRequestByHttpConnection(String url, String data) {
        MyMessage message = new MyMessage();
        try {
            AbstractReturnObject abstractReturnObject = OkHttpProxy.getInstance().postSync(url, "content=" + data, null, getRequestBuilder());
            message.setFlag(abstractReturnObject.code == 200);
            message.setMsg(abstractReturnObject.message);
        } catch (Exception e) {
            message.setFlag(false);
            message.setMsg(e.getMessage());
        }

        return message;
    }

    public static MyMessage sendPostRequestByHttpConnections(String url, String data) {
        MyMessage message = new MyMessage();
        try {
            AbstractReturnObject abstractReturnObject = OkHttpProxy.getInstance().postSync(url, "content=" + data, null, getRequestBuilder());
            if (abstractReturnObject.code == 200) {
                JSONObject object = new JSONObject(abstractReturnObject.message);
                message.setFlag(object.optInt("flag") == 1);
                message.setMsg(object.optString("msg"));
            } else {
                message.setFlag(false);
            }
        } catch (Exception e) {
            message.setFlag(false);
        }
        return message;
    }


}
