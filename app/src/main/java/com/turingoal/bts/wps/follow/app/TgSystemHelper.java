package com.turingoal.bts.wps.follow.app;

import android.app.Activity;
import android.content.Context;

import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseSystemHelper;
import com.turingoal.common.android.constants.TgConstantYesNo;
import com.turingoal.common.android.util.lang.TgStringUtil;

/**
 * 公用方法
 */
public final class TgSystemHelper extends TgBaseSystemHelper {
    private TgSystemHelper() {
        throw new Error("工具类不能实例化！");
    }

    /**
     * 获取服务器基础路径
     */
    public static String getServerBaseUrl() {
        String serverIp = TgApplication.getTgUserPreferences().getServerIp();   // ip
        if (TgStringUtil.isBlank(serverIp)) {
            serverIp = TgAppConfig.SERVER_IP;
        }
        Integer serverPort = TgApplication.getTgUserPreferences().getServerPort();  // port
        if (serverPort > 0) {
            serverPort = TgAppConfig.SERVER_PORT;
        }
        return TgAppConfig.SERVER_PROTOCOL + "://" + serverIp + ":" + serverPort + "/" + TgAppConfig.SERVER_NAME + "/";
    }

    /**
     * 注销
     */
    public static void logoff(final Context context) {
        TgApplication.getTgUserPreferences().setUserLoginStaus(TgConstantYesNo.NO); // 设置登录状态为未登录
        TgApplication.clearActivitys(); // 清空activiti堆栈
        TgSystemHelper.handleIntentAndFinish(TgArouterCommonPaths.COMMON_LOGIN, (Activity) context); // 跳转到登录页面
    }
}