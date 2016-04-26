package com.example.alan.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linjf on 2016/4/13 0013.
 */
public class MyApplication extends Application {
    static List<Activity> activityList=new ArrayList<Activity>();
//    static MyApplication instance=null;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler=new CrashHandler();
        crashHandler.init(getApplicationContext());
        // 内存泄漏 测试
        LeakCanary.install(this);
//        instance=this;
    }

//    static MyApplication getInstance(){
//        return instance;
//    }

    public static List<Activity> getActivityList() {
        return activityList;
    }

    public static void setActivityList(List<Activity> activityList) {
        MyApplication.activityList = activityList;
    }

    public class CrashHandler implements Thread.UncaughtExceptionHandler{

        private Thread.UncaughtExceptionHandler mDefaultHandler;

        /**
         * 初始化
         *
         * @param context
         */
        public void init(Context context) {
            Context mContext = context;
            //获取系统默认的UncaughtException处理器
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            //设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.i("CrashHandler",thread.toString()+"\n"+ex);
            ex.printStackTrace();
            mDefaultHandler.uncaughtException(thread,ex);
        }
    }
}
