package com.turingoal.bts.wps.follow.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.common.android.util.lang.TgDateUtil;

/**
 * 故障列表adapter
 */
public class BreakdownRecordAdapter extends BaseQuickAdapter<BreakdownRecord, BaseViewHolder> {
    public BreakdownRecordAdapter() {
        super(R.layout.app_item_breakdown);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final BreakdownRecord breakdownRecord) {
        holder.setText(R.id.tvTrainSetTrips, "车次：" + breakdownRecord.trainSetTrips)
                .setText(R.id.tvTrainSetCodeNum, "车组：" + breakdownRecord.trainSetCodeNum)
                .setText(R.id.tvCreateTime, "创建时间：" + TgDateUtil.date2String(breakdownRecord.createTime, TgDateUtil.FORMAT_YYYY_MM_DD_HH_MM_ZH))
                .setText(R.id.tvBreakdownItemCount, "" + breakdownRecord.breakdownRecordItems.size()); // 故障个数

        holder.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TgSystemHelper.handleIntentWithLong(TgArouterPaths.BREAKDOWN_RECORD_DETAIL, "breakdownRecordOid", breakdownRecord.oid);
            }
        });
    }
}
