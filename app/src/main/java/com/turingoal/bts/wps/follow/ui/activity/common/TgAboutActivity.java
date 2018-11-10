package com.turingoal.bts.wps.follow.ui.activity.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.turingoal.bts.wps.follow.BuildConfig;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.util.system.TgSystemUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于系统
 */
@Route(path = TgArouterCommonPaths.COMMON_ABOUT)
public class TgAboutActivity extends TgBaseActivity {
    @BindView(R.id.tg_common_layout_about_ivLogo)
    ImageView ivLogo; // logo
    @BindView(R.id.tg_common_layout_about_ivTitleLogo)
    ImageView ivTitleLogo; // titleLogo
    @BindView(R.id.tg_common_layout_about_tvVersion)
    TextView tvVersion; // 软件版本号
    @BindView(R.id.tg_common_layout_about_tvContactWebsite)
    TextView tvContactWebsite; // 联系网址
    @BindView(R.id.tg_common_layout_about_tvContactTel)
    TextView tvContactTel; // 联系电话
    @BindView(R.id.tg_common_layout_about_tvContactAddress)
    TextView tvContactAddress; // 联系地址
    @BindView(R.id.tg_common_layout_about_tvContactName)
    TextView tvContactName; // 联系名称

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_about;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, R.id.tg_common_view_title_bar_ivLeftBut, "关于我们"); // 顶部工具条
        ivLogo.setImageDrawable(getResources().getDrawable(R.mipmap.common_logo));
        ivTitleLogo.setImageDrawable(getResources().getDrawable(R.mipmap.common_title_logo));
        tvVersion.setText(BuildConfig.VERSION_NAME); // 版本名称
        tvContactWebsite.setText(TgAppConfig.CONTRACT_WEBSITE); // 联系网址
        tvContactTel.setText(TgAppConfig.CONTRACT_TEL); // 联系电话
        tvContactAddress.setText(TgAppConfig.CONTRACT_ADDRESS); // 联系地址
        tvContactName.setText(TgAppConfig.CONTRACT_NAME); // 联系名称
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.rlWebsite, R.id.rlTel})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.rlWebsite:
                TgSystemUtil.openWebSite(TgAppConfig.CONTRACT_WEBSITE); // 打开网址
                break;
            case R.id.rlTel:
                TgSystemUtil.call(TgAppConfig.CONTRACT_TEL); // 拨打电话号码
                break;
            default:
                break;
        }
    }
}
