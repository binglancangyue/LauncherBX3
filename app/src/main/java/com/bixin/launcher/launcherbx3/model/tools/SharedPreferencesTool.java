package com.bixin.launcher.launcherbx3.model.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.bixin.launcher.launcherbx3.model.LauncherApp;



/**
 * @author Altair
 * @date :2019.10.28 上午 10:03
 * @description: SharedPreferences 工具
 */
public class SharedPreferencesTool {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesTool() {
        this.mContext = LauncherApp.getInstance();
        this.mPreferences = mContext.getSharedPreferences("Launcher", Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    public SharedPreferences getSharePreferences() {
        return mPreferences;
    }

    public void saveString(String name, String value) {
        mEditor.putString(name, value);
        mEditor.apply();
    }

    public void saveInt(String name, int value) {
        mEditor.putInt(name, value);
        mEditor.apply();
    }

    public void saveBoolean(String name, boolean value) {
        mEditor.putBoolean(name, value);
        mEditor.apply();
    }
}
