package com.mzx.momo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity {
    private String processout = "log\n";
    private TextView textView;
    private Handler myHandler;
    private String shareurl;
    private int succcessnum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button startButton = (Button) findViewById(R.id.start_attendence);
        textView = (TextView) findViewById(R.id.process_out);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        final EditText tv1 = (EditText) findViewById(R.id.url);
        myHandler = new MyHandler(Main2Activity.this.getMainLooper());
        startButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareurl = tv1.getText().toString();
                        MyThread thread = new MyThread();
                        new Thread(thread).start();
                    }
                }
        );


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


    class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String message = b.get("msg").toString();
            processout += message;
            textView.setText(processout);

            int offset = textView.getLineCount() * textView.getLineHeight();
            if (offset > textView.getHeight()) {
                textView.scrollTo(0, offset - textView.getHeight());
            }
        }


    }

    class MyThread implements Runnable {

        @Override
        public void run() {
            String proxyurl = "http://www.66ip.cn/mo.php?tqsl=500";
            String res = get(proxyurl);



            String regEx = "([0-9].+)<br />";
            // 编译正则表达式
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(res);
            // 查找字符串中是否有匹配正则表达式的字符/字符串
            while (matcher.find()) {

                String proxy = matcher.group().replace("<br />", "");
                String host = proxy.split(":")[0];
                int port = Integer.valueOf(proxy.split(":")[1]);
                String result = aysnget(shareurl, host, port);
            }
            try {
                Thread.sleep(1000 * 20);
                send("\n成功访问了" + succcessnum + "次，如果数量不足40请重新点击\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String get(String url, String host, int port) {
        OkHttpClient client;
        client = new OkHttpClient().newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(5, TimeUnit.SECONDS)//设置读取超时时间
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port))).build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                return response.body().string();
            } else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String get(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                return response.body().string();
            } else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String aysnget(String url, final String host, int port) {
        OkHttpClient client;
        client = new OkHttpClient().newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(5, TimeUnit.SECONDS)//设置读取超时时间
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port))).build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                send("fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                send("\nsuccess proxy is " + host + "\n");
                succcessnum += 1;
            }
        });

        return "";
    }


    public void send(String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        Message message = new Message();
        message.setData(bundle);
        myHandler.sendMessage(message);
    }
}


