package com.turingoal.bts.wps.follow.ui.activity.common;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgValidateResultBean;
import com.turingoal.common.android.util.ui.TgToastUtil;
import com.turingoal.common.android.validatror.TgUsernamePasswordValidator;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 管理员登录页
 */
@Route(path = TgArouterCommonPaths.COMMON_AMIN_LOGIN)
public class TgAdminLoginActivity extends TgBaseActivity {
    @BindView(R.id.tg_common_layout_admin_login_ivLogo)
    ImageView ivLogo; // logo
    @BindView(R.id.tg_common_layout_admin_login_ivTitleLogo)
    ImageView ivTitleLogo; // titleLogo
    @BindView(R.id.tg_common_layout_admin_login_etPass)
    EditText etPassword; // 密码框
    @BindView(R.id.tg_common_layout_admin_login_tvContactName)
    TextView tvContactName; // 联系名称

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_admin_login;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, R.id.tg_common_view_title_bar_ivLeftBut, "登录"); // 顶部工具条
        ivLogo.setImageResource(R.mipmap.common_logo);
        ivTitleLogo.setImageResource(R.mipmap.common_title_logo);
        tvContactName.setText(TgAppConfig.CONTRACT_NAME); // 联系名称
    }

    /**
     * 点击
     */
    @OnClick(R.id.tg_common_layout_admin_login_btnLogin)
    public void goLogin() {
        String password = etPassword.getText().toString().trim(); // 密码
        TgValidateResultBean validateResult = TgUsernamePasswordValidator.validatePassword(password); // 校验密码
        if (!validateResult.isSuccess()) {
            TgToastUtil.showShort(validateResult.getMsg());
        } else {
            if (TgAppConfig.ADMIN_DEFAULT_PASSWORD.equals(EncryptUtils.encryptMD5ToString(password))) { // 和管理员默认密码比较
                TgSystemHelper.handleIntent(TgArouterCommonPaths.COMMON_AMIN_CONFIG);
            } else {
                ToastUtils.showLong("管理员密码错误");
            }
        }
    }
}
