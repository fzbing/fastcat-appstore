package com.fastspider.fastcat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Common {

    // TODO: 15-10-28 配置请求地址
    public static final String SERVER_ADDRESS = "http://10.1.0.16:9000/";

    public static JSONObject postServer(List<NameValuePair> vps) {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httpost = new HttpPost(SERVER_ADDRESS);
            httpost.setEntity(new UrlEncodedFormEntity(vps, HTTP.UTF_8));

            HttpResponse response = httpclient.execute(httpost);

            StringBuilder builder = new StringBuilder();
            if (response.getEntity() != null) {
                // 如果服务器端JSON没写对，这句是会出异常，是执行不过去的
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                    builder.append(s);
                }
            }
            try {
                return new JSONObject(builder.toString());
            } catch (JSONException e) {
                // TODO: 15-10-28
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();// 关闭连接  
                // todo 这两种释放连接的方法都可以
            } catch (Exception e) {
                Log.e("msg", e.getMessage());
            }
        }
        return new JSONObject();
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verCode;
    }
}  