package com.turingoal.bts.wps.follow.service;

import com.turingoal.bts.wps.follow.bean.TrainSet;
import com.turingoal.bts.wps.follow.contants.ConstantUrls;
import com.turingoal.common.android.bean.TgResponseBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 车组配置
 */
public interface TrainSetService {
    /**
     * 车组配置
     */
    @GET(ConstantUrls.URL_TRAIN_SET_LIST)
    Call<TgResponseBean<List<TrainSet>>> list();
}
