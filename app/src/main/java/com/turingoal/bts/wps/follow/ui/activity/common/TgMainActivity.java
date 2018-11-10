package com.turingoal.bts.wps.follow.ui.activity.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.manager.BreakdownRecordDbManager;
import com.turingoal.bts.wps.follow.manager.DbInitManager;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgGridItem;
import com.turingoal.common.android.ui.adapter.TgMainGridAdapter;
import com.turingoal.common.android.util.lang.TgDateUtil;
import com.turingoal.common.android.util.media.TgGlideUtil;
import com.turingoal.common.android.util.ui.TgDialogUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;

/**
 * 主页
 */
@Route(path = TgArouterCommonPaths.MAIN_INDEX)
public class TgMainActivity extends TgBaseActivity {
    @BindView(R.id.banner)
    Banner banner; // 轮播图
    @BindView(R.id.tvTrainSetTrips)
    TextView tvTrainSetTrips; // 车号
    @BindView(R.id.tvTrainSetCodeNum)
    TextView tvTrainSetCodeNum; // 车组
    @BindView(R.id.tvCreateTime)
    TextView tvCreateTime; // 时间
    @BindView(R.id.tvCreateTime2)
    TextView tvCreateTime2;
    @BindView(R.id.ll_item_2)
    LinearLayout ll_item_2; // 新建记录提示控件
    @BindView(R.id.tvBreakdownItemCount)
    TextView tvBreakdownRecordItemCount; // 当前故障记录条目数量
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView; // 九宫格

    @Override
    protected int getLayoutId() {
        return R.layout.common_activity_main;
    }

    @Override
    protected void initialized() {
        EventBus.getDefault().register(this);
        initToolbar(R.id.tvTitle, getString(R.string.app_title)); // 顶部工具条
        initBanner(); //轮播图
        TgMainGridAdapter mAdapter = new TgMainGridAdapter(TgAppConfig.MAIN_GRID_DATE); // adapter
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                TgGridItem item = (TgGridItem) adapter.getItem(position);
                if (TgArouterCommonPaths.COMMON_LOGOFF.equals(item.getUrlPath())) { // 注销退出
                    TgDialogUtil.showDialog(TgMainActivity.this, getString(R.string.string_logoff_hint), new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            TgSystemHelper.logoff(TgMainActivity.this);
                        }
                    });
                } else {
                    TgSystemHelper.handleIntent(item.getUrlPath()); // 跳转到对应的路径
                }
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(mAdapter);
        showLastBreakdownRecord(); // 显示当前故障记录
        DbInitManager.initData(); // 更新数据库
    }

    /**
     * 轮播图
     */
    private void initBanner() {
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE).setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                TgGlideUtil.loadBanner(context, path, imageView);
            }
        }).setImages(TgAppConfig.MAIN_BANNER_IMAGES).setBannerTitles(TgAppConfig.MAIN_BANNER_TITLES).start();
    }

    /**
     * 显示当前故障记录
     */
    private void showLastBreakdownRecord() {
        final BreakdownRecord currentBreakdownRecord = new BreakdownRecordDbManager().getCurrent(); // 当前故障记录
        if (currentBreakdownRecord != null) {
            ll_item_2.setVisibility(View.GONE);
            tvCreateTime.setText(TgDateUtil.date2String(currentBreakdownRecord.createTime, TgDateUtil.FORMAT_YYYY_MM_DD_ZH));
            tvTrainSetTrips.setText("车次：" + currentBreakdownRecord.trainSetTrips);
            tvTrainSetCodeNum.setText("车组：" + currentBreakdownRecord.trainSetCodeNum);
            tvBreakdownRecordItemCount.setText("" + currentBreakdownRecord.breakdownRecordItems.size());
            // 点击跳转到故障记录详情页
            findViewById(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TgSystemHelper.handleIntentWithLong(TgArouterPaths.BREAKDOWN_RECORD_DETAIL, "breakdownRecordOid", currentBreakdownRecord.oid);
                }
            });
        } else { // 系统第一次进入，还没有记录
            ll_item_2.setVisibility(View.VISIBLE);
            tvCreateTime2.setText(TgDateUtil.date2String(Calendar.getInstance().getTime(), TgDateUtil.FORMAT_YYYY_MM_DD_ZH));
            findViewById(R.id.ll_item_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TgSystemHelper.handleIntent(TgArouterPaths.BREAKDOWN_RECORD_ADD);
                }
            });
        }
    }

    /**
     * 点击返回按钮
     */
    @Override
    public void onBackPressed() {
        TgSystemHelper.dbClickExit(); //  再按一次退出系统
    }

    /**
     * event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String url) {
        if ("BreakdownRecordAddSuccess".equals(url) || "Refresh".equals(url)) { // 新建了故障,或者新建了分故障，通知刷新
            showLastBreakdownRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

