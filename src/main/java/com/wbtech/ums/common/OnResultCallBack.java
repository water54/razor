package com.wbtech.ums.common;

/**
 * Created by hawk.zheng on 2017/3/18.
 */
public interface OnResultCallBack {
    void onSuccess(String t);

    void onFailed(int errorCode, String errorInfo);
}
