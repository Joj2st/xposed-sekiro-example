package com.example.application;


import android.os.Build;
import android.util.Log;

import cn.iinti.sekiro3.business.api.SekiroClient;
import cn.iinti.sekiro3.business.api.interfaze.HandlerRegistry;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequestInitializer;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

// 豆瓣7.18

public class DouBanHook implements IXposedHookLoadPackage {
    private static final String TAG = "mmhook";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.i(TAG, "=====================Hook starting======================== ");
        if (!lpparam.packageName.equals("com.douban.frodo")) return;

        if (lpparam.packageName.equals(lpparam.processName)) {
            Log.i(TAG, "=====================Sekiro starting===================== ");
            new Thread("start") {public void run() {startSekiro(lpparam);}}.start();
        }

        // hook 方法是不是存在
        XposedHelpers.findAndHookMethod("com.douban.frodo.utils.crypto.HMACHash1",
                lpparam.classLoader, "a", String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws
                            Throwable {
                        super.beforeHookedMethod(param);
                        /* param.args 为方法的参数列表 */
                        Log.i(TAG, ("Hook 豆瓣 str1: " + param.args[0]));
                        Log.i(TAG, ("Hook 豆瓣 str2: " + param.args[1]));
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws
                            Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "Hook 豆瓣: result: " + param.getResult());
                    }
                });
    }

    // 开启 Sekiro 服务
    private static void startSekiro(XC_LoadPackage.LoadPackageParam lpparam) {
        Log.i(TAG, "======================start Sekiro========================");
        // 获取设备的唯一标识 CLIENTID
        final String CLIENTID = Build.BRAND + "_" + Build.MODEL;
        Log.i(TAG, "startSekiro: " + CLIENTID);
        // 创建SekiroClient对象 - 倒数第二个参数写电脑本机ip，用于该插件在手机安装后连接上电脑端开启的服务
        SekiroClient sekiroClient = new SekiroClient("test_xposed002", CLIENTID);
        // 调用setupSekiroRequestInitializer方法 初始化
        sekiroClient.setupSekiroRequestInitializer(new SekiroRequestInitializer() {
            @Override
            public void onSekiroRequest(SekiroRequest sekiroRequest, HandlerRegistry handlerRegistry) {
                // 将上面开发好的Handler注册到client上
                Log.i(TAG, "startSekiro: 处理逻辑");
                handlerRegistry.registerSekiroHandler(new DouBanHandler(lpparam.classLoader));
            }
        });
        // 启动创建的sekiro客户端
        sekiroClient.start();
    }

}
//https://sekiro.iinti.cn:5612/business/clientQueue?group=test_xposed002
//  https://sekiro.iinti.cn:5612/business/invoke?group=test_xposed002&action=testHandler
//  https://sekiro.iinti.cn:5612/business/invoke?group=test_xposed002&action=douban&key1=1&key2=2