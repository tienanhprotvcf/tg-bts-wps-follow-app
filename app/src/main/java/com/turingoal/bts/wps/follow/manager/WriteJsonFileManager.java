package com.turingoal.bts.wps.follow.manager;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.turingoal.bts.common.android.constants.ConstantMontorBreakdownPattern;
import com.turingoal.bts.common.android.constants.ConstantMontorBreakdownSource;
import com.turingoal.bts.common.android.constants.ConstantMontorBreakdownSystemTypes;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecordItem;
import com.turingoal.bts.wps.follow.bean.FilePathBean;
import com.turingoal.bts.wps.follow.util.TgBreackdownZipUtil;
import com.turingoal.common.android.bean.TgKeyValueBean;
import com.turingoal.common.android.util.io.TgJsonUtil;
import com.turingoal.common.android.util.io.TgUriUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 打包json的类
 */
public class WriteJsonFileManager {
    /**
     * 将BreakdownRecord写入data.json
     */
    public static void writeBreakdownRecord(BreakdownRecord breakdownRecord) {
        breakdownRecord.setSource(ConstantMontorBreakdownSource.CREW_LOG); // 故障模式始终来自日志
        for (BreakdownRecordItem breakdownRecordItem : breakdownRecord.breakdownRecordItems) { // 写入item
            writeBreakdownItemRecord(breakdownRecord, breakdownRecordItem);
        }
        try { // 将item写入分故障的data.json文件中
            FileWriter fw = new FileWriter(TgBreackdownZipUtil.getBreakdownRecordJsonFileFullPath(breakdownRecord));
            BufferedWriter out = new BufferedWriter(fw);
            out.write(TgJsonUtil.object2Json(breakdownRecord));
            out.close();
        } catch (IOException e) {
            ToastUtils.showLong("向json文件写数据失败");
        }
    }

    /**
     * 将BreakdownRecordItem写入data.json
     */
    public static void writeBreakdownItemRecord(BreakdownRecord breakdownRecord, BreakdownRecordItem breakdownRecordItem) {
        List<String> photoPaths = FilePathBeanDb2ListString(breakdownRecordItem.filePathBeans, breakdownRecord, breakdownRecordItem);
        breakdownRecordItem.setPhotoPaths(photoPaths); // 复制图片
        breakdownRecordItem.setSource(ConstantMontorBreakdownSource.CREW_LOG); // 故障模式始终来自日志
        String systemTypeStr = ConstantMontorBreakdownSystemTypes.QITA;
        for (TgKeyValueBean tgKeyValueBean : ConstantMontorBreakdownSystemTypes.DATA_LIST) {
            if (tgKeyValueBean.getStringValue().equals(breakdownRecordItem.systemType)) {
                systemTypeStr = tgKeyValueBean.getKey();
            }
        }
        breakdownRecordItem.setSystemType(systemTypeStr);
        String patternStr = ConstantMontorBreakdownPattern.OTHER;
        for (TgKeyValueBean tgKeyValueBean : ConstantMontorBreakdownPattern.DATA_LIST) {
            if (tgKeyValueBean.getStringValue().equals(breakdownRecordItem.pattern)) {
                patternStr = tgKeyValueBean.getKey();
            }
        }
        breakdownRecordItem.setPattern(patternStr);
        try { // 将item写入分故障的data.json文件中
            FileWriter fw = new FileWriter(TgBreackdownZipUtil.getBreakdownRecordItemJsonFileFullPath(breakdownRecord, breakdownRecordItem));
            BufferedWriter out = new BufferedWriter(fw);
            out.write(TgJsonUtil.object2Json(breakdownRecordItem));
            out.close();
        } catch (IOException e) {
            ToastUtils.showLong("向json文件写数据失败");
        }
    }

    /**
     * 将数据库BreakdownRecordItem类中的FilePathBeanDb数据，转为List<String>，并复制文件
     */
    private static List<String> FilePathBeanDb2ListString(List<FilePathBean> filePathBeanDbs, final BreakdownRecord breakdownRecord, final BreakdownRecordItem breakdownRecordItem) {
        List<String> paths = new ArrayList<>();
        for (FilePathBean filePathBeanDb : filePathBeanDbs) {
            String path;
            String name;
            if (filePathBeanDb.getPath().startsWith("content")) { // 视频
                File file = TgUriUtil.uri2File(Uri.parse(filePathBeanDb.getPath()), MediaStore.Video.Media.DATA);
                path = file.getPath();
                name = FileUtils.getFileName(file); // 文件名字，带后缀
            } else { // 图片
                path = filePathBeanDb.getPath();
                name = FileUtils.getFileName(filePathBeanDb.path); // 文件名字，带后缀
            }
            // 将手机图片复制到本目录下
            boolean b = FileUtils.copyFile(path, TgBreackdownZipUtil.getBreakDownItemPhotoFullDir(breakdownRecord, breakdownRecordItem) + name, new FileUtils.OnReplaceListener() {
                @Override
                public boolean onReplace() {
                    return true;
                }
            });
            if (b) {
                paths.add(TgBreackdownZipUtil.getBreakDownItemPhotoDir(breakdownRecord, breakdownRecordItem) + name); // 文件相对zip的目录
            }
        }
        return paths;
    }
}
