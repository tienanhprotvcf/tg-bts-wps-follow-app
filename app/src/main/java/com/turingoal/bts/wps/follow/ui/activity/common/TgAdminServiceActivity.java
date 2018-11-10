package com.turingoal.bts.wps.follow.ui.activity.common;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.bts.wps.follow.app.TgApplication;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgValidateResultBean;
import com.turingoal.common.android.util.ui.TgToastUtil;
import com.turingoal.common.android.validatror.TgNetValidator;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 服务器配置页面
 */
@Route(path = TgArouterCommonPaths.COMMON_AMIN_CONFIG_SERVER)
public class TgAdminServiceActivity extends TgBaseActivity {
    @BindView(R.id.tg_common_layout_admin_service_ivLogo)
    ImageView ivLogo; // logo
    @BindView(R.id.tg_common_layout_admin_service_ivTitleLogo)
    ImageView ivTitleLogo; // titleLogo
    @BindView(R.id.tg_common_layout_admin_service_tvContactName)
    TextView tvContactName; // 联系名称
    @BindView(R.id.et_ip)
    EditText etIp; // 输入框 ip
    @BindView(R.id.et_port)
    EditText etPort;  // 输入框 端口

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_admin_service;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, R.id.tg_common_view_title_bar_ivLeftBut, "服务器配置"); // 顶部工具条
        ivLogo.setImageResource(R.mipmap.common_logo);
        ivTitleLogo.setImageResource(R.mipmap.common_title_logo);
        tvContactName.setText(TgAppConfig.CONTRACT_NAME); // 联系名称
        String ip = TgApplication.getTgUserPreferences().getServerIp();
        if (!TextUtils.isEmpty(ip)) {
            etIp.setText(ip); // 如果IP已设置显示进输入框
        }
        Integer port = TgApplication.getTgUserPreferences().getServerPort();
        if (port > 0) {
            etPort.setText(String.valueOf(port)); // 如果端口已设置显示进输入框
        }
    }

    /**
     * 提交
     */
    @OnClick({R.id.btn_commit})
    public void onClick() {
        String ip = etIp.getText().toString().trim(); // ip
        String port = etPort.getText().toString().trim(); // 端口
        TgValidateResultBean validateResult = TgNetValidator.validateIpAndPort(ip, port);
        if (!validateResult.isSuccess()) { // 验证失败
            TgToastUtil.showLong(validateResult.getMsg());
        } else {
            TgApplication.getTgUserPreferences().setServerIp(ip); // 保存服务器ip
            TgApplication.getTgUserPreferences().setServerPort(Integer.parseInt(port)); // 保存服务器端口
            TgApplication.initRetrofit(); // 重新初始化retrofit
            TgToastUtil.showLong("服务器配置修改成功！");
            defaultFinish(); //返回
        }
    }
}
