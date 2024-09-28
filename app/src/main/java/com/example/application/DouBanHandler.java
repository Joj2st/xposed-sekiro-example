package com.example.application;

import android.util.Log;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.Action;
import cn.iinti.sekiro3.business.api.interfaze.AutoBind;
import cn.iinti.sekiro3.business.api.interfaze.RequestHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;
import de.robv.android.xposed.XposedHelpers;

@Action("douban")
public class DouBanHandler implements RequestHandler {
    @AutoBind
    private String arg1;
    private final ClassLoader MyClassLoader;

    public DouBanHandler(ClassLoader classLoader) {
        MyClassLoader = classLoader;
    }

    public static final String HMACKey = "bf7dddc7c9cfe6f7";

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        // 接受参数
        String args1 = sekiroRequest.getString("word");
        Log.i("mqhook", "Request param: " + sekiroRequest.getParamContent());
        // 主动调用
        Class<?> HMACHash1 = XposedHelpers.findClass("com.douban.frodo.utils.crypto.HMACHash1", this.MyClassLoader);
        Object result = XposedHelpers.callStaticMethod(HMACHash1, "a", HMACKey, args1);
        Log.i("mqhook", "HMACHash1: " + result);
        // 返回结果
        JSONObject jsonObject = sekiroRequest.getJsonModel();
        jsonObject.put("result", result);
        Log.i("mqhook", "response: " + jsonObject.toJSONString());
        sekiroResponse.success(jsonObject);
    }
}