package com.hyb.hookdemo;

import static com.hyb.hookdemo.OkCompat.Cls_OkHttpClient$Builder;
import static com.hyb.hookdemo.OkCompat.F_Builder_interceptors;
import static com.hyb.hookdemo.OkCompat.M_Builder_build;

import android.util.Log;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import okhttp3.logging.HttpLoggingInterceptor;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
//Ref : https://blog.csdn.net/moziqi123/article/details/109204801
//Ref : https://github.com/siyujie/OkHttpLoggerInterceptor-hook/tree/master
//Ref : https://github.com/Harlber/HookOkHttp
//Ref : https://github.com/Harlber/HookOkHttp/blob/master/app/src/main/java/just/hook/ok/OkHttpHook.java
//Ref : https://github.com/Harlber/HookOkHttp/blob/master/app/src/main/java/just/hook/ok/OkCompat.java
//Ref : https://blog.51cto.com/u_16993871/11908417

/**
 * createTime: 2020/4/16.13:56
 * updateTime: 2020/4/16.13:56
 * author: singleMan.
 * desc:
 */
public class OkHttpHooker {

    public static final String TAG = "OkHttp";


    /**
     *
     * @param classLoader
     */
    public static void attach(final ClassLoader classLoader){

        Class OkHttpClient_BuilderClass = null;
        try {
            OkHttpClient_BuilderClass = Class.forName(Cls_OkHttpClient$Builder, true, classLoader);
            if(null == OkHttpClient_BuilderClass){
                return;
            }

            findAndHookMethod(OkHttpClient_BuilderClass, M_Builder_build, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    List interceptors = (List) getObjectField(param.thisObject, F_Builder_interceptors);
                    //添加自己的过滤器
                    interceptors.add(new MyInterceptor.Builder()
                            .addHeader("customHeaderKey","customHeaderValue")
                            .addQueryParam("customQueryKey","customQueryValue")
                            .build(classLoader));

                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG,"没有发现OkHttp相关，可能未使用 or 被混淆");
        }
    }
}
