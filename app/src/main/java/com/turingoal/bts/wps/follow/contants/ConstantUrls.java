package com.turingoal.bts.wps.follow.contants;

import com.turingoal.bts.wps.follow.app.TgAppConfig;

/**
 * url路径
 */
public interface ConstantUrls {
    // 基本
    String URL_CHECK_VERSION = TgAppConfig.URL_BASE + "checkVersion" + TgAppConfig.URL_SUFFIX; // 检查版本
    // 车号
    String URL_TRAIN_SET_LIST = TgAppConfig.URL_BASE + "trainSet/find" + TgAppConfig.URL_SUFFIX; // 车号配置
    // 上传
    String URL_BREAKDOWN_SYNC = TgAppConfig.URL_BASE + "breakdownRecord/import" + TgAppConfig.URL_SUFFIX; // 总故障同步路径
    String URL_BREAKDOWN_ITEM_SYNC = TgAppConfig.URL_BASE + "breakdownRecordItem/import" + TgAppConfig.URL_SUFFIX; // 分故障同步路径
}
