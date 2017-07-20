package com.wbtech.ums.net;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.wbtech.ums.objects.AbstractReturnObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hawk.zheng on 2017/5/25.
 */

public class OkHttpProxy {
    private static OkHttpProxy okHttpProxy;
    public OkHttpClient mOkHttpClient;
    private OkHttpRequestBuilder requestBuilder;
    private MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/plain; charset=utf-8");

    public void init(Context context) {
        if (mOkHttpClient != null) return;
//        Stetho.initializeWithDefaults(context);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        File cacheDir = new File(context.getCacheDir(), "netCache");
        boolean cacheDirStatus = true;
        if (!cacheDir.exists()) cacheDirStatus = cacheDir.mkdirs();
        if (cacheDirStatus) {
            Cache cache = new Cache(cacheDir, 5 * 1024 * 1024);
            builder.cache(cache);
        }
        builder.readTimeout(20, TimeUnit.SECONDS).connectTimeout(20, TimeUnit.SECONDS)
        ;
        mOkHttpClient = builder.build();
    }

    private OkHttpProxy() {
        requestBuilder = new OkHttpRequestBuilder();
    }

    public static OkHttpProxy getInstance() {
        if (okHttpProxy == null) {
            synchronized (OkHttpProxy.class) {
                if (okHttpProxy == null) okHttpProxy = new OkHttpProxy();
            }
        }
        return okHttpProxy;
    }

    public AbstractReturnObject getSync(String url, String tag, OkHttpRequestBuilder requestBuilder) throws IOException {
        Request.Builder builder = requestBuilder.getBuilder(url, tag);
        Call call = mOkHttpClient.newCall(builder.build());
        Response execute = call.execute();
        return new AbstractReturnObject(execute.code(), execute.body().string());
    }

    public AbstractReturnObject getSync(String url, String tag) throws IOException {
        return getSync(url, tag, requestBuilder);
    }

    public Call getAsync(String url, String tag, OkHttpRequestBuilder requestBuilder, OnResultCallBack callBack) {
        Request.Builder builder = requestBuilder.getBuilder(url, tag);
        Call call = mOkHttpClient.newCall(builder.build());
        dealCallBack(callBack, call);
        return call;
    }

    private void dealCallBack(final OnResultCallBack callBack, Call call) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onFailed(-1, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack == null) return;
                if (response.isSuccessful()) {
                    callBack.onSuccess(response.body().string());
                } else {
                    callBack.onFailed(response.code(), response.body().string());
                }
            }
        });
    }

    public Call getAsync(String url, String tag, final OnResultCallBack callBack) {
        return getAsync(url, tag, requestBuilder, callBack);
    }


    public AbstractReturnObject postSync(String url, HashMap<String, String> params, String tag, OkHttpRequestBuilder request) throws IOException {
        Request httpRequest = getFormRequest(url, params, tag, request);
        Response execute = mOkHttpClient.newCall(httpRequest).execute();
        return new AbstractReturnObject(execute.code(), execute.body().string());
    }

    public AbstractReturnObject postSync(String url, HashMap<String, String> params, String tag) throws IOException {
        return postSync(url, params, tag, requestBuilder);
    }

    public Call postAsync(String url, HashMap<String, String> params, String tag, OkHttpRequestBuilder request, OnResultCallBack callBack) {
        Request httpRequest = getFormRequest(url, params, tag, request);
        Call call = mOkHttpClient.newCall(httpRequest);
        dealCallBack(callBack, call);
        return call;
    }

    public Call postAsync(String url, HashMap<String, String> params, String tag, OnResultCallBack callBack) {
        return postAsync(url, params, tag, requestBuilder, callBack);
    }


    public Request getFormRequest(String url, HashMap<String, String> params, String tag, OkHttpRequestBuilder request) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> next : entries) {
                String value = next.getValue();
                if (!TextUtils.isEmpty(value))
                    builder.add(next.getKey(), value);
            }
        }
        Request.Builder requestBuilder = request.getBuilder(url, tag);
        return requestBuilder.post(builder.build()).build();
    }

    public AbstractReturnObject postSync(String url, String data, String tag, OkHttpRequestBuilder requestBuilder) throws IOException {
        Request.Builder builder = requestBuilder.getBuilder(url, tag).post(RequestBody.create(MEDIA_TYPE_MARKDOWN, data));
        Response execute = mOkHttpClient.newCall(builder.build()).execute();
        return new AbstractReturnObject(execute.code(), execute.body().string());
    }

    public synchronized void clearHttpClient(Context context) {
        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        if (dispatcher == null) return;
        List<Call> queuedCalls = dispatcher.queuedCalls();
        for (int i = queuedCalls.size() - 1; i >= 0; i--) {
            queuedCalls.get(i).cancel();
        }
//        PersistentCookieStore.getInstance(context).removeAll();
    }

    public void cancelCall(Call call) {
        if (call == null) return;
        if (call.isCanceled()) return;
        call.cancel();
    }

    public void cancelRequests(String tag) {
        if (TextUtils.isEmpty(tag)) return;
        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        if (dispatcher == null) return;
        List<Call> queuedCalls = dispatcher.queuedCalls();
        cancelCallByTag(tag, queuedCalls);
        cancelCallByTag(tag, dispatcher.runningCalls());
    }

    private void cancelCallByTag(String tag, List<Call> queuedCalls) {
        if (queuedCalls == null || queuedCalls.size() == 0) return;
        for (Call call : queuedCalls) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }


    @Deprecated
    public void sendARequest(String url, HashMap<String, String> params, String tag, @NonNull Callback callback) {
        sendARequest(url, params, tag, callback, requestBuilder);
    }

    @Deprecated
    public void sendARequest(String url, HashMap<String, String> params, String tag, @NonNull Callback callback, OkHttpRequestBuilder requestBuilder) {
        Request request;
        if (params == null || params.size() == 0) {
            request = requestBuilder.getBuilder(url, tag).build();
        } else {
            request = getFormRequest(url, params, tag, requestBuilder);
        }
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public void sendARequest(String url, HashMap<String, String> params, String tag, OnResultCallBack callBack, OkHttpRequestBuilder requestBuilder) {
        Request request;
        if (params == null || params.size() == 0) {
            request = requestBuilder.getBuilder(url, tag).build();
        } else {
            request = getFormRequest(url, params, tag, requestBuilder);
        }
        Call call = mOkHttpClient.newCall(request);
        dealCallBack(callBack, call);
    }

    public void sendARequest(String url, HashMap<String, String> params, String tag, OnResultCallBack callBack) {
        sendARequest(url, params, tag, callBack, requestBuilder);
    }

}
