package com.bixin.launcher.launcherbx3.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bixin.launcher.launcherbx3.R;
import com.bixin.launcher.launcherbx3.model.LauncherApp;
import com.bixin.launcher.launcherbx3.model.adapter.RecyclerGridViewAdapter;
import com.bixin.launcher.launcherbx3.model.bean.AppInfo;
import com.bixin.launcher.launcherbx3.model.listener.OnAppUpdateListener;
import com.bixin.launcher.launcherbx3.model.listener.OnRecyclerViewItemListener;
import com.bixin.launcher.launcherbx3.model.receiver.APPReceiver;
import com.bixin.launcher.launcherbx3.model.tools.CallBackManagement;
import com.bixin.launcher.launcherbx3.model.tools.StartActivityTool;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Altair
 * @date :2020.09.23 上午 10:03
 * @description: AppList页面
 */
public class AppListActivity extends BaseActivity implements OnRecyclerViewItemListener, OnAppUpdateListener {
    private ArrayList<AppInfo> appInfoArrayList = new ArrayList<>();
    private StartActivityTool mActivityTools;
    private RecyclerGridViewAdapter mRecyclerGridViewAdapter;
    private RecyclerView recyclerView;
    private APPReceiver mUpdateAppReceiver;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initData();
        initAppInfo();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_app_list;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void init() {
        mUpdateAppReceiver = new APPReceiver();
        mDisposable = new CompositeDisposable();
        mActivityTools = new StartActivityTool();
        CallBackManagement.getInstance().setOnAppUpdateListener(this);
        registerAppReceiver();
    }

    @Override
    public void initView() {
        recyclerView = findViewById(R.id.rcv_app);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //取消动画,避免更新图片闪烁
//        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }

//    private void initData() {
//        getAppList();
//    }

    private void initAppInfo() {
        mDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                getAppList(emitter);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object aBoolean) throws Exception {
                        if (mRecyclerGridViewAdapter == null) {
                            mRecyclerGridViewAdapter = new RecyclerGridViewAdapter(
                                    mContext, appInfoArrayList);
                            recyclerView.setAdapter(mRecyclerGridViewAdapter);
                            mRecyclerGridViewAdapter.setOnRecyclerViewItemListener(AppListActivity.this);
                        } else {
                            mRecyclerGridViewAdapter.setAppInfoArrayList(appInfoArrayList);
                            mRecyclerGridViewAdapter.notifyDataSetChanged();
                        }
                    }
                }));
    }

    public void getAppList(ObservableEmitter<Boolean> emitter) {
        appInfoArrayList.clear();
        appInfoArrayList = LauncherApp.getInstance().getShowAppList();
        emitter.onNext(true);
    }

    @Override
    public void onItemClickListener(int position, String packageName) {
        Log.d(TAG, "onItemClickListener packageName: " + packageName);
        mActivityTools.launchAppByPackageName(packageName, "null");
    }

    @Override
    public void updateAppList() {
        Log.d(TAG, "updateAppList: ");
        initAppInfo();
    }

    private void registerAppReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
//        filter.addAction(Customer.ACTION_TXZ_CUSTOM_COMMAND);
        filter.addDataScheme("package");
        registerReceiver(mUpdateAppReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdateAppReceiver != null) {
            unregisterReceiver(mUpdateAppReceiver);
        }
    }

}
