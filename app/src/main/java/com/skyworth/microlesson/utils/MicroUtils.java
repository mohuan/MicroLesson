package com.skyworth.microlesson.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.skyworth.microlesson.app.AppContext;
import com.skyworth.microlesson.model.http.response.HttpResponseCode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import retrofit2.Response;
import retrofit2.adapter.rxjava2.HttpException;

public class MicroUtils {
    //获取设备唯一id
    public static String getUUID(){
        String uuid = "";
        SharedPreferences mShare = AppContext.getInstance().getSharedPreferences("uuid", Context.MODE_PRIVATE);
        if(mShare != null){
            uuid = mShare.getString("uuid", "");
        }
        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid",uuid).apply();
        }
        return uuid;
    }

    public static String slowHash(String s){

        for (int i=0;i<10000;i++){
            s = md5(s);
        }

        return s;
    }

    private static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static HttpResponseCode onError(HttpException e){
        // [text={"msg":"用户名密码错误","code":400}]
        Response response= e.response();
        try {
            String data = response.errorBody().source().toString();
            data = data.replace("[text=","");
            data = data.substring(0,data.length()-1);

            Gson gson = new Gson();
            HttpResponseCode httpResponseCode = gson.fromJson(data, HttpResponseCode.class);
            return httpResponseCode;
        }catch (NullPointerException ex){

        }
        return null;
    }
}
