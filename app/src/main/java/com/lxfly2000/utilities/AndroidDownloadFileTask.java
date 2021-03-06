package com.lxfly2000.utilities;

import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class AndroidDownloadFileTask extends AsyncTask<String,Integer,Boolean> {
    private ByteArrayInputStream inStream;
    private Object extraObject;
    private String userAgent=null;

    @Override
    protected Boolean doInBackground(String... params) {
        return DownloadFileToStream(params[0]);
    }

    private boolean DownloadFileToStream(String url){
        //http://blog.csdn.net/pgmsoul/article/details/7181793
        URL jUrl;
        try{
            jUrl=new URL(url);
        }catch (MalformedURLException e){
            return false;
        }
        URLConnection connection;
        InputStream ins;
        try{
            connection=jUrl.openConnection();
            if(userAgent!=null)
                connection.setRequestProperty("User-Agent",userAgent);
            ins=connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = ins.read(buffer)) > -1 ) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            // 打开一个新的输入流
            inStream=new ByteArrayInputStream(outputStream.toByteArray());
            ins.close();
        }catch (IOException e){
            //安卓P中默认设置不允许明文HTTP传输，否则会报错
            //解决办法1：将http改为https协议，2：在清单文件中添加android:usesCleartextTraffic="true"
            if(url.startsWith("http:"))//这里在子线程中调用Toast会报告应用程序事件顺序不对，因此无法使用Toast。
                return DownloadFileToStream(url.replaceFirst("http:","https:"));
            return false;
        }
        return true;
    }
    @Override
    protected void onPostExecute(Boolean result){
        OnReturnStream(inStream,result,extraObject);
    }

    public void SetExtra(Object extra){
        extraObject=extra;
    }

    public void SetUserAgent(String ua){
        userAgent=ua;
    }

    public abstract void OnReturnStream(ByteArrayInputStream stream, boolean success, Object extra);
}