package com.bixin.launcher.launcherbx3.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.bixin.launcher.launcherbx3.R;
import com.bixin.launcher.launcherbx3.model.bean.Customer;
import com.bixin.launcher.launcherbx3.model.bean.WeatherBean;
import com.bixin.launcher.launcherbx3.model.listener.OnLocationListener;
import com.bixin.launcher.launcherbx3.model.receiver.WeatherReceiver;
import com.bixin.launcher.launcherbx3.model.tools.CallBackManagement;
import com.bixin.launcher.launcherbx3.model.tools.StartActivityTool;

import java.lang.ref.WeakReference;

import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.OnEnterListener;
import cn.kuwo.autosdk.api.OnExitListener;
import cn.kuwo.autosdk.api.OnPlayerStatusListener;
import cn.kuwo.autosdk.api.PlayState;
import cn.kuwo.autosdk.api.PlayerStatus;
import cn.kuwo.base.bean.Music;

/**
 * @author Altair
 * @date :2020.04.01 上午 10:23
 * @description: Launcher主页
 */
public class LauncherHomeActivity extends BaseActivity implements View.OnClickListener,
        OnLocationListener {
    private final static String TAG = LauncherHomeActivity.class.getName();
    private StartActivityTool mActivityTool;
    private WeatherBean weatherBean;
    private ImageView ivWeatherIcon;
    private TextView tvWeather;
    private TextView tvCurrentCity;
    private static final int MIN_CLICK_DELAY_TIME = 5000;
    private static long lastClickTime;
    private WeatherReceiver mWeatherReceiver;
    private InnerHandler mHandler;
    private TextView tvMusicName;
    private ImageView ivPlay;

    private KWAPI mKwApi;
    private PlayerStatus musicStatus;
    private String musicName;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayout() {
        return R.layout.activity_launcher_home;
    }

    @Override
    public void init() {
        mActivityTool = new StartActivityTool();
        mHandler = new InnerHandler(this);
        CallBackManagement.getInstance().setOnLocationListener(this);
        mWeatherReceiver = new WeatherReceiver();
        registerWeatherReceiver();
        mHandler.sendEmptyMessageAtTime(1, 3000);
    }

    @Override
    public void initView() {
        LinearLayout llService = findViewById(R.id.ll_service);
        LinearLayout llNavigation = findViewById(R.id.ll_nav);
        LinearLayout llRecord = findViewById(R.id.ll_record);
        LinearLayout llApp = findViewById(R.id.ll_app);
        llApp.setOnClickListener(this);
        llNavigation.setOnClickListener(this);
        llService.setOnClickListener(this);
        llRecord.setOnClickListener(this);
        initWeatherView();
        initPlayerView();
    }

    private void initPlayerView() {
        ImageView ivMusic = findViewById(R.id.iv_music);
        ImageView ivPre = findViewById(R.id.iv_music_pre);
        ImageView ivNext = findViewById(R.id.iv_music_next);
        ivPlay = findViewById(R.id.iv_music_play);
        tvMusicName = findViewById(R.id.tv_music_name);
        ivPre.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivMusic.setOnClickListener(this);
    }

    private void initWeatherView() {
        RelativeLayout rlWeather = findViewById(R.id.cl_time);
        ivWeatherIcon = findViewById(R.id.iv_weather);
        tvWeather = findViewById(R.id.tv_weather);
        tvCurrentCity = findViewById(R.id.tv_city);
        tvWeather = findViewById(R.id.tv_weather);
        rlWeather.setOnClickListener(this);
    }

    public static class InnerHandler extends Handler {
        private final LauncherHomeActivity activity;

        private InnerHandler(LauncherHomeActivity activity) {
            WeakReference<LauncherHomeActivity> activityWeakReference = new WeakReference<>(activity);
            this.activity = activityWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                activity.updateWeather();
            }
            if (msg.what == 1) {
                activity.startAPK();
            }
            if (msg.what == 3) {
                activity.updateMusicName();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_nav:
                mActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_AUTONAVI);
                break;
            case R.id.ll_service:
//                mActivityTool.getAllContacts();
                mActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_RCX);
                break;
            case R.id.ll_record:
                mActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_DVR);
                break;
            case R.id.ll_app:
                mActivityTool.jumpToActivity(this, AppListActivity.class);
            case R.id.cl_time:
                getWeather();
                break;
            case R.id.iv_music:
                mActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_KWMUSIC);
                break;
            case R.id.iv_music_next:
                setKuWoState(3, PlayState.STATE_NEXT);
                break;
            case R.id.iv_music_pre:
                setKuWoState(1, PlayState.STATE_PRE);
                break;
            case R.id.iv_music_play:
                setKuWoState(2, null);
                break;
        }
    }

    @Override
    public void gpsSpeedChanged() {

    }

    @Override
    public void updateWeather(String weatherInfo) {
        Log.d(TAG, "updateWeather:weatherInfo " + weatherInfo);
        weatherBean = JSONObject.parseObject(weatherInfo, WeatherBean.class);
        if (weatherBean == null) {
            return;
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(0);
        }
    }

    private void updateWeather() {
        String cityName = weatherBean.getCityName();
        String weather = weatherBean.getWeather();
        tvCurrentCity.setText(cityName);
        String weatherInfo = weather + "  " + weatherBean.getCurrentTemperature() + "°";
        tvWeather.setText(weatherInfo);
        ivWeatherIcon.setImageResource(mActivityTool.getWeatherIcon(weather));
    }

    private void getWeather() {
        if (isFastClick()) {
            Intent intent = new Intent(Customer.ACTION_GET_WEATHER);
            sendBroadcast(intent);
        }
    }

    private boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    private void registerWeatherReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Customer.ACTION_UPDATE_WEATHER);
        registerReceiver(mWeatherReceiver, filter);
    }

    /**
     * 酷我音乐初始化
     */
    public void initKwApi() {
        if (mKwApi == null) {
            mKwApi = KWAPI.createKWAPI(mContext, "auto");
        }

        mKwApi.registerEnterListener(new OnEnterListener() {
            @Override
            public void onEnter() {
                mKwApi.bindAutoSdkService();
                Log.d(TAG, "应用启动收到了");
            }
        });
        mKwApi.registerExitListener(new OnExitListener() {
            @Override
            public void onExit() {
                mKwApi.unbindAutoSdkService();
            }
        });
        //注册获取播放状态的监听
        mKwApi.registerPlayerStatusListener(new OnPlayerStatusListener() {
            @Override
            public void onPlayerStatus(PlayerStatus arg0, Music music) {
                if (music != null) {
                    musicName = music.name;
                }
                Log.d(TAG, "onPlayerStatus:status " + arg0.name());
                updateMusicStatus();
            }
        });
    }

    /**
     * 根据酷我监听改变音乐item的播放状态图标
     */
    private void updateMusicStatus() {
        if (mKwApi == null) {
            return;
        }

        musicStatus = mKwApi.getPlayerStatus();
        if (musicStatus == null) {
            return;
        }
        Log.d(TAG, "updateMusicStatus: " + musicStatus);
        if (musicStatus == PlayerStatus.PAUSE || musicStatus == PlayerStatus.STOP ||
                musicStatus == PlayerStatus.INIT) {
            ivPlay.setSelected(true);
        } else if (musicStatus == PlayerStatus.PLAYING) {
            mHandler.sendEmptyMessage(3);
            ivPlay.setSelected(false);
        }
    }

    /**
     * 设置酷我播放状态
     *
     * @param type  音乐按钮
     * @param state 状态
     */
    private void setKuWoState(int type, PlayState state) {
        if (mKwApi == null) {
            return;
        }
        if (type == 2) {
            musicStatus = mKwApi.getPlayerStatus();
            if (musicStatus == PlayerStatus.PAUSE) {
                mKwApi.setPlayState(PlayState.STATE_PLAY);
            } else if (musicStatus == PlayerStatus.PLAYING) {
                mKwApi.setPlayState(PlayState.STATE_PAUSE);
            }
        }
        mKwApi.setPlayState(state);
    }

    private void updateMusicName() {
        tvMusicName.setText(musicName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void releaseKuWo() {
        if (mKwApi == null) {
            return;
        }
        mKwApi.unRegisterEnterListener();
        mKwApi.unRegisterExitListener();
        mKwApi.unRegisterPlayerStatusListener();
        mKwApi.unbindAutoSdkService();
        mKwApi.unBindKuWoApp();
        mKwApi = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mWeatherReceiver != null) {
            unregisterReceiver(mWeatherReceiver);
        }
        CallBackManagement.getInstance().releaseAll();
        releaseKuWo();
        CallBackManagement.getInstance().releaseAll();
    }

    private void startAPK() {
        initKwApi();
        mActivityTool.startVoiceRecognitionService();
        mActivityTool.startRCX();
    }

}