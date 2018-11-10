package com.turingoal.bts.wps.follow.manager;

import com.blankj.utilcode.util.FileUtils;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecordItem;
import com.turingoal.bts.wps.follow.util.TgBreackdownZipUtil;

/**
 * Zip文件操作类
 */
public class ZipFileManager {
    private BreakdownRecord breakdownRecord;  // 总故障
    private BreakdownRecordItem breakdownRecordItem; // 分故障

    private ZipFileManager() {
        FileUtils.createOrExistsDir(TgAppConfig.ROOT_FULL_DIR); // 创建根目录
        FileUtils.createOrExistsDir(TgAppConfig.ZIP_FULL_DIR); // 创建zip目录
    }

    public ZipFileManager(BreakdownRecord breakdownRecord) {
        this();
        this.breakdownRecord = breakdownRecord;
    }

    public ZipFileManager(BreakdownRecord breakdownRecord, BreakdownRecordItem breakdownRecordItem) {
        this(breakdownRecord);
        this.breakdownRecordItem = breakdownRecordItem;
    }

    /**
     * 压缩总故障
     */
    public String zipBreakdownRecord() {
        FileUtils.createOrExistsDir(TgBreackdownZipUtil.getBreakdownRecordZipFullDir(breakdownRecord)); // 创建存放打包好的zip的目录
        FileUtils.createOrExistsFile(TgBreackdownZipUtil.getBreakdownRecordJsonFileFullPath(breakdownRecord)); // 创建总故障data.json文件
        for (BreakdownRecordItem breakdownRecordItem : breakdownRecord.breakdownRecordItems) { // 写分故障
            FileUtils.createOrExistsDir(TgBreackdownZipUtil.getBreakdownRecordItemFullDir(breakdownRecord, breakdownRecordItem)); // 创建分故障目录
            FileUtils.createOrExistsFile(TgBreackdownZipUtil.getBreakdownRecordItemJsonFileFullPath(breakdownRecord, breakdownRecordItem)); // 创建分故障data.json文件
            FileUtils.createOrExistsDir(TgBreackdownZipUtil.getBreakDownItemPhotoFullDir(breakdownRecord, breakdownRecordItem)); // 创建分故障存放图片的文件目录
        }
        WriteJsonFileManager.writeBreakdownRecord(breakdownRecord); // 写总data文件
        return TgBreackdownZipUtil.zipFolderWithPasswordAndRenameWithMd5(TgBreackdownZipUtil.getBreakdownRecordFullDir(breakdownRecord), TgBreackdownZipUtil.getBreakdownRecordZipFileFullPath(breakdownRecord), TgAppConfig.ZIP_PASSWORD, TgAppConfig.ZIP_MD5_KEY); // 打包
    }

    /**
     * 压缩分故障
     */
    public String zipBreakdownItem() {
        if (breakdownRecordItem == null) {
            throw new RuntimeException("breakdownRecordItem为null");
        }
        FileUtils.createOrExistsDir(TgBreackdownZipUtil.getBreakdownRecordZipFullDir(breakdownRecord)); // 创建总故障zip文件全路径
        FileUtils.createOrExistsDir(TgBreackdownZipUtil.getBreakdownRecordItemFullDir(breakdownRecord, breakdownRecordItem)); // 创建分故障目录
        FileUtils.createOrExistsFile(TgBreackdownZipUtil.getBreakdownRecordItemJsonFileFullPath(breakdownRecord, breakdownRecordItem)); // 创建分故障data.json文件
        FileUtils.createOrExistsDir(TgBreackdownZipUtil.getBreakDownItemPhotoFullDir(breakdownRecord, breakdownRecordItem)); // 创建分故障存放图片的文件目录
        WriteJsonFileManager.writeBreakdownItemRecord(breakdownRecord, breakdownRecordItem); // 写分data文件
        String breakdownRecordItemFullDir = TgBreackdownZipUtil.getBreakdownRecordItemFullDir(breakdownRecord, breakdownRecordItem);
        String breakdownRecordItemZipFileFullPath = TgBreackdownZipUtil.getBreakdownRecordItemZipFileFullPath(breakdownRecord, breakdownRecordItem);
        return TgBreackdownZipUtil.zipFolderWithPasswordAndRenameWithMd5(breakdownRecordItemFullDir, breakdownRecordItemZipFileFullPath, TgAppConfig.ZIP_PASSWORD, TgAppConfig.ZIP_MD5_KEY);
    }
}
