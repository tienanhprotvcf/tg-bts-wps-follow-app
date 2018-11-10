package com.turingoal.bts.wps.follow.app;

import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.turingoal.bts.wps.follow.BuildConfig;
import com.turingoal.bts.wps.follow.bean.MyObjectBox;
import com.turingoal.common.android.base.TgBaseApplication;
import com.turingoal.common.android.constants.TgConstantPattern4DateTimeFormatter;
import com.turingoal.common.android.util.system.TgLogUtil;

import java.io.IOException;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 主应用
 */
public class TgApplication extends TgBaseApplication {
    private static TgUserPreferences userPreferences; // 用户数据存储
    private static BoxStore boxStore; // Application中初始化ObjectBox
    private static Retrofit retrofit; // Application中初始化retrofit

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext(); //context
        userPreferences = new TgUserPreferences(this, TgAppConfig.SP_NAME); // 初始化TgUserPreferences
        Utils.init(this); // 初始化Utils工具类
        initLogger(TgAppConfig.LOG_TAG, BuildConfig.DEBUG); // 初始化Logger
        initARouter(BuildConfig.DEBUG); // 初始化arouter
        initObjectBox(); // 初始化ObjectBox数据库
        initRetrofit(); // 初始化Retrofit
    }

    /**
     * 初始化ObjectBox
     */
    private void initObjectBox() {
        boxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(this); // 添加调试
            TgLogUtil.d("Using ObjectBox " + BoxStore.getVersion() + " (" + BoxStore.getVersionNative() + ")");
        }
    }

    /**
     * initRetrofit
     */
    public static void initRetrofit() {
        Gson gson = new GsonBuilder().setDateFormat(TgConstantPattern4DateTimeFormatter.YYYY_MM_DD_HH24_MM_SS).create();  //配置Gson
        String baserUrl = TgSystemHelper.getServerBaseUrl(); //  Retrofit2 的baseUlr 必须以 /（斜线） 结束
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                // 添加通用的Parameters
                HttpUrl url = original.url().newBuilder()
                        //.addQueryParameter("userId",getTgUserPreferences().getUserId())
                        .build();
                // 添加通用的headers
                Request.Builder requestBuilder = original.newBuilder() // 添加token
                        //.addHeader(TgConstantSystemValues.ACCESS_TOKEN, getTgUserPreferences().getToken()) // getToken 不能为null
                        .url(url);
                return chain.proceed(requestBuilder.build());
            }
        });
        if (BuildConfig.DEBUG) { // 日志拦截,请确保日志拦截器是添加到OkHttp客户端的最后一个拦截器
            httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        OkHttpClient build = httpClient.build();
        retrofit = new Retrofit.Builder().baseUrl(baserUrl).client(build).addConverterFactory(GsonConverterFactory.create(gson)).build();
    }

    /**
     * getRetrofit
     */
    public static Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * getBoxStore
     */
    public static BoxStore getBoxStore() {
        return boxStore;
    }

    /**
     * getTgUserPreferences
     */
    public static TgUserPreferences getTgUserPreferences() {
        return userPreferences;
    }
}
