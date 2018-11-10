package com.turingoal.bts.wps.follow.app;

import com.turingoal.bts.wps.follow.R;
import com.turingoal.common.android.app.TgArouterCommonPaths;
import com.turingoal.common.android.base.TgBaseAppConfig;
import com.turingoal.common.android.bean.TgGridItem;
import com.turingoal.common.android.util.io.TgSdCardUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统配置
 */
public class TgAppConfig extends TgBaseAppConfig {
    // 关于
    public static final String CONTRACT_WEBSITE = "www.bjtds.com.cn"; // 联系网址
    public static final String CONTRACT_TEL = "010-51852819"; //  联系电话
    public static final String CONTRACT_ADDRESS = "北京中关村科技园总部基地"; // 联系地址
    public static final String CONTRACT_NAME = "北京铁道工程机电技术研究所股份有限公司"; // 联系名称
    // 基本配置
    public static final String PROJECT_NAME = "tg-bts-wps—follow"; // 项目名字
    public static final String APP_BASE_PATH = "/bts/wps/follow/"; // 页面路由库，要求二级路径，防止出错
    public static final String LOG_TAG = PROJECT_NAME + "-log"; // log tag
    public static final String SP_NAME = PROJECT_NAME + "-sp"; // SharedPreferences名称
    public static final String ADMIN_DEFAULT_PASSWORD = "E80B5017098950FC58AAD83C8C14978E"; // 管理员默认经过md5加密的密码,abcdef
    public static final String USER_DEFAULT_PASSWORD = "E10ADC3949BA59ABBE56E057F20F883E"; // 用户默认经过md5加密的密码,123456
    // server 配置
    public static final String SERVER_IP = "192.168.0.254"; // server ip
    public static final Integer SERVER_PORT = 8086; // server 端口
    public static final String SERVER_NAME = "tg-bts-wps"; // server 名称
    // 文件路径
    public static final String APP_DIR = "0tgBts_wps_follow"; // 项目使用的整体目录
    public static final String ROOT_FULL_DIR = TgSdCardUtil.getSDCardPathByEnvironment() + File.separator + TgAppConfig.APP_DIR + File.separator; // 最根目录全路径
    // zip打包相关
    public static final String ZIP_DIR = "0zip"; // 打包好zip，放到0zip目录下
    public static final String ZIP_FULL_DIR = TgAppConfig.ROOT_FULL_DIR + TgAppConfig.ZIP_DIR + File.separator; // zip 全路径
    public static final String ZIP_FILES_DIR = "files"; // 故障图片存放的路径
    public static final String ZIP_JSON_DATA_NAME = "data.json"; // json数据的文件名
    public static final String ZIP_PASSWORD = "bjtds666"; // zip密码
    public static final String ZIP_MD5_KEY = "tgBts"; // zip的md5的key
    public static final String ZIP_EXTENSION = ".zip"; // zip后缀
    // 首页轮播图
    public static final List<Integer> MAIN_BANNER_IMAGES; // 图片
    public static final List<String> MAIN_BANNER_TITLES; // 标题
    // 首页九宫格
    public static final List<TgGridItem> MAIN_GRID_DATE; // 首页九宫格

    static {
        // 首页轮播图
        MAIN_BANNER_IMAGES = new ArrayList<>();
        MAIN_BANNER_IMAGES.add(R.mipmap.p1);
        MAIN_BANNER_IMAGES.add(R.mipmap.p2);
        MAIN_BANNER_IMAGES.add(R.mipmap.p3);
        MAIN_BANNER_IMAGES.add(R.mipmap.p4);
        MAIN_BANNER_TITLES = new ArrayList<>();
        MAIN_BANNER_TITLES.add("擎起中国高铁脊梁走向世界");
        MAIN_BANNER_TITLES.add("和谐大功率机车整车试验台");
        MAIN_BANNER_TITLES.add("动车组安全联锁监控系统");
        MAIN_BANNER_TITLES.add("运营安全产品在线监测系统");
        // 首页九宫格
        MAIN_GRID_DATE = new ArrayList<>();
        MAIN_GRID_DATE.add(new TgGridItem(R.mipmap.ic_add, "新建记录", TgArouterPaths.BREAKDOWN_RECORD_ADD));
        MAIN_GRID_DATE.add(new TgGridItem(R.mipmap.ic_fault, "故障记录", TgArouterPaths.BREAKDOWN_RECORD));
        MAIN_GRID_DATE.add(new TgGridItem(R.drawable.ic_userinfo, "个人信息", TgArouterCommonPaths.COMMON_SELF_INFO));
        MAIN_GRID_DATE.add(new TgGridItem(R.drawable.ic_about, "关于系统", TgArouterCommonPaths.COMMON_ABOUT));
        MAIN_GRID_DATE.add(new TgGridItem(R.drawable.ic_help, "系统帮助", TgArouterCommonPaths.COMMON_HELP));
        MAIN_GRID_DATE.add(new TgGridItem(R.drawable.ic_logout, "注销退出", TgArouterCommonPaths.COMMON_LOGOFF));
    }
}
