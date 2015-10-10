package com.gowarrior.nmp.localserver;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer extends Service {
    private static final String TAG = "HttpServer";
    private ILocalServerService mLocalServiceService = null;
    private static int port = 3932;
    private static final String ALL_PATTERN = "*";
    private static final String PNG_PATTREN = "*.png";
    private static final String XML_PATTREN = "*.xml";
    private BasicHttpProcessor httpProcessor = null;
    private BasicHttpContext httpContext = null;
    private HttpParams params= null;
    private HttpService httpService = null;
    private HttpRequestHandlerRegistry registry = null;
    private ServerSocket socket = null;
    private Handler handler = null;
    public HttpServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "Start Create Server!");

        ServiceConnection mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Service start connected!");
                mLocalServiceService = ILocalServerService.Stub.asInterface(service);
                Log.d(TAG, "Service connected! mLocalServiceService="+mLocalServiceService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLocalServiceService = null;
                Log.d(TAG, "Service Disconnected");
            }
        };
        Intent intent = new Intent(ILocalServerService.class.getName());
        boolean ret = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        httpProcessor = new BasicHttpProcessor();
        httpProcessor.addInterceptor(new ResponseDate());
        httpProcessor.addInterceptor(new ResponseServer());
        httpProcessor.addInterceptor(new ResponseContent());
        httpProcessor.addInterceptor(new ResponseConnControl());

        httpContext = new BasicHttpContext();

        params = new BasicHttpParams();
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        params.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024);
        params.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
        params.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
        params.setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

        registry = new HttpRequestHandlerRegistry();
        registry.register(ALL_PATTERN, new ServerHandlerLocal());

        httpService = new HttpService(httpProcessor, new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());
        httpService.setParams(params);
        httpService.setHandlerResolver(registry);

        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.post(runable);
    }

    private Runnable runable = new Runnable() {
        @Override
        public void run() {
            try{
                socket = new ServerSocket(port);
                DefaultHttpServerConnection conn = null;
                Log.d(TAG, "Listen port: " + port);
                Log.d(TAG, "Thread interrupted = " + Thread.interrupted());
                Log.d(TAG, "socket = " +socket);
                while (!Thread.interrupted()) {
                    try {
                        Socket s = socket.accept();
                        Log.d(TAG, "incomming socket = " + s);
                        conn = new DefaultHttpServerConnection();
                        Log.d(TAG, "Incoming address= " + s.getInetAddress());
                        conn.bind(s, params);
                        if (conn.isOpen()) {
                            try {
                                Log.d(TAG, "Ready to handle message");
                                httpService.handleRequest(conn, httpContext);
                            }catch (HttpException e) {
                                Log.d(TAG, "IO error:" + e.getMessage());
                            }
                        }
                    } catch(IOException e) {
                        Log.d(TAG, "IO error:" + e.getMessage());
                    } finally {
                        try{
                            conn.shutdown();
                            Log.d(TAG,"Shut Down connect");
                        } catch (IOException e) {
                            Log.d(TAG, "IO error:" + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "IO error:" + e.getMessage());
            }
        }
    };

    public class ServerHandlerAIDL implements HttpRequestHandler {

        @Override
        public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
            String method = httpRequest.getRequestLine().getMethod().toUpperCase();

            byte[] ret = null;
            String target = httpRequest.getRequestLine().getUri();
            Log.d(TAG,  "ServerHandle handle message:method=" + method + ", target=" + target);
            if (method.equals("GET")) {
                try {
                    ret = mLocalServiceService.DoGet(target);
                    Log.d(TAG,  "ServerHandle get return msg="+ret);
                    httpResponse.setStatusCode(HttpStatus.SC_OK);
                    HttpEntity entity = new ByteArrayEntity(ret);
                    httpResponse.setEntity(entity);
                    Log.d(TAG,  "ServerHandle send to Client");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    httpResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }
            } else if (method.equals("POST")) {
                try {
                    ret = mLocalServiceService.DoPost(target);
                    httpResponse.setStatusCode(HttpStatus.SC_OK);
                    HttpEntity entity = new ByteArrayEntity(ret);
                    httpResponse.setEntity(entity);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    httpResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                }
            } else {
                httpResponse.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    public class ServerHandlerLocal implements HttpRequestHandler {
        private static final String LOCALADDR = "/data/data/server";
        private byte[] DoGet(String file) {
            FileInputStream fin = null;
            File f = null;
            byte[] res = null;
            file = LOCALADDR + file;
            Log.d(TAG, "ServerHandler DoGet = " + file);
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
        private byte[] DoPost(String file){
            Log.d(TAG, "ServerHandler DoPost" + file);
            return ("postdataOK").getBytes();
        }
        @Override
        public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
            String method = httpRequest.getRequestLine().getMethod().toUpperCase();

            byte[] ret = null;
            String target = httpRequest.getRequestLine().getUri();
            Log.d(TAG,  "ServerHandle handle message:method=" + method + ", target=" + target);
            if (method.equals("GET")) {
                ret = DoGet(target);
                Log.d(TAG,  "ServerHandle get return msg="+ret);
                httpResponse.setStatusCode(HttpStatus.SC_OK);
                HttpEntity entity = new ByteArrayEntity(ret);
                httpResponse.setEntity(entity);
                Log.d(TAG,  "ServerHandle send to Client");
            } else if (method.equals("POST")) {
                ret = DoPost(target);
                httpResponse.setStatusCode(HttpStatus.SC_OK);
                HttpEntity entity = new ByteArrayEntity(ret);
                httpResponse.setEntity(entity);
            } else {
                httpResponse.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
            }
        }
    }
    @Override
    public void onDestroy(){
        Log.d(TAG, "Start Destroy Server!");
        handler.removeCallbacks(runable);
        super.onDestroy();

    }
}
