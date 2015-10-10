package com.gowarrior.nmp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String TAG = "LocalClient";
    private Handler handler = null;
    private ClientTask clientTask = null;
    private ExecutorService executorService = null;

    private static final int UPDATE_UI = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newFixedThreadPool(5);
        handler = new UIHandler();
        clientTask = new ClientTask();
        try {
            URL url = new URL("http://127.0.0.1:3932/jm.html");
            clientTask.executeOnExecutor(executorService, url);
            setContentView(R.layout.activity_main);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Stop Client");
        super.onDestroy();
    }

    private class ClientTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {
            InputStream in = null;
            String str = null;

            try {
                URLConnection connection = params[0].openConnection();
                in = new BufferedInputStream(connection.getInputStream());
                Log.d(TAG, "GetResponse");
                str = readStream(in);
                Log.d(TAG, "str = "+str);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }finally {
                assert in != null;
                try {
                    Log.d(TAG, "close");
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result){
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", result);
            msg.what = UPDATE_UI;
            msg.setData(bundle);
            msg.sendToTarget();
            Log.d(TAG, "send data done");
        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            int num = 0;
            while(i != -1) {
                Log.d(TAG, "num[" + num + "]=" + Integer.toHexString(i));
                ++num;
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public class UIHandler extends Handler {
        @Override
        public void handleMessage (Message msg) {
            switch(msg.what) {
                case UPDATE_UI:
                    Log.d(TAG, "What ="+msg.what);
                    Bundle b = msg.getData();
                    String retdata = b.getString("response");
                    TextView tv = (TextView) findViewById(R.id.test);
                    Log.d(TAG, "tv ="+tv);
                    tv.setText(retdata);
                    Log.d(TAG, "retdata ="+retdata);
                    break;
                default:
                    Log.d(TAG, "What ="+msg.what);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
