package com.turingoal.bts.wps.follow.ui.activity.common;

import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.turingoal.bts.wps.follow.R;
import com.turingoal.bts.wps.follow.app.TgArouterPaths;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.bean.TgPhotoGroupBean;
import com.turingoal.common.android.ui.adapter.TgPhotoViewPagerAdapter;
import com.turingoal.common.android.ui.fragment.TgPhotoFragment;
import com.turingoal.common.android.util.ui.TgToastUtil;

import java.util.List;

import butterknife.BindView;

/**
 * 通用单独图片查看界面
 */
@Route(path = TgArouterPaths.PHOTO_SHOW)
public class TgPhotoActivity extends TgBaseActivity {
    @BindView(R.id.vpPhoto)
    ViewPager vpPhoto; // 图片
    @Autowired
    TgPhotoGroupBean tgPhotoGroupBean; // 照片数据

    @Override
    protected int getLayoutId() {
        return R.layout.tg_common_layout_photo_activity;
    }

    @Override
    protected void initialized() {
        if (tgPhotoGroupBean != null) {
            List<TgPhotoFragment> fragments = TgPhotoFragment.createPhotoFragments(tgPhotoGroupBean); // 图片fragment
            vpPhoto.setAdapter(new TgPhotoViewPagerAdapter(getSupportFragmentManager(), fragments));
            vpPhoto.setCurrentItem(tgPhotoGroupBean.getIndexNum());
        } else {
            TgToastUtil.showLong("系统错误，请联系管理员");
            defaultFinish();
        }
    }
}