package com.wbtech.ums.message;

/**
 * Created by hawk.zheng on 2017/7/6.
 */

public class MessageCenter {
    private static MessageCenter messageCenter;
    private MessageHandlerThread messageHandler;

    private MessageCenter() {
    }

    public static MessageCenter getInstance() {
        if (messageCenter == null) {
            synchronized (MessageCenter.class) {
                if (messageCenter == null) messageCenter = new MessageCenter();
            }
        }
        return messageCenter;
    }


    public void sendMessage(Runnable runnable, long delay) {
        if (messageHandler == null) {
            messageHandler = new MessageHandlerThread();
            messageHandler.start();
        }
        messageHandler.postMessage(runnable, delay);
    }

    public void sendMessage(Runnable runnable) {
        sendMessage(runnable, 0);
    }

    public void quite() {
        if (messageHandler != null) {
            messageHandler.quite();
            messageHandler = null;
        }
    }
}
