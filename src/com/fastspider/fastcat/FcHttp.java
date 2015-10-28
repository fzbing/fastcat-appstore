package com.fastspider.fastcat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FcHttp {

    public static String get(String serverUrl) {
        String result = "";
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader bufReader = new BufferedReader(in);
            String readLine = null;
            while ((readLine = bufReader.readLine()) != null) {
                result += readLine;
            }
            in.close();
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
        }
        return result;
    }

}