package com.bixin.launcher.launcherbx3.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bixin.launcher.launcherbx3.R;
import com.bixin.launcher.launcherbx3.model.bean.Customer;
import com.bixin.launcher.launcherbx3.model.listener.OnLocationListener;
import com.bixin.launcher.launcherbx3.model.tools.StartActivityTool;
import com.bixin.launcher.launcherbx3.model.tools.StoragePaTool;

import java.io.File;
import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;

public class T10LaunchHomeActivity extends BaseActivity implements View.OnClickListener, OnLocationListener {
    private StartActivityTool mStartActivityTool;
    private AlertDialog selectTimeDialog;
    private RadioButton rb8;
    private RadioButton rb16;
    private RadioButton rb24;
    private RadioButton rb48;
    private RadioButton rbAlways;
    TextView tvMpa;
    TextView tvFM;
    TextView tvMusic;
    TextView tvCloud;
    TextView tvAPP;
    TextView tvDVR;
    private ImageView ivRecord;
    private InnerHandler mHandle;
    private static final String CAMERA_RECORD_STATUS = "camera_record_status";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.acitivity_launcher_t10;
    }

    @Override
    public void init() {
        mStartActivityTool = new StartActivityTool();
        mHandle = new InnerHandler(this);
        registerDVRContentObserver();
        mHandle.sendEmptyMessageDelayed(2, 5000);
        mHandle.sendEmptyMessageDelayed(3,10000);
        test();
    }

    @Override
    public void initView() {
        FrameLayout flDVR = findViewById(R.id.fl_dvr);
        FrameLayout flCloud = findViewById(R.id.fl_cloud);
        FrameLayout flFM = findViewById(R.id.fl_fm);
        FrameLayout flMap = findViewById(R.id.fl_map);
        FrameLayout flMusic = findViewById(R.id.fl_music);
        FrameLayout flAPP = findViewById(R.id.fl_app);
        tvCloud = findViewById(R.id.tv_cloud);
        tvDVR = findViewById(R.id.tv_dvr);
        tvMpa = findViewById(R.id.tv_map);
        tvFM = findViewById(R.id.tv_fm);
        tvMusic = findViewById(R.id.tv_music);
        tvAPP = findViewById(R.id.tv_app);
        ivRecord = findViewById(R.id.iv_record_state);
        tvCloud.setText(Html.fromHtml(getResources().getString(R.string.cloud_t10)));
        tvDVR.setText(Html.fromHtml(getResources().getString(R.string.dvr_t10)));
        tvMpa.setText(Html.fromHtml(getResources().getString(R.string.map_t10)));
        tvFM.setText(Html.fromHtml(getResources().getString(R.string.fm_t10)));
        tvMusic.setText(Html.fromHtml(getResources().getString(R.string.music_t10)));
        tvAPP.setText(Html.fromHtml(getResources().getString(R.string.app_t10)));

        flDVR.setOnClickListener(this);
        flCloud.setOnClickListener(this);
        flFM.setOnClickListener(this);
        flMap.setOnClickListener(this);
        flMusic.setOnClickListener(this);
        flAPP.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fl_dvr:
                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_DVR);
                break;
            case R.id.fl_cloud:
                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_ViDEO_PLAY_BACK);
                break;
            case R.id.fl_fm:
                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_FM);
                break;
            case R.id.fl_map:
                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_MAPS);
                break;
            case R.id.fl_music:
                mStartActivityTool.launchAppByPackageName("com.spotify.music");
                break;
            case R.id.fl_app:
                mStartActivityTool.jumpToActivity(this, AppListActivity.class);
                break;
            case R.id.rb_time_8:
                clearAll();
                rb8.setChecked(true);
                Settings.Global.putInt(getContentResolver(), Customer.SELECT_PARK_TIME, 0);
                break;
            case R.id.rb_time_16:
                clearAll();
                rb16.setChecked(true);
                Settings.Global.putInt(getContentResolver(), Customer.SELECT_PARK_TIME, 1);
                break;
            case R.id.rb_time_24:
                clearAll();
                rb24.setChecked(true);
                Settings.Global.putInt(getContentResolver(), Customer.SELECT_PARK_TIME, 2);
                break;
            case R.id.rb_time_48:
                clearAll();
                rb48.setChecked(true);
                Settings.Global.putInt(getContentResolver(), Customer.SELECT_PARK_TIME, 3);
                break;
            case R.id.rb_time_always:
                clearAll();
                rbAlways.setChecked(true);
                Settings.Global.putInt(getContentResolver(), Customer.SELECT_PARK_TIME, 4);
                break;
        }
    }


    @Override
    public void gpsSpeedChanged() {

    }

    @Override
    public void updateWeather(String weatherInfo) {
        Log.d(TAG, "updateWeather:weatherInfo " + weatherInfo);
        if (weatherInfo.equals("123")) {
            showSelectTimeDialog();
        }
    }

    private void showSelectTimeDialog() {
        if (selectTimeDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View view = View.inflate(mContext, R.layout.dialog_select_time, null);
            builder.setView(view);
            builder.setTitle(R.string.settings_park_time);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismissTimeDialog();
                }
            });
            rb8 = view.findViewById(R.id.rb_time_8);
            rb16 = view.findViewById(R.id.rb_time_16);
            rb24 = view.findViewById(R.id.rb_time_24);
            rb48 = view.findViewById(R.id.rb_time_48);
            rbAlways = view.findViewById(R.id.rb_time_always);
            rb8.setOnClickListener(this);
            rb16.setOnClickListener(this);
            rb24.setOnClickListener(this);
            rb48.setOnClickListener(this);
            rbAlways.setOnClickListener(this);
