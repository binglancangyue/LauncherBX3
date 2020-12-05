package com.bixin.launcher.launcherbx3.model.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.bixin.launcher.launcherbx3.R;
import com.bixin.launcher.launcherbx3.model.LauncherApp;

import static android.content.ContentValues.TAG;

/**
 * @author Altair
 * @date :2019.10.21 下午 02:18
 * @description:
 */
public class StartActivityTool {
    private Context mContext;
    private ContentResolver mResolver;

    public StartActivityTool() {
        this.mContext = LauncherApp.getInstance();
        this.mResolver = mContext.getContentResolver();
    }

    /**
     * 根据action跳转
     *
     * @param action action
     */
    public void jumpByAction(String action) {
        Intent intent = new Intent(action);
        mContext.startActivity(intent);
    }

    public void jumpToActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        mContext.startActivity(intent);
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    public void launchAppByPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "package name is null!");
            return;
        }

        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastTool.showToast(R.string.app_not_install);
        } else {
            mContext.startActivity(launchIntent);
        }
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    public void launchAppByPackageName(String packageName, String name) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "package name is null!");
            return;
        }

        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastTool.showToast(R.string.app_not_install);
        } else {
            mContext.startActivity(launchIntent);
            sendBToRCX(packageName, name);
        }
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    public void launchAppByPackageName(String packageName, boolean isTrue) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "package name is null!");
            return;
        }

        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastTool.showToast(R.string.app_not_install);
        } else {
            launchIntent.putExtra("front", isTrue);
            mContext.startActivity(launchIntent);
        }
    }

    /**
     * 返回桌面
     */
    public void goHome() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        mContext.startActivity(intent);
    }

    public void openTXZView() {
        Intent it = new Intent("com.txznet.txz.config.change");
        it.putExtra("type", "screen");
        it.putExtra("x", 0);
        it.putExtra("y", 0);
//        it.putExtra("width", 1024);
//        it.putExtra("height", 517);
        mContext.sendBroadcast(it);
//        TXZAsrManager.getInstance().triggerRecordButton();
    }


    @SuppressLint("NewApi")
    public void startVoiceRecognitionService() {
        Log.d(TAG, "startVoiceRecognitionService: ");
//        Intent intent = new Intent();
//        String packageName = "com.bixin.speechrecognitiontool";
//        String className = "com.bixin.speechrecognitiontool.SpeechRecognitionService";
//        intent.setClassName(packageName, className);
//        mContext.startForegroundService(intent);
        launchAppByPackageName("com.bixin.speechrecognitiontool");
    }

    public int getWeatherIcon(String weather) {
        Log.d(TAG, "getWeatherIcon: weather " + weather);
        int iconId = 0;
        switch (weather) {
            case "多云":
                iconId = R.drawable.ic_weather_cloudy;
                break;
            case "阴":
            case "阴天":
                iconId = R.drawable.ic_weather_cloudy_day;
                break;
            case "阵雨":
                iconId = R.drawable.ic_weather_shower;
                break;
            case "雷阵雨伴有冰雹":
                iconId = R.drawable.ic_weather_thunder_shower;
                break;
            case "雷阵雨":
                iconId = R.drawable.ic_weather_thunder_storm_and_hail;
                break;
            case "小雨":
            case "雨":
                iconId = R.drawable.ic_weather_light_rain;
                break;
            case "中雨":
                iconId = R.drawable.ic_weather_moderate_rain;
                break;
            case "大雨":
                iconId = R.drawable.ic_weather_heavy_rain;
                break;
            case "暴雨":
                iconId = R.drawable.ic_weather_baoyu;
                break;
            case "大暴雨":
                iconId = R.drawable.ic_weather_dabaoyu;
                break;
            case "特暴大雨":
                iconId = R.drawable.ic_weather_torrential_rain;
                break;
            case "冰雨":
                iconId = R.drawable.ic_weather_ice_rain;
                break;
            case "小雪":
                iconId = R.drawable.ic_weather_light_snow;
                break;
            case "中雪":
                iconId = R.drawable.ic_weather_medium_snow;
                break;
            case "大雪":
                iconId = R.drawable.ic_weather_heavy_snow;
                break;
            case "暴雪":
                iconId = R.drawable.ic_weather_greate_heavy_snow;
                break;
            case "雨夹雪":
                iconId = R.drawable.ic_weather_sleet;
                break;
            case "阵雪":
                iconId = R.drawable.ic_weather_snow_shower;
                break;
            case "雾":
                iconId = R.drawable.ic_weather_fog;
                break;
            case "霾":
                iconId = R.drawable.ic_weather_haze;
                break;
            case "浮尘":
                iconId = R.drawable.ic_weather_float_dust;
                break;
            case "扬沙":
                iconId = R.drawable.ic_weather_raise_sand;
                break;
            case "沙尘暴":
                iconId = R.drawable.ic_weather_dust_storm;
                break;
            default:    // 晴天
                iconId = R.drawable.ic_weather_sunny_day;
                break;

        }
        return iconId;
    }

    public void startRCX() {
        LauncherApp.getInstance().startService(
                new Intent().setClassName("com.mapgoo.diruite",
                        "com.mapgoo.smart.service.DataSyncServiceSDK"));
    }

    public void sendBToRCX(String packageName, String name) {
        Intent intent = new Intent();
        intent.setAction("com.mapgoo.broadcast.recv.renchexing");
        intent.putExtra("KEY_TYPE", 10017);
        intent.putExtra("EXTRA_PKG", packageName);
        intent.putExtra("EXTRA_NAME", name);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mContext.sendBroadcast(intent);
    }

    public void getAllContacts() {
        if (mResolver == null) {
            mResolver = mContext.getContentResolver();
        }
        Uri uri = ContactsContract.Data.CONTENT_URI;
        Cursor cursorUser = mResolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
        boolean isSave = false;
        if (cursorUser != null) {
            while (cursorUser.moveToNext()) {
                int id = cursorUser.getInt(0); // 按上面数组的声明顺序获取
                String name = cursorUser.getString(1);
                String rawContactsId = cursorUser.getString(2);
                if (name.equals("明台產物保險公司")) {
                    if (rawContactsId.equals("0907913491")) {
                        isSave = true;
                    }
                }
            }
            cursorUser.close();
        }
        if (!isSave) {
            addContact("明台產物保險公司", "0907913491");
            Log.d(TAG, "getAllContacts: 添加 明台產物保險公司");
        } else {
            Log.d(TAG, "getAllContacts:已经保存 明台產物保險公司");
        }
    }

    private void addContact(String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = mResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        mResolver.insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        mResolver.insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();
    }

}
