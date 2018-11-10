package com.turingoal.bts.wps.follow.app;

/**
 * 常量-Activity路径
 */
public interface TgArouterPaths {
    // 故障
    String BREAKDOWN_RECORD = TgAppConfig.APP_BASE_PATH + "breakdown"; // 故障列表
    String BREAKDOWN_RECORD_ADD = TgAppConfig.APP_BASE_PATH + "breakdown/add"; // 故障新建
    String BREAKDOWN_RECORD_DETAIL = TgAppConfig.APP_BASE_PATH + "breakdown/detail"; // 故障详情
    String BREAKDOWN_RECORD_ITEM_ADD = TgAppConfig.APP_BASE_PATH + "breakdown/item/add"; // 故障item新建
    String BREAKDOWN_RECORD_ITEM_DETAIL = TgAppConfig.APP_BASE_PATH + "breakdown/item/detail"; // 故障item详情
    String PHOTO_SHOW = TgAppConfig.APP_BASE_PATH + "photoShow"; // 查看照片
}
