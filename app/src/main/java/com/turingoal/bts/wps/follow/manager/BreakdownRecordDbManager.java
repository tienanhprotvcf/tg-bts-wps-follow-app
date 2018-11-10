package com.turingoal.bts.wps.follow.manager;

import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord_;

import java.util.List;

/**
 * BreakdownRecordDbManager
 */
public class BreakdownRecordDbManager {
    /**
     * 得到当前故障记录
     */
    public BreakdownRecord getCurrent() {
        BreakdownRecord breakdownRecord = null;
        String userCodeNum = TgApplication.getTgUserPreferences().getUserCodeNum(); // 用户编号
        if (userCodeNum != null) {
            List<BreakdownRecord> breakdownRecords = TgApplication.getBoxStore().boxFor(BreakdownRecord.class).query()
                    .equal(BreakdownRecord_.userCodeNum, TgApplication.getTgUserPreferences().getUserCodeNum()) // 当前用户
                    .orderDesc(BreakdownRecord_.createTime).build().find(); // 时间排序
            if (breakdownRecords.size() != 0) {
                breakdownRecord = breakdownRecords.get(0); // 取第一个
            }
        }
        return breakdownRecord;
    }
}
