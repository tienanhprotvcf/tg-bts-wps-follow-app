package com.turingoal.bts.wps.follow.ui.activity.common;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.bts.wps.follow.app.TgSystemHelper;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgValidateResultBean;
import com.turingoal.common.android.ui.listener.TgTextAfterChangedWatcher;
import com.turingoal.common.android.validatror.TgUsernamePasswordValidator;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改密码
 */
@Route(path = TgArouterCommonPaths.COMMON_CHANGE_PASSWORD)
public class TgChangePasswordActivity extends TgBaseActivity {
    @BindView(R.id.tg_common_layout_change_pass_etPassOld)
    EditText etPasswordOld; // 旧密码
    @BindView(R.id.tg_common_layout_change_pass_tilPassOld)
    TextInputLayout tilPasswordOld; // 旧密码控制
    @BindView(R.id.tg_common_layout_change_pass_etPassNew)
    EditText etPasswordNew; // 新密码
    @BindView(R.id.tg_common_layout_change_pass_tilPassNew)
    TextInputLayout tilPasswordNew; // 新密码控制
    @BindView(R.id.tg_common_layout_change_pass_etPassAgain)
    EditText etPasswordAgain; // 确认密码
    @BindView(R.id.tg_common_layout_change_pass_tilPassAgain)
    TextInputLayout tilPasswordAgain; // 确认密码控制

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_change_pass;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, R.id.tg_common_view_title_bar_ivLeftBut, getString(R.string.title_common_pass_change)); // 顶部工具条
        etPasswordOld.addTextChangedListener(passwordTextWatcherOld); // 旧密码内容监听
        etPasswordNew.addTextChangedListener(passwordTextWatcherNew);  // 新密码内容监听
        etPasswordAgain.addTextChangedListener(passwordTextWatcherAgain); // 确认密码内容监听
        tilPasswordOld.setPasswordVisibilityToggleEnabled(true); // 点击显示密码
        tilPasswordNew.setPasswordVisibilityToggleEnabled(true); // 点击显示密码
        tilPasswordAgain.setPasswordVisibilityToggleEnabled(true); // 点击显示密码
    }

    /**
     * 旧密码EditText监听
     */
    private TextWatcher passwordTextWatcherOld = new TgTextAfterChangedWatcher() {
        @Override
        public void afterTextChanged(final Editable editable) {
            tilPasswordOld.setErrorEnabled(false);
            if (editable.toString().trim().length() > TgUsernamePasswordValidator.MAX_PASSWORD_LEN) { // 字数大于最大限制，提示错误
                tilPasswordOld.setError(getString(R.string.hint_common_pass_change_max));
            }
        }
    };

    /**
     * 新密码EditText监听
     */
    private TextWatcher passwordTextWatcherNew = new TgTextAfterChangedWatcher() {
        @Override
        public void afterTextChanged(final Editable editable) {
            tilPasswordNew.setErrorEnabled(false);
            if (editable.toString().trim().length() > TgUsernamePasswordValidator.MAX_PASSWORD_LEN) { // 字数大于最大限制，提示错误
                tilPasswordNew.setError(getString(R.string.hint_common_pass_change_max));
            }
        }
    };

    /**
     * 确认密码EditText监听
     */
    private TextWatcher passwordTextWatcherAgain = new TgTextAfterChangedWatcher() {
        @Override
        public void afterTextChanged(final Editable editable) {
            tilPasswordAgain.setErrorEnabled(false);
            if (editable.toString().trim().length() > TgUsernamePasswordValidator.MAX_PASSWORD_LEN) { // 字数大于最大限制，提示错误
                tilPasswordAgain.setError(getString(R.string.hint_common_pass_change_max));
            }
        }
    };

    /**
     * 修改密码
     */
    @OnClick(R.id.tg_common_layout_change_pass_btnChangePass)
    public void goChangePassword() {
        String passwordOld = etPasswordOld.getText().toString().trim();
        String passwordNew = etPasswordNew.getText().toString().trim();
        String passwordAgain = etPasswordAgain.getText().toString().trim();
        TgValidateResultBean validateResult = TgUsernamePasswordValidator.validatePassword(passwordOld, passwordNew, passwordAgain);
        if (!validateResult.isSuccess()) { // 验证失败
            String fieldName = validateResult.getFieldName();
            if (TgUsernamePasswordValidator.FIELD_NAME_OLD_PASSWORRD.equals(fieldName)) { // 旧密码
                tilPasswordOld.setError(validateResult.getMsg());
            } else if (TgUsernamePasswordValidator.FIELD_NAME_NEW_PASSWORRD.equals(fieldName)) { // 新密码
                tilPasswordNew.setError(validateResult.getMsg());
            } else if (TgUsernamePasswordValidator.FIELD_NAME_CONFIRM_PASSWORRD.equals(fieldName)) { // 确认密码
                tilPasswordAgain.setError(validateResult.getMsg());
            }
        } else {
            if (!TgApplication.getTgUserPreferences().getUserPassword().equals(EncryptUtils.encryptMD5ToString(passwordOld))) { // 旧密码不对
                tilPasswordOld.setError(getString(R.string.hint_common_pass_change_error));
            } else { // 密码正确
                TgApplication.getTgUserPreferences().setUserPassword(EncryptUtils.encryptMD5ToString(passwordNew)); // 保存新密码
                ToastUtils.showLong(R.string.hint_common_pass_change_success); // 提示修改成功
                TgSystemHelper.logoff(this); // 重新登录
            }
        }
    }
}
