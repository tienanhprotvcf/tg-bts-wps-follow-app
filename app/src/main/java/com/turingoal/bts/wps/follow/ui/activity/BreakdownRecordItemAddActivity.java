package com.turingoal.bts.wps.follow.ui.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.turingoal.android.photopicker.PhotoPicker;
import com.turingoal.bts.common.android.constants.ConstantMontorBreakdownPattern;
import com.turingoal.bts.common.android.constants.ConstantMontorBreakdownSystemTypes;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.BreakdownRecordItem;
import com.turingoal.bts.wps.follow.bean.FilePathBean;
import com.turingoal.bts.wps.follow.ui.adapter.PhotoAddAdapter;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgKeyValueBean;
import com.turingoal.common.android.bean.TgPhotoGroupBean;
import com.turingoal.common.android.ui.layout.TgFlowRadioGroupLayout;
import com.turingoal.common.android.util.lang.TgDateUtil;
import com.turingoal.common.android.util.ui.TgDialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.objectbox.Box;

/**
 * 新建分故障
 */
@Route(path = TgArouterPaths.BREAKDOWN_RECORD_ITEM_ADD)
public class BreakdownRecordItemAddActivity extends TgBaseActivity implements AdapterView.OnItemSelectedListener, TgFlowRadioGroupLayout.OnCheckedChangeListener {
    @BindView(R.id.tvDiscoveryTime)
    TextView tvDiscoveryTime; // 发现时间
    @BindView(R.id.spSystemType)
    Spinner spSystemType; // 故障类型
    @BindView(R.id.spPattern)
    Spinner spPattern; // 故障模式
    @BindView(R.id.etCode)
    EditText etCode; // 故障代码
    @BindView(R.id.frgCarriage)
    TgFlowRadioGroupLayout frgCarriage; // 车厢
    @BindView(R.id.etDiscoveryDesc)
    EditText etDiscoveryDesc; // 问题描述
    @BindView(R.id.tvDiscoveryDescCount)
    TextView tvDiscoveryDescCount; // 问题描述字数统计
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView; // recyclerView
    private PhotoAddAdapter adapter = new PhotoAddAdapter(); // adapter
    private static final int SPAN_COUNT = 3; // 照片列数
    private ItemTouchHelper mItemTouchHelper = null;
    @Autowired
    long breakdownRecordOid;
    private Date currentDate; // 当前时间
    private boolean isEdit; // 修改过数据,提示是否保存

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_breakdown_item_add;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tvTitle, getString(R.string.title_breakdown_item_add));  // 顶部工具条
        setToobarLeftButClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditDialog();
            }
        });
        currentDate = Calendar.getInstance().getTime();
        tvDiscoveryTime.setText(TgDateUtil.date2String(currentDate, TgDateUtil.FORMAT_YYYY_MM_DD_HH_MM_ZH));
        List<String> systemTypeList = new ArrayList<>();
        for (TgKeyValueBean tgKeyValueBean : ConstantMontorBreakdownSystemTypes.DATA_LIST) {
            String stringValue = tgKeyValueBean.getStringValue();
            systemTypeList.add(stringValue);
        }
        spSystemType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, systemTypeList));
        spSystemType.setSelection(0, true);
        spSystemType.setOnItemSelectedListener(this);
        List<String> patternList = new ArrayList<>();
        for (TgKeyValueBean tgKeyValueBean : ConstantMontorBreakdownPattern.DATA_LIST) {
            String stringValue = tgKeyValueBean.getStringValue();
            patternList.add(stringValue);
        }
        spPattern.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, patternList));
        spPattern.setSelection(0, true);
        spPattern.setOnItemSelectedListener(this);
        etCode.addTextChangedListener(codeWatcher);
        frgCarriage.setOnCheckedChangeListener(this);
        etDiscoveryDesc.addTextChangedListener(countWatcher);
        initAdapter();
    }

    /**
     * OnClick
     */
    @OnClick({R.id.llDiscoveryTime, R.id.btnSave})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.llDiscoveryTime:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        isEdit = true;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        currentDate = calendar.getTime();
                        tvDiscoveryTime.setText(TgDateUtil.date2String(currentDate, TgDateUtil.FORMAT_YYYY_MM_DD_HH_MM_ZH));
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true).show();
                break;
            case R.id.btnSave:
                if (TextUtils.isEmpty(etCode.getText().toString().trim())) {
                    ToastUtils.showLong(R.string.string_breakdown_item_no_code_hint);
                    return;
                }
                final RadioButton radioButton = frgCarriage.findViewById(frgCarriage.getCheckedRadioButtonId());
                if (radioButton == null) {
                    ToastUtils.showLong(R.string.string_breakdown_item_no_carriage_hint);
                    return;
                }
                if (TextUtils.isEmpty(etDiscoveryDesc.getText().toString().trim())) {
                    ToastUtils.showLong(R.string.string_breakdown_item_no_discovery_desc_hint);
                    return;
                }
                TgDialogUtil.showDialog(this, getString(R.string.string_breakdown_item_confirm_hint), new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        BreakdownRecordItem breakdownRecordItem = new BreakdownRecordItem();
                        breakdownRecordItem.id = UUID.randomUUID().toString().replace("-", ""); // * 故障分id很重要
                        breakdownRecordItem.discoveryTime = currentDate;
                        breakdownRecordItem.carriage = radioButton.getText().toString();
                        breakdownRecordItem.systemType = spSystemType.getSelectedItem().toString();
                        breakdownRecordItem.pattern = spPattern.getSelectedItem().toString();
                        breakdownRecordItem.breakdownCode = etCode.getText().toString().trim();
                        breakdownRecordItem.discoveryDesc = etDiscoveryDesc.getText().toString().trim();
                        for (String path : adapter.getData()) {
                            if (!PhotoAddAdapter.PICTURES_ADD.equals(path)) { // 数据结果去除最后一个加号
                                breakdownRecordItem.filePathBeans.add(new FilePathBean(path));
                            }
                        }
                        Box<BreakdownRecord> objectBox = TgApplication.getBoxStore().boxFor(BreakdownRecord.class);
                        BreakdownRecord breakdownRecord = objectBox.get(breakdownRecordOid);
                        breakdownRecord.sync = "no"; // 没有同步
                        breakdownRecordItem.indexNum = getBreakDownItemIndex(breakdownRecord.breakdownRecordItems.size() + 1); // 第几个故障,打zip文件名需要
                        breakdownRecordItem.breakdownRecordOid = breakdownRecord.oid; // * 故障总id很重要
                        breakdownRecordItem.breakdownRecordId = breakdownRecord.id; // * 故障总id很重要
                        breakdownRecordItem.trainSetTrips = breakdownRecord.trainSetTrips;
                        breakdownRecordItem.trainSetCodeNum = breakdownRecord.trainSetCodeNum;
                        breakdownRecordItem.source = breakdownRecord.source;
                        breakdownRecordItem.createTime = breakdownRecord.createTime;
                        breakdownRecordItem.recordTime = breakdownRecord.recordTime;
                        breakdownRecordItem.userCodeNum = breakdownRecord.userCodeNum;
                        breakdownRecordItem.sync = "no"; // 没有同步
                        breakdownRecord.breakdownRecordItems.add(breakdownRecordItem);
                        objectBox.put(breakdownRecord);
                        ToastUtils.showLong(R.string.string_breakdown_item_add_success);
                        EventBus.getDefault().post("ItemAddSuccess"); // 通知详情页面更新
                        defaultFinish();
                    }
                });
                break;
            default:
                break;
        }
    }

    private String getBreakDownItemIndex(int index) {
        if (index < 10) {
            return "00" + index;
        } else if (index < 100) {
            return "0" + index;
        } else {
            return "" + index;
        }
    }

    /**
     * 初始化recyclerView和adapter
     */
    private void initAdapter() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        mItemTouchHelper = new ItemTouchHelper(new ItemDragAndSwipeCallback(adapter) {
            @Override
            public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                // 最后一个为加号，所以不支持【拖拽换位置】
                return target.getAdapterPosition() != recyclerView.getAdapter().getItemCount() - 1 && super.canDropOver(recyclerView, current, target);
            }
        });
        mItemTouchHelper.attachToRecyclerView(recyclerView); // 附加到RecyclerView
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                // 如果item不是最后一个，则可以拖拽【不是加号，可以拖拽】
                if (position != adapter.getData().size() - 1) {
                    mItemTouchHelper.startDrag(recyclerView.getChildViewHolder(view));
                }
                return false;
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String url = (String) adapter.getItem(position);
                if (PhotoAddAdapter.PICTURES_ADD.equals(url)) { // 加号
                    // 选择图片或者视频
                    List<String> items = new ArrayList<>();
                    items.add("照片");
                    items.add("视频");
                    new MaterialDialog.Builder(BreakdownRecordItemAddActivity.this).items(items).itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            if (position == 0) {
                                selectPhoto();
                            } else {
                                selectVideo();
                            }
                        }
                    }).show();

                } else {
                    if (url.startsWith("content")) { // 查看视频，以content开头就是视频
                        VideoPlayerActivity.actionStart(BreakdownRecordItemAddActivity.this, url);
                    } else { // 查看图片
                        List<String> photos = new ArrayList<>(adapter.getData());
                        photos.remove(PhotoAddAdapter.PICTURES_ADD); // 删除最后一个加号
                        Iterator<String> it = photos.iterator();
                        while (it.hasNext()) { // 删除content开头的数据
                            String x = it.next();
                            if (x.startsWith("content")) {
                                it.remove();
                            }
                        }
                        TgPhotoGroupBean tgPhotoGroupBean = new TgPhotoGroupBean();
                        tgPhotoGroupBean.setIndexNum(photos.indexOf(url));
                        tgPhotoGroupBean.setPhotos(photos);
                        TgSystemHelper.handleIntentWithObj(TgArouterPaths.PHOTO_SHOW, "tgPhotoGroupBean", tgPhotoGroupBean); // 参考图片
                    }
                }
            }
        });
    }

    /**
     * 选择视频
     */
    private void selectVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_SIZE_LIMIT, 768000);  // 设置视频大小
        intent.putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT, 45000); // 设置视频时间  毫秒单位
        startActivityForResult(intent, 1);
    }

    /**
     * 选择照片
     */
    private void selectPhoto() {
        List<String> photos = new ArrayList<>(adapter.getData());
        photos.remove(PhotoAddAdapter.PICTURES_ADD); // 删除最后一个加号
        int i = PhotoAddAdapter.PICTURES_MAX_COUNT; // 最多可以选择9个文件
        Iterator<String> it = photos.iterator();
        while (it.hasNext()) { // 删除content开头的数据是视频
            String x = it.next();
            if (x.startsWith("content")) {
                it.remove();
                i--;
            }
        }
        String[] paths = photos.size() == 0 ? null : photos.toArray(new String[photos.size()]); // 已经选择的图片
        PhotoPicker.selectPics(this, i, paths, new PhotoPicker.PicCallBack() { // 加上视频最多选择9个
            @Override
            public void onPicSelected(final String[] paths) {
                List<String> pathList = new ArrayList<>();
                pathList.addAll(Arrays.asList(paths)); // UnsupportedOperationException
                List<String> data = adapter.getData();
                pathList.removeAll(data); // 新选择的里面删除原来已经有的
                data.remove(PhotoAddAdapter.PICTURES_ADD);// 删除最后一个加号
                data.addAll(pathList); // 原来已经有的，加上新选择的，保留顺序
                if (data.size() < PhotoAddAdapter.PICTURES_MAX_COUNT) { // 小于9个文件
                    data.add(PhotoAddAdapter.PICTURES_ADD); //　添加加号
                }
                adapter.setNewData(data);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == BreakdownRecordItemAddActivity.RESULT_OK) { //    判断拍摄成功
                if (data != null) {
                    Uri uri = data.getData();//可以通过这个播放
                    if (uri != null) {
                        String uriStr = uri.toString();
                        if (!TextUtils.isEmpty(uriStr)) {
                            List<String> adapterData = new ArrayList<>(adapter.getData());
                            adapterData.remove(PhotoAddAdapter.PICTURES_ADD);
                            adapterData.add(uriStr);
                            if (adapterData.size() < PhotoAddAdapter.PICTURES_MAX_COUNT) {
                                adapterData.add(PhotoAddAdapter.PICTURES_ADD);
                            }
                            adapter.setNewData(adapterData);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        isEditDialog();
    }

    /**
     * 如果用户编辑过数据，但是没有保存，提示是否没有保存就退出
     */
    private void isEditDialog() {
        if (isEdit) {
            TgDialogUtil.showDialog(this, getString(R.string.stirng_breakdown_item_no_save_hint), new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    defaultFinish();
                }
            });
        } else {
            defaultFinish();
        }
    }

    /**
     * 问题描述内容长度监听
     */
    private TextWatcher countWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            isEdit = true;
            tvDiscoveryDescCount.setText("" + editable.toString().trim().length() + "/140"); // 文字计数 140个
        }
    };

    /**
     * 故障代码EditText是否编辑过监听
     */
    private TextWatcher codeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            isEdit = true;
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        isEdit = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(TgFlowRadioGroupLayout group, int checkedId) {
        isEdit = true;
    }
}
