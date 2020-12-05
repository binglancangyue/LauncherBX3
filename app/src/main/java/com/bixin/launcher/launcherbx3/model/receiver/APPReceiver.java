package com.bixin.launcher.launcherbx3.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bixin.launcher.launcherbx3.model.LauncherApp;
import com.bixin.launcher.launcherbx3.model.tools.CallBackManagement;


/**
 * @author Altair
 * @date :2020.09.23 下午 03:10
 * @description:
 */
public class APPReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        Log.d("APPReceiver", "onReceive:action:" + action + ";");
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED) ||
                action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            LauncherApp.getInstance().initAppList();
            CallBackManagement.getInstance().updateAppList();
        }
    }
}