//            title.setText(R.string.close_gps);
//            message.setText(R.string.close_gps_message);
//            negativeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismissTimeDialog();
//                    sendBroadcast("CLOSE_GPS", true);
//                    InterfaceCallBackManagement.getInstance().updateView(1, true);
//                }
//            });
        }
        if (!selectTimeDialog.isShowing()) {
            updateSummary();
            selectTimeDialog.show();
        }
    }

    private void updateSummary() {
        int textId = getSelectTime();
        switch (textId) {
            case 0:
                rb8.setChecked(true);
                break;
            case 1:
                rb16.setChecked(true);
                break;
            case 2:
                rb24.setChecked(true);
                break;
            case 3:
                rb48.setChecked(true);
                break;
            case 4:
                rbAlways.setChecked(true);
                break;
        }
    }

    private void dismissTimeDialog() {
        if (selectTimeDialog != null) {
            selectTimeDialog.dismiss();
        }
    }

    private int getSelectTime() {
        return Settings.Global.getInt(getContentResolver(), Customer.SELECT_PARK_TIME, 0);
    }

    private void clearAll() {
        rb8.setChecked(false);
        rb16.setChecked(false);
        rb24.setChecked(false);
        rb48.setChecked(false);
        rbAlways.setChecked(false);
    }

    private void registerDVRContentObserver() {
        Log.d(TAG, "registerGPSContentObserver: ");
        getContentResolver().registerContentObserver(
                Settings.Global.getUriFor(CAMERA_RECORD_STATUS),
                false, mDVRContentObserver);
    }

    private void unRegisterContentObserver() {
        if (mDVRContentObserver != null) {
            getContentResolver().unregisterContentObserver(mDVRContentObserver);
        }
    }

    private final ContentObserver mDVRContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mHandle.sendEmptyMessage(0);
        }
    };

    private void isShowRecordIcon() {
        int state = 1;
        try {
            state = Settings.Global.getInt(getContentResolver(), CAMERA_RECORD_STATUS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "dvr state: " + state);
        if (state == 1) {
            ivRecord.setVisibility(View.VISIBLE);
        } else {
            ivRecord.setVisibility(View.GONE);
        }
    }

    public static class InnerHandler extends Handler {
        private final T10LaunchHomeActivity activity;

        private InnerHandler(T10LaunchHomeActivity activity) {
            WeakReference<T10LaunchHomeActivity> activityWeakReference = new WeakReference<>(activity);
            this.activity = activityWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                activity.isShowRecordIcon();
            }
            if (msg.what == 2) {
                activity.startDVRService();
            }
            if (msg.what == 3){
                activity.startValidationTools();
            }
            removeMessages(msg.what);
        }
    }

    @SuppressLint("NewApi")
    private void startDVRService() {
        Intent launchIntent = mContext.getPackageManager()
                .getLaunchIntentForPackage(Customer.PACKAGE_NAME_DVR);
        if (launchIntent == null) {
            mHandle.sendEmptyMessageDelayed(2, 1000);
        } else {
            mContext.startActivity(launchIntent);
        }
    }

    public void startValidationTools() {
        if (!Customer.IS_START_TEST_APP) {
            return;
        }
        String path = StoragePaTool.getStoragePath(true);
        Log.d(TAG, "startValidationTools: " + path);
        if (path != null) {
            path = path + "/BixinTest";
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.sprd.validationtools",
                        "com.sprd.validationtools.ValidationToolsMainActivity");
                intent.setComponent(cn);
                mContext.startActivity(intent);
                Log.d(TAG, "startValidationTools: OK");
            } else {
                Log.d(TAG, "startValidationTools: !exists");
            }
        }
    }

    private void test() {
        mHandle.postDelayed(new Runnable() {
            public void run() {
                // 判断用户是否手动设置了定位模式
                int mode = Settings.System.getInt(getContentResolver(), "location_mode_changed", 0); // 1 : has changed  0 : no change
                // 去掉Improve location accuracy弹窗
                ContentResolver localContentResolver = getContentResolver();
                ContentValues localContentValues = new ContentValues();
                localContentValues.put("name", "network_location_opt_in");
                localContentValues.put("value", 1);
                localContentResolver.insert(Uri.parse("content://com.google.settings/partner"), localContentValues);

                if (mode == 0) { // user did not choose the location mode
                    Settings.Secure.putInt(getContentResolver(),
                            Settings.Secure.LOCATION_MODE,
                            android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
//                    Settings.Secure.setLocationProviderEnabled(localContentResolver, "network", true);
                }
            }
        }, 10000);   // 这里加延时是由于google的应用服务起来比较慢，起来之后会设置network_location_opt_in的值
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyDown: keyCode " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterContentObserver();
        if (mHandle != null) {
            mHandle.removeCallbacksAndMessages(null);
            mHandle = null;
        }
    }
}
