package com.wbtech.ums.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class UmsAgentStorage {
    static UmsAgentStorage umsAgentStorage;
    private SharedPreferences sp;

    private UmsAgentStorage(Context con) {
        sp = con.getSharedPreferences("UmsAgentStorage", Context.MODE_PRIVATE);
    }

    public static UmsAgentStorage getInstance(Context context) {
        if (umsAgentStorage == null) {
            synchronized (UmsAgentStorage.class) {
                if (umsAgentStorage == null) umsAgentStorage = new UmsAgentStorage(context);
            }
        }
        return umsAgentStorage;
    }

    public void writeString(String name, String value) {
        Editor editor = sp.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public boolean containsString(String name) {
        return sp.contains(name) && !TextUtils.isEmpty(sp.getString(name, null));
    }


    public String readString(String name) {
        return sp.getString(name, null);
    }

    public String readString(String name, String defaultString) {
        return sp.getString(name, defaultString);
    }


    public void writeInt(String name, int value) {
        Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.apply();

    }

    public int readInt(String name) {
        return sp.getInt(name, -1);
    }

    public int readInt(String name, int defaultValue) {
        return sp.getInt(name, defaultValue);
    }

    public long readLong(String name, long defaultValue) {
        return sp.getLong(name, defaultValue);
    }

    public void writeLong(String name, long value) {
        Editor editor = sp.edit();
        editor.putLong(name, value);
        editor.apply();
    }

    public void writeFloat(String name, float value) {
        Editor editor = sp.edit();
        editor.putFloat(name, value);
        editor.apply();
    }

    public float readFloat(String name) {
        return sp.getFloat(name, -1);
    }

    public float readFloat(String name, float defaultValue) {
        return sp.getFloat(name, defaultValue);
    }

    public void writeBoolean(String name, boolean value) {
        Editor editor = sp.edit();
        editor.putBoolean(name, value);
        editor.apply();

    }

    public void removeContent(String key) {
        Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        Editor editor = sp.edit();
        editor.clear().apply();
    }

    public boolean readBoolean(String name) {
        return sp.getBoolean(name, false);
    }

}
