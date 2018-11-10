package com.turingoal.bts.wps.follow.ui.activity.common;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.barteksc.pdfviewer.PDFView;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgAppConfig;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.util.doc.TgPdfUtil;

import butterknife.BindView;

/**
 * 系统帮助
 */
@Route(path = TgArouterCommonPaths.COMMON_HELP)
public class TgHelpActivity extends TgBaseActivity {
    @BindView(R.id.tg_common_layout_help_pdfView)
    public PDFView pdfView; // pdf组件

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_help;
    }

    @Override
    protected void initialized() {
        initToolbar(R.id.tg_common_view_title_bar_tvTitle, R.id.tg_common_view_title_bar_ivLeftBut, getString(R.string.title_common_help)); // 顶部工具栏
        TgPdfUtil.loadPdfFromAsset(pdfView, TgAppConfig.HELP_DOC_FILE_NAME); // 从Asset加载帮助文件pdf
    }
}