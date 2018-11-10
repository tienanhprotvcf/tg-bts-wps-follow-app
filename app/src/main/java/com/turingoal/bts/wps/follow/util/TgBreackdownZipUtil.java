package com.turingoal.bts.wps.follow.util;

import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecordItem;
import com.turingoal.common.android.util.io.TgFileUtil;
import com.turingoal.common.android.util.io.TgZip4jUtil;

import java.io.File;

/**
 * * BreackdownZip工具类
 */
public final class TgBreackdownZipUtil {
    private TgBreackdownZipUtil() {
        throw new Error("工具类不能实例化！");
    }

    /**
     * 使用给定密码压缩指定文件夹到指定位置.获得md5并且重命名文件。加上_{md5}。返回最终文件名
     */
    public static String zipFolderWithPasswordAndRenameWithMd5(final String sourceDir, final String zipFile, final String pssword, final String key) {
        String zipFilePath = TgZip4jUtil.zipFolderWithPassword(sourceDir, zipFile, pssword);
        return TgFileUtil.renameAddFilesMd5AndMd5(zipFilePath, key);
    }


    /**
     * 获得BreakdownRecord目录路径
     */
    private static String getBreakdownRecordDir(final BreakdownRecord breakdownRecord) {
        return "" + breakdownRecord.createTimeStr + "_" + breakdownRecord.trainSetTrips + "_" + breakdownRecord.trainSetCodeNum;
    }

    /**
     * 获得BreakdownRecord目录全路径
     */
    public static String getBreakdownRecordFullDir(final BreakdownRecord breakdownRecord) { // 当前总故障全路径，总故障打包全目录
        return TgAppConfig.ROOT_FULL_DIR + getBreakdownRecordDir(breakdownRecord) + File.separator;
    }

    /**
     * 获得BreakdownRecord Zip目录全路径
     */
    public static String getBreakdownRecordZipFullDir(final BreakdownRecord breakdownRecord) { // 返回存放当前zip的目录
        return TgAppConfig.ROOT_FULL_DIR + TgAppConfig.ZIP_DIR + File.separator + getBreakdownRecordDir(breakdownRecord) + File.separator;
    }

    /**
     * 获得BreakdownRecord json文件全路径
     */
    public static String getBreakdownRecordJsonFileFullPath(final BreakdownRecord breakdownRecord) { // 得到总故障的json文件
        return getBreakdownRecordFullDir(breakdownRecord) + TgAppConfig.ZIP_JSON_DATA_NAME;
    }


    /**
     * 获得BreakdownRecord zip文件全路径,带文件名
     */
    public static String getBreakdownRecordZipFileFullPath(final BreakdownRecord breakdownRecord) {
        return getBreakdownRecordZipFullDir(breakdownRecord) + "ALL_" + getBreakdownRecordDir(breakdownRecord) + TgAppConfig.ZIP_EXTENSION;
    }

    /**
     * 获得BreakdownRecordItem目录路径
     */
    private static String getBreakdownRecordItemDir(final BreakdownRecordItem breakdownRecordItem) {
        return "" + breakdownRecordItem.carriage + "_" + breakdownRecordItem.indexNum;
    }

    /**
     * 获得BreakdownRecordItem目录全路径
     */
    public static String getBreakdownRecordItemFullDir(final BreakdownRecord breakdownRecord, final BreakdownRecordItem breakdownRecordItem) { // 当前分故障全路径,分故障打包此目录
        return getBreakdownRecordFullDir(breakdownRecord) + getBreakdownRecordItemDir(breakdownRecordItem) + File.separator;
    }

    /**
     * 获得BreakdownRecordItem  json文件全路径
     */
    public static String getBreakdownRecordItemJsonFileFullPath(final BreakdownRecord breakdownRecord, final BreakdownRecordItem breakdownRecordItem) { // 得到分故障的data.json文件
        return getBreakdownRecordItemFullDir(breakdownRecord, breakdownRecordItem) + TgAppConfig.ZIP_JSON_DATA_NAME;
    }

    /**
     * 获得BreakdownRecordItem zip文件全路径,带文件名
     */
    public static String getBreakdownRecordItemZipFileFullPath(final BreakdownRecord breakdownRecord, final BreakdownRecordItem breakdownRecordItem) { // 返回存放当前分故障的zip的全路径
        return getBreakdownRecordZipFullDir(breakdownRecord) + "ITEM_" + getBreakdownRecordDir(breakdownRecord) + "_" + getBreakdownRecordItemDir(breakdownRecordItem) + TgAppConfig.ZIP_EXTENSION;
    }

    /**
     * 得到当前分故障图片的目录
     */
    public static String getBreakDownItemPhotoFullDir(final BreakdownRecord breakdownRecord, final BreakdownRecordItem breakdownRecordItem) {
        return getBreakdownRecordItemFullDir(breakdownRecord, breakdownRecordItem) + TgAppConfig.ZIP_FILES_DIR + File.separator;
    }

    // ***********************************************************************************
    // 图片专用,相对目录
    // ***********************************************************************************
    public static String getBreakDownItemPhotoDir(final BreakdownRecord breakdownRecord, final BreakdownRecordItem breakdownRecordItem) { // 得到当前相关故障item的照片目录
        return File.separator + getBreakdownRecordDir(breakdownRecord) + File.separator + getBreakdownRecordItemDir(breakdownRecordItem) + File.separator;
    }
}