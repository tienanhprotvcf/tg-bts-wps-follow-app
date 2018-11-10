package com.turingoal.bts.wps.follow.manager;

import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.bean.TrainSet;
import com.turingoal.bts.wps.follow.service.TrainSetService;
import com.turingoal.bts.wps.follow.util.TgRetrofitCallback;

import java.util.List;

/**
 * 数据库初始化
 */
public final class DbInitManager {
    private DbInitManager() {
        throw new Error("工具类不能实例化！");
    }

    /**
     * 初始化数据库数据
     */
    public static void initData() {
        initTrainSetDatas(); // 初始化[车号]数据
    }

    /**
     * 初始化[车组]数据
     */
    private static void initTrainSetDatas() {
        TgApplication.getRetrofit().create(TrainSetService.class)
                .list()
                .enqueue(new TgRetrofitCallback<List<TrainSet>>(null, false, false) {
                    @Override
                    public void successHandler(List<TrainSet> list) {
                        TgApplication.getBoxStore().boxFor(TrainSet.class).removeAll();
                        TgApplication.getBoxStore().boxFor(TrainSet.class).put(list);
                    }
                });
    }
}