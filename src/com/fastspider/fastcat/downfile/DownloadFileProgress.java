package com.fastspider.fastcat.downfile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class DownloadFileProgress implements DownProgress {

    public void downFile(String url, File file_env, String filename) {
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    onStart(length);

                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream;
                    if (is != null) {
                        File file = new File(file_env, filename);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        long count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            onUpdate(count);
                        }
                        fileOutputStream.flush();
//                        if (fileOutputStream != null) {
                        fileOutputStream.close();
//                        }
                        onEnd();
                    }

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onStart(long length) {

    }

    @Override
    public void onUpdate(long count) {

    }

    @Override
    public void onEnd() {

    }
}
