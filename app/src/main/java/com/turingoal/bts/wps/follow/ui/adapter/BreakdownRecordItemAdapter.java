package com.turingoal.bts.wps.follow.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.bts.wps.follow.bean.BreakdownRecordItem;
import com.turingoal.common.android.util.lang.TgDateUtil;

/**
 * 故障item列表adapter
 */
public class BreakdownRecordItemAdapter extends BaseQuickAdapter<BreakdownRecordItem, BaseViewHolder> {
    public BreakdownRecordItemAdapter() {
        super(R.layout.app_item_breakdown_item);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final BreakdownRecordItem breakdownRecordItem) {
        holder.setText(R.id.tvSystemType, "故障类型：" + breakdownRecordItem.systemType)
                .setText(R.id.tvPattern, "故障模式：" + breakdownRecordItem.pattern)
                .setText(R.id.tvCode, "故障代码：" + breakdownRecordItem.breakdownCode)
                .setText(R.id.tvDiscoveryTime, "发现时间：" + TgDateUtil.date2String(breakdownRecordItem.discoveryTime, TgDateUtil.FORMAT_YYYY_MM_DD_HH_MM_ZH))
                .setText(R.id.tvDiscoveryDesc, "" + breakdownRecordItem.discoveryDesc) // 故障描述
                .setText(R.id.tvCarriage, "车厢：" + breakdownRecordItem.carriage);
        holder.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TgSystemHelper.handleIntentWithLong(TgArouterPaths.BREAKDOWN_RECORD_ITEM_DETAIL, "breakdownRecordItemOid", breakdownRecordItem.oid);
            }
        });
        holder.addOnLongClickListener(R.id.ll_item);
    }
}
