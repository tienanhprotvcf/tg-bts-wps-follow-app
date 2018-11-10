package com.turingoal.bts.wps.follow.service;

import com.turingoal.bts.wps.follow.contants.ConstantUrls;
import com.turingoal.common.android.bean.TgResponseBean;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 总故障在线上传同步
 */
public interface BreakdownRecordSyncService {
    /**
     * 提交总故障文件
     */
    @Multipart
    @POST(ConstantUrls.URL_BREAKDOWN_SYNC)
    Call<TgResponseBean<Object>> upload(@Part MultipartBody.Part part);
}
