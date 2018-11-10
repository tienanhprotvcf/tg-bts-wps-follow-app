package com.turingoal.bts.wps.follow.ui.activity.common;

import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人信息
 */
@Route(path = TgArouterCommonPaths.COMMON_SELF_INFO)
public class TgSelfInfoActivity extends TgBaseActivity {
    @BindView(R.id.tvUserCodeNum)
    TextView tvUserCodeNum; // 工号

    @Override
    protected int getLayoutId() {
        return R.layout.common_activity_self_info;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, R.id.tg_common_view_title_bar_ivLeftBut, getString(R.string.title_common_self_info)); // 顶部工具条
        tvUserCodeNum.setText(TgApplication.getTgUserPreferences().getUserCodeNum()); // 显示工号
    }

    /**
     * 修改密码
     */
    @OnClick(R.id.btnChangePassword)
    public void goChangePassword() {
        TgSystemHelper.handleIntent(TgArouterCommonPaths.COMMON_CHANGE_PASSWORD);
    }
}
