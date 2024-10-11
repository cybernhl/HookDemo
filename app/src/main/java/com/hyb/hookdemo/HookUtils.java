package com.hyb.hookdemo;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import timber.log.Timber;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.ContextWrapper;


import okhttp3.internal.http.RealInterceptorChain;

/**
 * @author Mr.HuaYunBin
 */
public class HookUtils implements IXposedHookLoadPackage , IXposedHookZygoteInit {
    private static final String TAG = "HookUtils";
    private static final boolean ENABLE_LOG_HOOKER = true;
    private static final byte[] lock = new byte[1];
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private boolean classInDexFound = false;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (ENABLE_LOG_HOOKER) {//FIXME adven hookXposedFrameworkApi
            hookXposedFrameworkApi(startupParam);
        }
    }
    /**
     * 用于分析第三方 XP 模块采用了哪些钩子，主要用于模块包体比较大，
     * 做了加固或者混淆做得比较彻底的场景
     * <p>
     * 钩子的目标类和方法名，可在 Xposed Installer 的日志中查看
     */
    private void hookXposedFrameworkApi(StartupParam param) {
//        hookXpFindAndHookMethod();
//        hookXpFindAndHookConstructor();
//        hookXpHookAllMethods();
//        hookXpHookAllConstructors();
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Timber.plant(new Timber.DebugTree());//FIXME init for Hook module
        String target_hook_applicationId = "com.hyb.hookdemo.app";
        if (target_hook_applicationId.equals(lpparam.packageName)) {
            Timber.e("Show me !!!!已成功Hook到HookDemo应用");
            XposedBridge.log("已成功Hook到HookDemo应用");
            Class clazz = lpparam.classLoader.loadClass("com.hyb.hookdemo.MainActivity");
            XposedHelpers.findAndHookMethod(clazz, "toastMsg", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.e(TAG, "Show me beforeHookedMethod"  );
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("你被劫持啦");
                    Log.e(TAG, "Show me afterHookedMethod"  );
                }
            });
            //hookOriginNewCall(lpparam);
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                    Class<?> aClass = cl.loadClass("okhttp3.OkHttpClient");
                    Class<?> requestClass = cl.loadClass("okhttp3.Request");
                    findAndHookMethod(aClass, "newCall", requestClass, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            try {
                                //okhttp3.Request cannot be cast to okhttp3.Request
                                String str = toJson(param.args[0]);
                                Log.i(TAG, "request json string " + str);
                                //Request request = gson.fromJson(str, Request.class);
                                //set result 修改请求参数
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Log.i(TAG, "OkHttpClient afterHookedMethod package name = " + lpparam.packageName);
                        }
                    });
                }
            });
        }
    }


    private void hookOriginNewCall(final LoadPackageParam lp) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                Class<?> aClass = cl.loadClass("okhttp3.OkHttpClient");
                Class<?> requestClass = cl.loadClass("okhttp3.Request");
                findAndHookMethod(aClass, "newCall", requestClass, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        try {
                            //okhttp3.Request cannot be cast to okhttp3.Request
                            String str = toJson(param.args[0]);
                            Log.i(TAG, "request json string " + str);
                            //Request request = gson.fromJson(str, Request.class);
                            //set result 修改请求参数
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i(TAG, "OkHttpClient afterHookedMethod package name = " + lp.packageName);
                    }
                });
            }
        });
    }


    private void hookGetPackageManager(XC_LoadPackage.LoadPackageParam lpparam) {
        String target_hook_applicationId = "com.hyb.hookdemo.app";
        if (target_hook_applicationId.equals(lpparam.packageName)) {
            Timber.e("Show me !!!!已成功Hook到HookDemo应用");
            XposedBridge.log("已成功Hook到HookDemo应用");
            //FIXME finish hook all run at memory !! so  not Logcat !!
//            Class clazz = lpparam.classLoader.loadClass("com.hyb.hookdemo.MainActivity");//FIXME if use "lpparam.classLoader" need "throws Throwable"
//            XposedHelpers.findAndHookMethod(clazz, "toastMsg", new XC_MethodHook() {
            XposedHelpers.findAndHookMethod("com.hyb.hookdemo.MainActivity", lpparam.classLoader, "toastMsg", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.e(TAG,"Show me Yooo");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e(TAG,"Show me YoooKKK");
                    param.setResult("你被劫持啦");
                }
            });
        }else {
            return;
        }
    }


    private void hookXpFindAndHookMethod() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedHelpers",
                    HookUtils.class.getClassLoader());
            XposedHelpers.findAndHookMethod(cls, "findAndHookMethod",
                    Class.class, String.class, Object[].class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("findAndHookMethod: cls = " + param.args[0]
                                    + ", method = " + param.args[1]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private void hookXpFindAndHookConstructor() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedHelpers",
                    HookUtils.class.getClassLoader());
            XposedHelpers.findAndHookMethod(cls, "findAndHookConstructor",
                    Class.class, Object[].class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("findAndHookConstructor: cls = " + param.args[0]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private void hookXpHookAllMethods() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedBridge",
                    HookUtils.class.getClassLoader());
            Class clsXC_MethodHook = XposedHelpers.findClass(
                    "de.robv.android.xposed.XC_MethodHook", HookUtils.class.getClassLoader()
            );
            XposedHelpers.findAndHookMethod(cls, "hookAllMethods",
                    Class.class, String.class, clsXC_MethodHook, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("hookAllMethods: cls = " + param.args[0]
                                    + ", method = " + param.args[1]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private void hookXpHookAllConstructors() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedBridge",
                    HookUtils.class.getClassLoader());
            Class clsXC_MethodHook = XposedHelpers.findClass(
                    "de.robv.android.xposed.XC_MethodHook", HookUtils.class.getClassLoader()
            );
            XposedHelpers.findAndHookMethod(cls, "hookAllConstructors",
                    Class.class, clsXC_MethodHook, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("hookAllConstructors: cls = " + param.args[0]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private final OnFindDexListener findDexListener = new OnFindDexListener() {
        @Override
        public void onFind(ClassLoader classLoader) {
            if (classInDexFound) {
                Log.i(TAG, "found in hide dex");
                return;
            }

            realHookLastInterceptor(classLoader);
        }
    };

    //java.lang.ClassNotFoundException: Didn't find class "okhttp3.Interceptor.Chain" on path: DexPathList
    private void hookLastInterceptor(final LoadPackageParam lp) {
        Log.i(TAG, "start hookLastInterceptor:" + lp.packageName);
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final ClassLoader cl = ((Context) param.args[0]).getClassLoader();

                try {
                    cl.loadClass("okhttp3.internal.http.CallServerInterceptor");
                    cl.loadClass("okhttp3.Interceptor.Chain");
                    classInDexFound = true;
                    realHookLastInterceptor(cl);
                } catch (Throwable e) {
                    if (e instanceof ClassNotFoundException) {
                        findHideDex(findDexListener);
                    } else {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    private void realHookLastInterceptor(final ClassLoader cl) {
        try {
            Class<?> aClass = cl.loadClass("okhttp3.internal.http.CallServerInterceptor");
            Class<?> chainClass = cl.loadClass("okhttp3.Interceptor.Chain");
            findAndHookMethod(aClass, "intercept", chainClass, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    classInDexFound = true;
                    try {
                        //RealInterceptorChain realChain = (RealInterceptorChain) chain;
                        String str = gson.toJson(param.args[0]);
                        Log.i(TAG, "RealInterceptorChain json string " + str);
                        RealInterceptorChain realChain = gson.fromJson(str, RealInterceptorChain.class);
                        if (realChain != null) {
                            String requestStr = toJson(realChain.request());
                            Log.i(TAG, "RealInterceptorChain request json string " + requestStr);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Throwable e) {
            //ignore
        }
    }

    /**
     * 查找其他dex中class
     */
    private void findHideDex(final OnFindDexListener listener) {
        XposedBridge.hookAllMethods(ContextWrapper.class, "attachBaseContext", new XC_MethodHook() {

            public void beforeHookedMethod(MethodHookParam param) {
                ClassLoader classLoader = ((Context) param.args[0]).getClassLoader();
                if (classLoader == null) return;
                if (listener != null) listener.onFind(classLoader);
            }
        });
        XposedBridge.hookAllConstructors(ClassLoader.class, new XC_MethodHook() {
            public void beforeHookedMethod(MethodHookParam param) {
                ClassLoader classLoader = (ClassLoader) param.args[0];
                if (classLoader == null) return;
                if (listener != null) listener.onFind(classLoader);
            }
        });
    }


    private String toJson(Object object) {
        synchronized (lock) {
            return gson.toJson(object);
        }
    }

    private <T> T fromJson(String string, Class<T> classOfT) {
        synchronized (lock) {
            return gson.fromJson(string, classOfT);
        }
    }

    public interface OnFindDexListener {
        void onFind(ClassLoader classLoader);
    }
}
