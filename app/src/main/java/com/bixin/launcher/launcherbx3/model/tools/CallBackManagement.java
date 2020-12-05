package com.bixin.launcher.launcherbx3.model.tools;


import com.bixin.launcher.launcherbx3.model.listener.OnAppUpdateListener;
import com.bixin.launcher.launcherbx3.model.listener.OnLocationListener;

/**
 * @author Altair
 * @date :2020.09.23 上午 10:23
 * @description: 接口回调管理类
 */
public class CallBackManagement {
    private OnLocationListener mOnLocationListener;
    private OnAppUpdateListener mOnAppUpdateListener;

    public static CallBackManagement getInstance() {
        return SingleHolder.management;
    }

    public static class SingleHolder {
        static CallBackManagement management = new CallBackManagement();
    }

    public void setOnAppUpdateListener(OnAppUpdateListener onAppUpdateListener) {
        this.mOnAppUpdateListener = onAppUpdateListener;
    }

    public void setOnLocationListener(OnLocationListener listener) {
        this.mOnLocationListener = listener;
    }

    public void gpsSpeedChange() {
        if (mOnLocationListener == null) {
            return;
        }
        mOnLocationListener.gpsSpeedChanged();
    }

    public void updateAppList() {
        if (mOnAppUpdateListener != null) {
            mOnAppUpdateListener.updateAppList();
        }
    }

    public void updateWeather(String weatherInfo) {
        if (mOnLocationListener != null) {
            mOnLocationListener.updateWeather(weatherInfo);
        }
    }

    public void releaseAll() {
        if (mOnAppUpdateListener != null) {
            mOnAppUpdateListener = null;
        }
        if (mOnLocationListener != null) {
            mOnLocationListener = null;
        }
    }

}
