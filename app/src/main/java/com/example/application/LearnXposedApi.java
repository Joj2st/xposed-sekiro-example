package com.example.application;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class LearnXposedApi implements IXposedHookLoadPackage {
    private static final String TAG = "mqhook";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //
        Class<?> clazz = XposedHelpers.findClass("com.example.application.Demo", lpparam.classLoader);
        // hook 静态字段
        XposedHelpers.setStaticIntField(clazz, "add", 12);
        // hook 静态字段string
        XposedHelpers.setObjectField(clazz, "tag", "hello");
        // hook class的构造方法
        XposedHelpers.findAndHookConstructor(clazz, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookConstructor(clazz, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        // hook 静态 私有 公共 保护 方法
        XposedHelpers.findAndHookMethod(clazz, "getSign", String.class, Integer.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        // hook 复杂参数
        XposedHelpers.findAndHookMethod(clazz, "getWha", String.class, String[][].class, Class.forName("java.util.HashMap"), HashMap.class, ArrayList.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        // hook 自定义类
        Class<?> DogClass = lpparam.classLoader.loadClass("com.example.application.Dog");
        Class<?> CatClass = XposedHelpers.findClass("com.example.application.Cat", lpparam.classLoader);
        Class<?> AnyClass = Class.forName("com.example.application.Any", false, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "inner", DogClass, CatClass, AnyClass, Integer.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        // 替换函数
        XposedHelpers.findAndHookMethod(clazz, "getSign", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return null;
            }
        });

        // hook 内部类
        Class<?> innerClass = XposedHelpers.findClass("com.example.application.Demo$InnerClass", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(innerClass, "innerFunc", String.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

        // 主动调用
        Class<?> DemoClass = XposedHelpers.findClass("com.example.application.Demo", lpparam.classLoader);
        Object result = XposedHelpers.callMethod(DemoClass, "getSign", String.class);
        Log.i(TAG, "result: " + result);

        // 反射调用
        Class<?> fClass = Class.forName("com.example.application.Demo",false,lpparam.classLoader);
        // 获取公有 保护 方法
//        fClass.getMethod();
        // 获取所有 方法 私有方法要设置访问权限 setA
        Method ref = fClass.getDeclaredMethod("ref");
        Object result2 = ref.invoke(fClass.newInstance());
        // 遍历所有方法和字段
        Class<?> cl = XposedHelpers.findClass("",lpparam.classLoader);
        Log.i(TAG, "handleLoadPackage: " + cl.getName());

        Method[] mds = cl.getDeclaredMethods();
        for (Method md : mds) {
            Log.i(TAG, "handleLoadPackage: " + md.getName());
        }
        Field[] fds = cl.getDeclaredFields();
        for (Field fd : fds) {
            Log.i(TAG, "handleLoadPackage: " + fd.toString());
        }
        // 所有内部类
        Object[] icls = cl.getDeclaredClasses();
        for (Object icl : icls) {
            Log.i(TAG, "handleLoadPackage: " + icl.toString());
        }
        // hook 所有类
        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> clazz = (Class) param.getResult();
                Log.i(TAG, "loadClass: " + clazz.getName());
            }
        });
    }
}
