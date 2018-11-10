package com.turingoal.bts.wps.follow.ui.activity.common;

import android.Manifest;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.NetworkUtils;
import com.turingoal.android.photopicker.permission.PermissionListener;
import com.turingoal.android.photopicker.permission.PermissionManager;
import com.turingoal.bts.wps.follow.BuildConfig;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.bts.wps.follow.service.UserService;
import com.turingoal.bts.wps.follow.util.TgRetrofitCallback;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgKeyValueBean;
import com.turingoal.common.android.constants.TgConstantYesNo;
import com.turingoal.common.android.util.lang.TgStringUtil;
import com.turingoal.common.android.util.system.TgAppUtil;
import com.turingoal.common.android.util.system.TgSystemUtil;
import com.turingoal.common.android.util.ui.TgDialogUtil;

/**
 * 欢迎页
 */
public class TgWelcomeActivity extends TgBaseActivity {
    private PermissionManager helper; // 权限申请
    private static final int REQUEST_CODE = 1001; // 权限请求code
    private static final int GO_CODE = 0; // handler发送跳转code

    private Handler handler = new Handler(new Handler.Callback() { // 因为NetworkUtils.isAvailableByPing是一个耗时操作，所以new Thread，因此Handler必须自主线程new。
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case GO_CODE:
                    // 如果没有登录，调到登录页面，否则跳到主页面
                    if (TgApplication.getTgUserPreferences().getUserLoginStaus() == TgConstantYesNo.YES) {
                        TgSystemHelper.handleIntentAndFinish(TgArouterCommonPaths.MAIN_INDEX, TgWelcomeActivity.this);
                    } else {
                        TgSystemHelper.handleIntentAndFinish(TgArouterCommonPaths.COMMON_LOGIN, TgWelcomeActivity.this);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected int getLayoutId() {
        return R.layout.common_activity_welcome;
    }

    @Override
    protected void initialized() {
        helper = PermissionManager.with(this)
                .addRequestCode(REQUEST_CODE)  // 添加权限请求码
                .permissions( // 设置权限，可以添加多个权限
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                )
                .setPermissionsListener(new PermissionListener() { // 设置权限监听器
                    @Override
                    public void onGranted() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (NetworkUtils.isAvailableByPing(TgApplication.getTgUserPreferences().getServerIp())) { // 如果网络可用，检查版本
                                    checkVersion();
                                } else {
                                    go(); // 网络不可用
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onDenied() {
                        defaultFinish();   // 用户拒绝该权限时调用
                    }

                    @Override
                    public void onShowRationale(String[] permissions) {
                        helper.setIsPositive(true);
                        helper.request();
                    }
                }).request(); // 请求权限
    }

    /**
     * 请求权限结果处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                helper.onPermissionResult(permissions, grantResults);
                break;
        }
    }

    /**
     * 3秒后跳转
     */
    private void go() {
        handler.sendEmptyMessageDelayed(GO_CODE, TgAppConfig.WELCOME_DELAY_TIME);
    }

    /**
     * 检查版本
     */
    private void checkVersion() {
        TgApplication.getRetrofit().create(UserService.class)
                .checkVersion(BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID)
                .enqueue(new TgRetrofitCallback<TgKeyValueBean>(this, false, false) {
                    @Override
                    public void successHandler(final TgKeyValueBean bean) {
                        final String appDownloadUrl = bean.getStringValue();
                        if (TgStringUtil.isBlank(appDownloadUrl)) {
                            go(); // 没有新版本
                        } else {
                            TgDialogUtil.showDialog(TgWelcomeActivity.this, "版本检查", "检查到有新版本，是否马上下载安装?",
                                    new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            TgSystemUtil.openWebSite(appDownloadUrl);
                                            defaultFinish();
                                            TgAppUtil.exitApp();
                                        }
                                    }, new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            go(); // 点击确定，下载新版本
                                        }
                                    });
                        }
                    }

                    @Override
                    public void failHandler(String msg) {
                        go(); // 请求失败
                    }
                });
    }
}
