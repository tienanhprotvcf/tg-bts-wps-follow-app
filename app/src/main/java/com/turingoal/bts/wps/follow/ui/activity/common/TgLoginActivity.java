package com.turingoal.bts.wps.follow.ui.activity.common;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgValidateResultBean;
import com.turingoal.common.android.constants.TgConstantYesNo;
import com.turingoal.common.android.ui.listener.TgTextAfterChangedWatcher;
import com.turingoal.common.android.ui.widget.TgClearEditText;
import com.turingoal.common.android.ui.widget.TgLongClickImageView;
import com.turingoal.common.android.util.lang.TgStringUtil;
import com.turingoal.common.android.util.ui.TgToastUtil;
import com.turingoal.common.android.validatror.TgUsernamePasswordValidator;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录页
 */
@Route(path = TgArouterCommonPaths.COMMON_LOGIN)
public class TgLoginActivity extends TgBaseActivity {
    @BindView(R.id.tg_common_layout_login_ivLogo)
    TgLongClickImageView ivLogo; // logo,长按进入系统管理员密码
    @BindView(R.id.tg_common_layout_login_ivTitleLogo)
    ImageView ivTitleLogo; // titleLogo
    @BindView(R.id.tg_common_layout_login_tvContactName)
    TextView tvContactName; // 联系名称
    @BindView(R.id.tg_common_layout_login_etUsername)
    TgClearEditText etUsername; // 带删除按钮的输入框，用户名
    @BindView(R.id.tg_common_layout_login_tilUsername)
    TextInputLayout tilUsername; // 用户名控制
    @BindView(R.id.tg_common_layout_login_etPassword)
    EditText etPassword; // 密码框
    @BindView(R.id.tg_common_layout_login_tilPassword)
    TextInputLayout tilPassword; // 密码控制

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_login;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, getString(R.string.title_common_login)); // 顶部工具条
        ivLogo.setImageDrawable(getResources().getDrawable(R.mipmap.common_logo));
        ivLogo.setClickListenere(new TgLongClickImageView.LongClickListener() {
            @Override
            public void longClick() {
                TgSystemHelper.handleIntent(TgArouterCommonPaths.COMMON_AMIN_LOGIN); // 进入系统管理员登录界面
            }
        });
        ivTitleLogo.setImageDrawable(getResources().getDrawable(R.mipmap.common_title_logo));
        tvContactName.setText(TgAppConfig.CONTRACT_NAME); // 联系名称
        etUsername.setText(TgApplication.getTgUserPreferences().getUserCodeNum()); // 显示保存的用户名
        etUsername.addTextChangedListener(userNameTextWatcher); // 用户名内容监听
        etPassword.addTextChangedListener(passwordTextWatcher); // 密码内容监听
        tilPassword.setPasswordVisibilityToggleEnabled(true); // 点击显示密码
        String userCodeNum = TgApplication.getTgUserPreferences().getUserCodeNum();
        if (!TextUtils.isEmpty(userCodeNum)) {
            etUsername.setText(userCodeNum);
            etPassword.requestFocus();
        }
    }

    /**
     * 用户名EditText监听
     */
    private TextWatcher userNameTextWatcher = new TgTextAfterChangedWatcher() {
        @Override
        public void afterTextChanged(final Editable editable) {
            tilUsername.setErrorEnabled(false);
            if (editable.toString().trim().length() > TgUsernamePasswordValidator.MAX_USERNAME_LEN) { // 字数大于最大限制，提示错误
                tilUsername.setError(getString(R.string.hint_common_login_username_max));
            }
        }
    };

    /**
     * 密码EditText监听
     */
    private TextWatcher passwordTextWatcher = new TgTextAfterChangedWatcher() {
        @Override
        public void afterTextChanged(final Editable editable) {
            tilPassword.setErrorEnabled(false);
            if (editable.toString().trim().length() > TgUsernamePasswordValidator.MAX_PASSWORD_LEN) { // 字数大于最大限制，提示错误
                tilPassword.setError(getString(R.string.hint_common_login_password_max));
            }
        }
    };

    /**
     * 登录
     */
    @OnClick(R.id.tg_common_layout_login_btnLogin)
    public void goLogin() {
        String username = etUsername.getText().toString().trim(); // 用户名
        String password = etPassword.getText().toString().trim(); // 密码
        TgValidateResultBean validateResult = TgUsernamePasswordValidator.validateUsernameAndPassword(username, password); // 校验用户名和密码
        if (!validateResult.isSuccess()) {
            TgToastUtil.showShort(validateResult.getMsg());
        } else {
            if (TgStringUtil.isBlank(TgApplication.getTgUserPreferences().getUserPassword())) { // 如果以前没有输入过密码，和默认密码比较
                if (!TgAppConfig.USER_DEFAULT_PASSWORD.equals(EncryptUtils.encryptMD5ToString(password))) { // 密码错误
                    ToastUtils.showLong(R.string.hint_common_login_username_password_error);
                    return;
                }
            } else { // 和以前输入过的密码比较
                if (!TgApplication.getTgUserPreferences().getUserPassword().equals(EncryptUtils.encryptMD5ToString(password))) { // 密码错误
                    ToastUtils.showLong(R.string.hint_common_login_username_password_error);
                    return;
                }
            }
            TgApplication.getTgUserPreferences().setUserLoginStaus(TgConstantYesNo.YES); // 设置登录状态为已登录
            TgApplication.getTgUserPreferences().setUserCodeNum(username); // 保存用户工号
            TgApplication.getTgUserPreferences().setUserPassword(EncryptUtils.encryptMD5ToString(password)); // 保存用户密码
            TgSystemHelper.handleIntentAndFinish(TgArouterCommonPaths.MAIN_INDEX, this); // 跳转到主页面，关闭当前页面
        }
    }

    /**
     * 点击返回按钮
     */
    @Override
    public void onBackPressed() {
        TgSystemHelper.dbClickExit(); //  再按一次退出系统
    }
}
