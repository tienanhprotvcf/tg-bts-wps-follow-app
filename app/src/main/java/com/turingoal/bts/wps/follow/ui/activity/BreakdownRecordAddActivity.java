package com.turingoal.bts.wps.follow.ui.activity;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.bts.wps.follow.bean.BreakdownRecord;
import com.turingoal.bts.wps.follow.bean.TrainSet;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.util.lang.TgDateUtil;
import com.turingoal.common.android.util.ui.TgDateTimePickUtil;
import com.turingoal.common.android.util.ui.TgDialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新建总故障
 */
@Route(path = TgArouterPaths.BREAKDOWN_RECORD_ADD)
public class BreakdownRecordAddActivity extends TgBaseActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.tvRecordTime)
    TextView tvRecordTime; // 当前时间
    @BindView(R.id.tvUserId)
    TextView tvUserId; // 工号
    @BindView(R.id.spTrainSetTrips)
    Spinner spTrainSetTrips; //车次 C D G
    @BindView(R.id.etTrainSetTrips)
    EditText etTrainSetTrips;
    @BindView(R.id.spTrainSetCodeNum)
    Spinner spTrainSetCodeNum; // 车组，车号
    @BindView(R.id.spSource)
    Spinner spSource; // 来源
    private boolean isEdit; // 修改过数据，提示是否保存
    private Date recordTime; // 记录时间

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_breakdown_add;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tvTitle, getString(R.string.title_breakdown_add));  // 顶部工具条
        setToobarLeftButClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditDialog();
            }
        });
        recordTime = Calendar.getInstance().getTime();
        tvRecordTime.setText(TgDateUtil.date2String(recordTime, TgDateUtil.FORMAT_YYYY_MM_DD_ZH));
        tvUserId.setText(TgApplication.getTgUserPreferences().getUserCodeNum());
        spTrainSetTrips.setSelection(0, true); // 不要默认执行
        spTrainSetTrips.setOnItemSelectedListener(this);
        List<TrainSet> trainSets = TgApplication.getBoxStore().boxFor(TrainSet.class).getAll(); // 所有车组数据
        List<String> trainSetCodeNumList = new ArrayList<>();
        for (TrainSet trainSet : trainSets) {
            trainSetCodeNumList.add(trainSet.codeNum);
        }
        spTrainSetCodeNum.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, trainSetCodeNumList));
        spTrainSetCodeNum.setSelection(0, true);
        spTrainSetCodeNum.setOnItemSelectedListener(this);
        etTrainSetTrips.addTextChangedListener(trainSetTripsWatcher);
    }

    @OnClick({R.id.btnSave, R.id.llRecordime})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                if (TextUtils.isEmpty(etTrainSetTrips.getText().toString())) {
                    ToastUtils.showLong(R.string.string_breakdown_no_trainSet_trips_hint);
                    return;
                }
                if (spTrainSetCodeNum.getSelectedItem() == null) {
                    ToastUtils.showLong("请在联网环境下打开应用，更新车组数据"); // 如果车组为空，可能是打开app的时候，没有请求到车组数据
                    return;
                }
                TgDialogUtil.showDialog(this, getString(R.string.string_breakdown_confirm_hint), new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        BreakdownRecord breakdownRecord = new BreakdownRecord();
                        breakdownRecord.id = UUID.randomUUID().toString().replace("-", ""); // * 故障总id很重要
                        breakdownRecord.userCodeNum = TgApplication.getTgUserPreferences().getUserCodeNum();
                        breakdownRecord.createTime = Calendar.getInstance().getTime();
                        breakdownRecord.createTimeStr = TgDateUtil.date2String(Calendar.getInstance().getTime(), TgDateUtil.FORMAT_YYYYMM);
                        breakdownRecord.recordTime = recordTime;
                        breakdownRecord.trainSetTrips = spTrainSetTrips.getSelectedItem().toString() + etTrainSetTrips.getText().toString(); // 车次
                        if (spTrainSetCodeNum.getSelectedItem() != null) {
                            breakdownRecord.trainSetCodeNum = spTrainSetCodeNum.getSelectedItem().toString(); // 车组
                        } else {
                            breakdownRecord.trainSetCodeNum = "";
                        }
                        breakdownRecord.source = spSource.getSelectedItem().toString(); // 来源
                        TgApplication.getBoxStore().boxFor(BreakdownRecord.class).put(breakdownRecord);
                        ToastUtils.showLong(R.string.string_breakdown_add_success);
                        EventBus.getDefault().post("BreakdownRecordAddSuccess");
                        defaultFinish();
                    }
                });
                break;
            case R.id.llRecordime:
                new TgDateTimePickUtil(this, recordTime, TgDateUtil.DEFAULT_START_DATE, Calendar.getInstance().getTime(), null)
                        .dateTimePickDialog(new TgDateTimePickUtil.OnSubmitSelectDateListener() {
                            @Override
                            public void onSumbitSelect(Date date) {
                                recordTime = date;
                                tvRecordTime.setText(TgDateUtil.date2String(recordTime, TgDateUtil.FORMAT_YYYY_MM_DD_ZH));
                            }
                        });
                break;
            default:
                break;
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
            TgDialogUtil.showDialog(this, getString(R.string.stirng_breakdown_no_save_hint), new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    defaultFinish();
                }
            });
        } else {
            defaultFinish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        isEdit = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * 车次EditText是否编辑过监听
     */
    private TextWatcher trainSetTripsWatcher = new TextWatcher() {
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
}
