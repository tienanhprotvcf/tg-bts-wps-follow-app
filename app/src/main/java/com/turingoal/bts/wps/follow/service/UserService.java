package com.turingoal.bts.wps.follow.service;

import com.turingoal.bts.wps.follow.contants.ConstantUrls;
import com.turingoal.common.android.bean.TgKeyValueBean;
import com.turingoal.common.android.bean.TgResponseBean;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 用户
 */
public interface UserService {
    /**
     * 检查版本
     */
    @POST(ConstantUrls.URL_CHECK_VERSION)
    Call<TgResponseBean<TgKeyValueBean>> checkVersion(@Query("currentVersion") Integer currentVersion, @Query("packageName") String pageName);
}
