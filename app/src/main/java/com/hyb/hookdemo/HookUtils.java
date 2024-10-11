package com.hyb.hookdemo;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.pm.PackageManager;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import timber.log.Timber;

/**
 * @author Mr.HuaYunBin
 */
public class HookUtils implements IXposedHookLoadPackage , IXposedHookZygoteInit {
    private static final boolean ENABLE_LOG_HOOKER = true;

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
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("你被劫持啦");
                }
            });
        }
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

                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

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
}
