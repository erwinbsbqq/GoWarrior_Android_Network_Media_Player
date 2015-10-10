package com.gowarrior.nmp.localserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LocalServer extends Service {
    private static String TAG = "LocalServer";
    private static final String LOCALADDR = "/data/data/server";
    public class LocalServerImp extends ILocalServerService.Stub {
        /*@Override
        public byte[] DoGet(String file) {
            FileInputStream fin = null;
            byte[] res = null;
            Log.d(TAG, "getfilepath origin = " + LOCALADDR + file);
            file=file.substring(1);
            Log.d(TAG, "getfilepath = " + LOCALADDR + file);
            try {
                fin = openFileInput(file);
                Log.d(TAG, "fin = " + fin);
                int leng = fin.available();
                byte[] buffer = new byte[leng];
                fin.read(buffer);

                res = buffer;
                fin.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "fin0 = " + fin);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "fin1 = " + fin);
                e.printStackTrace();
            }
            Log.d(TAG, "data = " + res);
            return res;
        }*/

        @Override
        public byte[] DoGet(String file) {
            FileInputStream fin = null;
            File f = null;
            byte[] res = null;
            file = LOCALADDR + file;
            Log.d(TAG, "getfilepath = " + file);
            try {
                f = new File(file);
                fin = new FileInputStream(f);
                int leng = fin.available();
                byte[] buffer = new byte[leng];
                fin.read(buffer);

                res = buffer;
                fin.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        public byte[] DoPost(String data){
            Log.d(TAG, "postdata " + data);
            return ("postdataOK").getBytes();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new LocalServerImp();
    }
}
