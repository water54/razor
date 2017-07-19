package com.wbtech.ums.common;

import android.text.TextUtils;

import okhttp3.Request;

/**
 * Created by hawk.zheng on 2017/5/25.
 */

public class OkHttpRequestBuilder {
    public Request.Builder getBuilder(String url, String tag) {
        Request.Builder builder = new Request.Builder().url(url);
        if (!TextUtils.isEmpty(tag)) {
            builder = builder.tag(tag);
        }
        return builder;
    }
}
