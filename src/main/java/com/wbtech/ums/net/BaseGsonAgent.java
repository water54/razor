package com.wbtech.ums.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by hawk.zheng on 2016/4/16.
 */
public class BaseGsonAgent {
    public Gson gson;

    private BaseGsonAgent() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public static BaseGsonAgent getInstance() {
        return GsonHolder.gsonAgent;
    }

    private static class GsonHolder {
        private static BaseGsonAgent gsonAgent = new BaseGsonAgent();
    }
}
