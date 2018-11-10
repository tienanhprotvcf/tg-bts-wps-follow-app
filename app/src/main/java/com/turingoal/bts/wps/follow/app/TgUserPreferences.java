package com.turingoal.bts.wps.follow.app;

import android.content.Context;

import com.turingoal.common.android.base.TgBaseUserPreferences;
import com.turingoal.common.android.constants.TgConstantYesNo;
import com.turingoal.common.android.support.system.TgConstantSystemValues;

/**
 * 用户数据_参数保存服务
 */
public class TgUserPreferences extends TgBaseUserPreferences {
    public TgUserPreferences(final Context context, final String spName) {
        super(context, spName);
    }

    /*** 设置ip */
    public void setServerIp(final String ip) {
        sharedPreferences.edit().putString("server_ip", ip).commit();
    }

    /*** 获取ip */
    public String getServerIp() {
        return sharedPreferences.getString("server_ip", TgAppConfig.SERVER_IP);
    }

    /*** 设置port */
    public void setServerPort(final Integer port) {
        sharedPreferences.edit().putInt("server_port", port).apply();
    }

    /*** 获取port */
    public int getServerPort() {
        return sharedPreferences.getInt("server_port", TgAppConfig.SERVER_PORT);
    }

    /*** 设置token */
    public void setToken(final String token) {
        sharedPreferences.edit().putString(TgConstantSystemValues.ACCESS_TOKEN, token).commit();
    }

    /*** 获取token */
    public String getToken() {
        return sharedPreferences.getString(TgConstantSystemValues.ACCESS_TOKEN, "");
    }

    /**
     * 设置用户登录状态
     */
    public void setUserLoginStaus(final Integer status) {
        sharedPreferences.edit().putInt(TgConstantSystemValues.LOGIN_STAUS, status).apply();
    }

    /**
     * 得到用户登录状态
     */
    public int getUserLoginStaus() {
        return sharedPreferences.getInt(TgConstantSystemValues.LOGIN_STAUS, TgConstantYesNo.NO);
    }

    /**
     * 设置用户工号
     */
    public void setUserCodeNum(final String userCodeNum) {
        sharedPreferences.edit().putString(TgConstantSystemValues.CURRENT_USER_CODE_NUM, userCodeNum).apply();
    }

    /**
     * 得到用户工号
     */
    public String getUserCodeNum() {
        return sharedPreferences.getString(TgConstantSystemValues.CURRENT_USER_CODE_NUM, null);
    }

    /**
     * 设置用户密码
     */
    public void setUserPassword(final String userPassword) {
        sharedPreferences.edit().putString(TgConstantSystemValues.CURRENT_USER_PASSWORD, userPassword).apply();
    }

    /**
     * 得到用户密码
     */
    public String getUserPassword() {
        return sharedPreferences.getString(TgConstantSystemValues.CURRENT_USER_PASSWORD, null);
    }
}
