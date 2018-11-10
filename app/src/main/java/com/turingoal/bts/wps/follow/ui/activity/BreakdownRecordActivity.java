package com.turingoal.bts.wps.follow.ui.activity;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord_;
import com.turingoal.bts.wps.follow.ui.adapter.BreakdownRecordAdapter;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.util.lang.TgDateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 故障记录
 */
@Route(path = TgArouterPaths.BREAKDOWN_RECORD)
public class BreakdownRecordActivity extends TgBaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.tvQueryDate)
    TextView tvQueryDate; // 日期
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout; // 下拉刷行
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView; // recyclerView
    private BreakdownRecordAdapter adapter = new BreakdownRecordAdapter(); // adapter
    private Date currentDate; // 时间

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_breakdown;
    }

    @Override
    protected void initialized() {
        EventBus.getDefault().register(this);
        initToolbar(R.id.tvTitle, R.id.ivStart, getString(R.string.title_breakdown));  // 顶部工具条
        initAdapter();
        getData(Calendar.getInstance().getTime());
    }

    @OnClick({R.id.ivQueryStart, R.id.ivQueryEnd})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ivQueryStart:
                Calendar c1 = Calendar.getInstance();
                c1.setTime(currentDate);
                c1.add(Calendar.MONTH, -1);
                getData(c1.getTime());
                break;
            case R.id.ivQueryEnd:
                if (!TgDateUtil.date2String(Calendar.getInstance().getTime(), TgDateUtil.FORMAT_YYYYMM).equals(TgDateUtil.date2String(currentDate, TgDateUtil.FORMAT_YYYYMM))) { // 不是当月，如果现在选择的是当月，不能往后选择
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(currentDate);
                    c2.add(Calendar.MONTH, 1);
                    getData(c2.getTime());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 下拉刷行
     */
    @Override
    public void onRefresh() {
        getData(currentDate);
    }

    /**
     * adapter
     */
    private void initAdapter() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.rgb(255, 102, 108), Color.rgb(85, 193, 255), Color.rgb(204, 202, 111));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.openLoadAnimation();
        adapter.setEmptyView(getNotDataView((ViewGroup) recyclerView.getParent()));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 从数据库获取数据
     */
    private void getData(Date date) {
        currentDate = date;
        tvQueryDate.setText(TgDateUtil.date2String(currentDate, TgDateUtil.FORMAT_YYYY_MM_ZH));
        List<BreakdownRecord> breakdownRecordDbs = TgApplication.getBoxStore().boxFor(BreakdownRecord.class).query()
                .equal(BreakdownRecord_.userCodeNum, TgApplication.getTgUserPreferences().getUserCodeNum()) // 当前用户
                .equal(BreakdownRecord_.createTimeStr, TgDateUtil.date2String(date, TgDateUtil.FORMAT_YYYYMM)) // 当月
                .orderDesc(BreakdownRecord_.createTime).build().find(); // 时间排序
        if (breakdownRecordDbs.size() == 0) {
            adapter.setNewData(null);
        } else {
            adapter.setNewData(breakdownRecordDbs);
        }
        swipeRefreshLayout.setRefreshing(false); // 停止刷新
    }

    /**
     * event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if ("Refresh".equals(message)) { // 新增item成功，刷新本页面
            getData(currentDate);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
