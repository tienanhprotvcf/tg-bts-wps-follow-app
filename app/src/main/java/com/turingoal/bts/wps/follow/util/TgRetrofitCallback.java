package com.turingoal.bts.wps.follow.util;

import android.content.Context;

import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.common.android.support.net.retrofit.TgHttpRetrofitCallback;

/**
 * Retrofit请求Callback
 */
public abstract class TgRetrofitCallback<T> extends TgHttpRetrofitCallback<T> {
    public TgRetrofitCallback(final Context contextParm, final boolean showErrorMessageParm, final boolean checkTokenParm) {
        super(contextParm, showErrorMessageParm, checkTokenParm);
    }

    /**
     * token错误handler
     */
    @Override
    public void tokenErrorHandler(final String message) {
        if (context != null) {
            TgSystemHelper.logoff(context); //注销并跳转到登录页面
        }
    }
}
