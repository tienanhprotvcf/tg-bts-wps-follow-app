package com.turingoal.bts.wps.follow.ui.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecordItem;
import com.turingoal.bts.wps.follow.manager.ZipFileManager;
import com.turingoal.bts.wps.follow.service.BreakdownRecordSyncService;
import com.turingoal.bts.wps.follow.ui.adapter.BreakdownRecordItemAdapter;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.support.net.retrofit.TgHttpRetrofitCallback;
import com.turingoal.common.android.ui.layout.TgTitleContextLinearLayout;
import com.turingoal.common.android.util.ui.TgDialogUtil;
import com.turingoal.common.android.util.ui.TgToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * [故障详情]
 */
@Route(path = TgArouterPaths.BREAKDOWN_RECORD_DETAIL)
public class BreakdownRecordDetailActivity extends TgBaseActivity {
    @BindView(R.id.ivEnd)
    ImageView ivEnd; // 同步提交
    @BindView(R.id.tclTrainSetTrips)
    TgTitleContextLinearLayout tclTrainSetTrips; // 车次
    @BindView(R.id.tclTrainSetCodeNum)
    TgTitleContextLinearLayout tclTrainSetCodeNum; // 车组，车号
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView; // recyclerView
    @BindView(R.id.btnZip)
    Button btnZip;
    private BreakdownRecordItemAdapter adapter = new BreakdownRecordItemAdapter(); // adapter
    @Autowired
    long breakdownRecordOid; // 当前总故障的id,从上一个页面传来的
    private BreakdownRecord breakdownRecord; // 当前总故障

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_breakdown_detail;
    }

    @Override
    protected void initialized() {
        EventBus.getDefault().register(this);
        initToolbar(R.id.tvTitle, R.id.ivStart, getString(R.string.title_breakdown_detail));  // 顶部工具条
        breakdownRecord = TgApplication.getBoxStore().boxFor(BreakdownRecord.class).get(breakdownRecordOid);
        tclTrainSetTrips.setContextStr(breakdownRecord.trainSetTrips);
        tclTrainSetCodeNum.setContextStr(breakdownRecord.trainSetCodeNum);
        ivEnd.setImageResource(R.drawable.ic_sync);
        if ("yes".equals(breakdownRecord.sync)) { // 是否已经同步
            ivEnd.setVisibility(View.GONE);
        } else {
            ivEnd.setVisibility(View.VISIBLE);
        }
        initAdapter();
        getData();
    }

    @OnClick({R.id.ivEnd, R.id.btnAdd, R.id.btnZip})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ivEnd:
                upload();
                break;
            case R.id.btnAdd: // 增加故障
                TgSystemHelper.handleIntentWithLong(TgArouterPaths.BREAKDOWN_RECORD_ITEM_ADD, "breakdownRecordOid", breakdownRecordOid);
                break;
            case R.id.btnZip: // 打总包
                if (new ZipFileManager(breakdownRecord).zipBreakdownRecord() != null) {
                    ToastUtils.showLong(R.string.string_breakdown_detail_zip_success);
                } else {
                    ToastUtils.showLong(R.string.string_breakdown_detail_zip_failure);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 上传文件
     */
    private void upload() {
        String path = new ZipFileManager(breakdownRecord).zipBreakdownRecord(); // zip路径
        if (path != null) { // 打包
            File file = new File(path); // 得到zip文件
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            TgApplication.getRetrofit().create(BreakdownRecordSyncService.class)
                    .upload(part)
                    .enqueue(new TgHttpRetrofitCallback<Object>(this, true, false) {
                        @Override
                        public void successHandler(Object o) {
                            TgToastUtil.showLong("上传成功");
                            breakdownRecord.sync = "yes";
                            TgApplication.getBoxStore().boxFor(BreakdownRecord.class).put(breakdownRecord);
                            ivEnd.setVisibility(View.GONE);
                        }
                    });
        } else {
            ToastUtils.showLong(R.string.string_breakdown_detail_zip_failure);
        }
    }

    /**
     * adapter
     */
    private void initAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.openLoadAnimation();
        adapter.setEmptyView(getNotDataView((ViewGroup) recyclerView.getParent()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(final BaseQuickAdapter baseQuickAdapter, View view, final int position) {
                TgDialogUtil.showDialog(BreakdownRecordDetailActivity.this, "是否删除当前故障？", new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        BreakdownRecordItem breakdownRecordItem = adapter.getItem(position);
                        adapter.getData().remove(breakdownRecordItem);
                        TgApplication.getBoxStore().boxFor(BreakdownRecordItem.class).remove(breakdownRecordItem.oid);
                        adapter.notifyDataSetChanged();
                        EventBus.getDefault().post("Refresh"); // 通知详情页面更新
                    }
                });
                return false;
            }
        });
    }

    /**
     * 加载数据
     */
    private void getData() {
        List<BreakdownRecordItem> breakdownRecordItems = breakdownRecord.breakdownRecordItems;
        Collections.sort(breakdownRecordItems, new Comparator<BreakdownRecordItem>() { // 按照发现时间排序
            @Override
            public int compare(BreakdownRecordItem itemBeanDb1, BreakdownRecordItem itemBeanDb2) {
                long t1 = itemBeanDb1.discoveryTime == null ? 0 : itemBeanDb1.discoveryTime.getTime();
                long t2 = itemBeanDb2.discoveryTime == null ? 0 : itemBeanDb2.discoveryTime.getTime();
                return Long.compare(t2, t1);
            }
        });
        if (breakdownRecordItems.size() == 0) {
            adapter.setNewData(null);
            btnZip.setVisibility(View.GONE); // 如果没有故障，不显示打包按钮
        } else {
            btnZip.setVisibility(View.VISIBLE); // 如果有故障，显示打包按钮
            adapter.setNewData(breakdownRecordItems);
        }
    }

    /**
     * event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if ("ItemAddSuccess".equals(message)) { // 新增item成功，更新本页面
            breakdownRecord = TgApplication.getBoxStore().boxFor(BreakdownRecord.class).get(breakdownRecordOid);
            if ("yes".equals(breakdownRecord.sync)) { // 是否已经同步
                ivEnd.setVisibility(View.GONE);
            } else {
                ivEnd.setVisibility(View.VISIBLE);
            }
            getData();
            EventBus.getDefault().post("Refresh"); // 通知列表页面刷新
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
