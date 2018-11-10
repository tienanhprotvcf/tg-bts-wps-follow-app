package com.turingoal.bts.wps.follow.service;

import com.turingoal.bts.wps.follow.contants.ConstantUrls;
import com.turingoal.common.android.bean.TgResponseBean;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 分故障在线上传同步
 */
public interface BreakdownRecordItemSyncService {
    /**
     * 提交分故障文件
     */
    @Multipart
    @POST(ConstantUrls.URL_BREAKDOWN_ITEM_SYNC)
    Call<TgResponseBean<Object>> upload(@Part MultipartBody.Part part);
}
