package com.turingoal.bts.wps.follow.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecordItem;
import com.turingoal.bts.wps.follow.bean.FilePathBean;
import com.turingoal.bts.wps.follow.manager.ZipFileManager;
import com.turingoal.bts.wps.follow.service.BreakdownRecordItemSyncService;
import com.turingoal.bts.wps.follow.ui.adapter.PhotoDetailAdapter;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.support.net.retrofit.TgHttpRetrofitCallback;
import com.turingoal.common.android.ui.layout.TgTitleContextLinearLayout;
import com.turingoal.common.android.util.lang.TgDateUtil;
import com.turingoal.common.android.util.ui.TgToastUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 分故障详情
 */
@Route(path = TgArouterPaths.BREAKDOWN_RECORD_ITEM_DETAIL)
public class BreakdownRecordItemDetailActivity extends TgBaseActivity {
    @BindView(R.id.ivEnd)
    ImageView ivEnd; // 同步提交
    @BindView(R.id.tclDiscoveryTime)
    TgTitleContextLinearLayout tclDiscoveryTime; // 发现时间
    @BindView(R.id.tclSystemType)
    TgTitleContextLinearLayout tclSystemType; // 故障类型
    @BindView(R.id.tclPattern)
    TgTitleContextLinearLayout tclPattern; // 故障模式
    @BindView(R.id.tclCode)
    TgTitleContextLinearLayout tclCode; // 故障代码
    @BindView(R.id.tclCarriage)
    TgTitleContextLinearLayout tclCarriage; // 故障车厢
    @BindView(R.id.tvDiscoveryDesc)
    TextView tvDiscoveryDesc; // 问题描述
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView; // recyclerView
    private static final int SPAN_COUNT = 3; // 照片列数
    @Autowired
    long breakdownRecordItemOid;
    private BreakdownRecordItem breakdownRecordItem; // 当前分故障

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_breakdown_item_detail;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tvTitle, R.id.ivStart, getString(R.string.title_breakdown_item_detail));  // 顶部工具条
        breakdownRecordItem = TgApplication.getBoxStore().boxFor(BreakdownRecordItem.class).get(breakdownRecordItemOid);
        tclDiscoveryTime.setContextStr(TgDateUtil.date2String(breakdownRecordItem.discoveryTime, TgDateUtil.FORMAT_YYYY_MM_DD_HH_MM_ZH));
        tclSystemType.setContextStr(breakdownRecordItem.systemType);
        tclPattern.setContextStr(breakdownRecordItem.pattern);
        tclCode.setContextStr(breakdownRecordItem.breakdownCode);
        tclCarriage.setContextStr(breakdownRecordItem.carriage);
        tvDiscoveryDesc.setText(breakdownRecordItem.discoveryDesc);
        ArrayList<String> paths = new ArrayList<>(); // 图片
        for (FilePathBean filePathBeanDb : breakdownRecordItem.filePathBeans) {
            paths.add(filePathBeanDb.path);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new PhotoDetailAdapter(paths));
        ivEnd.setImageResource(R.drawable.ic_sync);
        if ("yes".equals(breakdownRecordItem.sync)) { // 是否已经同步
            ivEnd.setVisibility(View.GONE);
        } else {
            ivEnd.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.ivEnd, R.id.btnZip})
    public void onClick(final View view) { // 打分包
        switch (view.getId()) {
            case R.id.ivEnd:
                upload();
                break;
            case R.id.btnZip:
                BreakdownRecord breakdownRecord = TgApplication.getBoxStore().boxFor(BreakdownRecord.class).get(breakdownRecordItem.breakdownRecordOid);
                if (new ZipFileManager(breakdownRecord, breakdownRecordItem).zipBreakdownItem() != null) {
                    ToastUtils.showLong(R.string.string_breakdown_item_detail_zip_success);
                } else {
                    ToastUtils.showLong(R.string.string_breakdown_item_detail_zip_failure);
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
        BreakdownRecord breakdownRecord = TgApplication.getBoxStore().boxFor(BreakdownRecord.class).get(breakdownRecordItem.breakdownRecordOid);
        String path = new ZipFileManager(breakdownRecord, breakdownRecordItem).zipBreakdownItem();
        if (path != null) {// 打包
            File file = new File(path); // 得到zip文件
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            TgApplication.getRetrofit().create(BreakdownRecordItemSyncService.class)
                    .upload(part)
                    .enqueue(new TgHttpRetrofitCallback<Object>(this, true, false) {
                        @Override
                        public void successHandler(Object o) {
                            TgToastUtil.showLong("上传成功");
                            breakdownRecordItem.sync = "yes";
                            TgApplication.getBoxStore().boxFor(BreakdownRecordItem.class).put(breakdownRecordItem);
                            ivEnd.setVisibility(View.GONE);
                        }
                    });
        } else {
            ToastUtils.showLong(R.string.string_breakdown_detail_zip_failure);
        }
    }
}
