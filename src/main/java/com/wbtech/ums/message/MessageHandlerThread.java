package com.wbtech.ums.message;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Semaphore;

/**
 * Created by hawk.zheng on 2017/7/6.
 */

class MessageHandlerThread extends Thread {
    private Handler handler;
    private Semaphore semaphore = new Semaphore(1);

    void postMessage(Runnable runnable, long delay) {
        try {
            semaphore.acquire();
            if (delay == 0) {
                handler.post(runnable);
            } else {
                handler.postDelayed(runnable, delay);
            }
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void quite() {
        if (handler != null) {
            if (Build.VERSION.SDK_INT >= 18) {//支持最小版本版本大于18后可以删除
                handler.getLooper().quitSafely();
            } else {
                handler.getLooper().quit();
            }
        }
    }

    MessageHandlerThread() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        handler = new Handler();
        semaphore.release();
        Looper.loop();

    }
}
