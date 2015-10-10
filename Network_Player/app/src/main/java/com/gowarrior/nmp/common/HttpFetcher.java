package com.gowarrior.nmp.common;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by GoWarrior on 2015/7/2.
 */
public class HttpFetcher {
    private static final String TAG ="HttpFetcher";
    private URL url = null;
    private String filename = null;
    public HttpFetcher(){

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (HttpFetcher.class) {
                try {
                    URLConnection connection = url.openConnection();
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    Log.d(TAG, "in=" + in);
                    save(filename, in);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    HttpFetcher.class.notify();
                }
            }
        }
    };
    public void get(String data, String dest){
        try {
            url = new URL(data);
            Log.d(TAG, "url=" + data);
            data = data.substring(data.lastIndexOf('/'));
            Log.d(TAG, "save file name=" + dest + data);
            filename = dest + data;
            new Thread(runnable).start();
            synchronized (HttpFetcher.class){
                HttpFetcher.class.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "quit get" );
    }

    private void save(String path, InputStream in){
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            int i = in.read();
            while (i != -1) {
                fos.write(i);
                i = in.read();
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
