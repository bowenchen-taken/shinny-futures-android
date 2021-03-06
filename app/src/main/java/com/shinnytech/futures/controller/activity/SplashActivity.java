package com.shinnytech.futures.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.shinnytech.futures.R;
import com.shinnytech.futures.application.BaseApplication;
import com.shinnytech.futures.constants.CommonConstants;
import com.shinnytech.futures.utils.LogUtils;
import com.shinnytech.futures.utils.SPUtils;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import static com.shinnytech.futures.application.BaseApplication.TD_BROADCAST_ACTION;
import static com.shinnytech.futures.constants.CommonConstants.CONFIG_IS_FIRM;
import static com.shinnytech.futures.constants.CommonConstants.TD_MESSAGE_LOGIN_FAIL;
import static com.shinnytech.futures.constants.CommonConstants.TD_MESSAGE_LOGIN_SUCCEED;
import static com.shinnytech.futures.constants.CommonConstants.TD_OFFLINE;
import static com.shinnytech.futures.utils.ScreenUtils.getStatusBarHeight;

public class SplashActivity extends AppCompatActivity {
    private final int TO_LOGIN = 0;
    private BroadcastReceiver mReceiverLogin;
    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean isFirm = (boolean) SPUtils.get(BaseApplication.getContext(), CONFIG_IS_FIRM, true);
        changeStatusBarColor(isFirm);

        if(!isTaskRoot()) finish();

        mHandler = new MyHandler(this);
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(TO_LOGIN);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Context context = BaseApplication.getContext();
        mTimer.schedule(mTimerTask, 10000);

        //没有登录过
        if (!SPUtils.contains(context, CommonConstants.CONFIG_LOGIN_DATE) ||
                ((String) SPUtils.get(context, CommonConstants.CONFIG_LOGIN_DATE, "")).isEmpty())
            mHandler.sendEmptyMessageDelayed(TO_LOGIN, 2000);

        registerBroaderCast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiverLogin != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiverLogin);
    }


    private void changeStatusBarColor(boolean isFirm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = getStatusBarHeight(BaseApplication.getContext());

            View view = new View(this);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            if (isFirm) view.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
            else view.setBackground(getResources().getDrawable(R.color.login_simulation_hint));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (isFirm)
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            else
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.login_simulation_hint));
        }
    }

    /**
     * date: 2019/4/24
     * author: chenli
     * description: 监听登录成功、失败事件
     */
    private void registerBroaderCast() {

        mReceiverLogin = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("msg");
                switch (msg) {
                    case TD_MESSAGE_LOGIN_SUCCEED:
                        //登录成功
                        toMain();
                        break;
                    case TD_MESSAGE_LOGIN_FAIL:
                        //登录失败
                        toLogin();
                        break;
                    case TD_OFFLINE:
                        //超时检测，连接失败
                        toLogin();
                        break;
                    default:
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiverLogin, new IntentFilter(TD_BROADCAST_ACTION));

    }

    private void toLogin() {
        mTimer.cancel();
        mTimerTask.cancel();
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(loginIntent);
        SplashActivity.this.finish();
    }

    private void toMain() {
        mTimer.cancel();
        mTimerTask.cancel();
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }

    /**
     * date: 6/1/18
     * author: chenli
     * description: 点击登录后服务器返回处理
     * version:
     * state:
     */
    static class MyHandler extends Handler {
        WeakReference<SplashActivity> mActivityReference;

        MyHandler(SplashActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SplashActivity activity = mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        activity.toLogin();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
