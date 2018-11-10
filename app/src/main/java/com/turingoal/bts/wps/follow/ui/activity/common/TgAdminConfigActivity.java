package com.turingoal.bts.wps.follow.ui.activity.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 管理员功能页
 */
@Route(path = TgArouterCommonPaths.COMMON_AMIN_CONFIG)
public class TgAdminConfigActivity extends TgBaseActivity {
    @BindView(R.id.tg_common_layout_admin_config_ivLogo)
    ImageView ivLogo; // logo
    @BindView(R.id.tg_common_layout_admin_config_ivTitleLogo)
    ImageView ivTitleLogo; // titleLogo
    @BindView(R.id.tg_common_layout_admin_config_tvContactName)
    TextView tvContactName; // 联系名称

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_admin_config;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, R.id.tg_common_view_title_bar_ivLeftBut, "管理员"); // 顶部工具条
        ivLogo.setImageResource(R.mipmap.common_logo);
        ivTitleLogo.setImageResource(R.mipmap.common_title_logo);
        tvContactName.setText(TgAppConfig.CONTRACT_NAME); // 联系名称
    }

    /**
     * 点击
     */
    @OnClick({R.id.tg_common_layout_admin_config_tvReset, R.id.tg_common_layout_admin_config_tvIP})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.tg_common_layout_admin_config_tvReset:
                TgApplication.getTgUserPreferences().setUserPassword(null); // 清除密码
                ToastUtils.showLong("重置密码成功！"); // 提示成功
                TgSystemHelper.logoff(this); // 注销
                break;
            case R.id.tg_common_layout_admin_config_tvIP:
                TgSystemHelper.handleIntent(TgArouterCommonPaths.COMMON_AMIN_CONFIG_SERVER); // 服务器配置页面
                break;
            default:
                break;
        }
    }
}
