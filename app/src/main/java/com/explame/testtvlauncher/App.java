package com.explame.testtvlauncher;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.explame.testtvlauncher.utils.CrashHandler;
import com.explame.testtvlauncher.utils.LogUtils;
import com.explame.testtvlauncher.utils.Utils;
import com.facebook.stetho.Stetho;

/**
 * Created by admin on 2018/5/2.
 */

public class App extends MultiDexApplication {

    private static Application mApp;
    public static LogUtils.Builder lBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Utils.init(mApp);
        lBuilder = new LogUtils.Builder()
                .setLogSwitch(true)// 设置log总开关，默认开
//                .setGlobalTag("haha")// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setLogFilter(LogUtils.V);// log过滤器，和logcat过滤器同理，默认Verbose

        //初始化Stetho
        Stetho.initializeWithDefaults(this);
        // 加载全部异常捕获
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    /**
     * 获取Application实例
     *
     * @return Application实例
     */
    public static Application getApplication() {
        return mApp;
    }


}
