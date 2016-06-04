package com.example.administrator.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/4/17.
 */
public class MainActivity_Second extends BaseActivity {

    private String courses[] = {
            "软件工程实验",
            "编译原理",
            "云计算概论",
            "通信原理",
            "软件工程导论",
            "Android应用设计与开发"
    };
    private static Context context;
    private static LinearLayout day[] = new LinearLayout[7];
    private static OnClickClassListener OnListener = new OnClickClassListener();

    private Button exchangeTable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table);

        context = this;

        exchangeTable = (Button) findViewById(R.id.exchange_table);
        exchangeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_Second.this, AddCourseTableActivity.class);
                startActivity(intent);
            }
        });

        connectTo();

    }

    public void connectTo() {
        day[1] = (LinearLayout) findViewById(R.id.monday);
        day[2] = (LinearLayout) findViewById(R.id.tuesday);
        day[3] = (LinearLayout) findViewById(R.id.wednesday);
        day[4] = (LinearLayout) findViewById(R.id.thursday);
        day[5] = (LinearLayout) findViewById(R.id.friday);
        day[6] = (LinearLayout) findViewById(R.id.saturday);
        day[0] = (LinearLayout) findViewById(R.id.sunday);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Message message = new Message();
                try {
                    String str = "kind=buildClass&number=" + courses.length;
                    Integer i_count = 0;
                    for(String s:courses){
                        str = str + "&" + i_count.toString() + "=" + s;
                        i_count++;
                    }
                    URL url = new URL("http://119.29.148.205:8080/hwServer/main");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.getOutputStream().write(str.getBytes("utf8"));
                    connection.getOutputStream().flush();
                    connection.getOutputStream().close();
                    InputStream input = connection.getInputStream();

                    BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null){
                        buffer.append(line);
                    }
                    String sdata = buffer.toString();
                    JSONObject json = new JSONObject(sdata);
                    message.what = json.getInt("state");
                    message.obj = json;
                    handler.sendMessage(message);
                } catch (ConnectException e) {
                    message.what = 9;
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    MyHandler handler = new MyHandler(MainActivity_Second.this);
    static class MyHandler extends Handler {
        WeakReference<MainActivity_Second> MainActivity_SecondWeakReference;
        JSONArray jsonArray;
        ClassActivity classTable;
        public MyHandler(MainActivity_Second Activity) {
            MainActivity_SecondWeakReference = new WeakReference<>(Activity);
            classTable = new ClassActivity();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0: // 显示课表
                        JSONObject json_all = (JSONObject) msg.obj;
                        jsonArray = json_all.getJSONArray("classData");
                        ArrayList<ArrayList<Integer>> int_class = new ArrayList<>();
                        for(Integer i = 0; i < 7; i++)
                            int_class.add(new ArrayList<Integer>());
                        // 分组
                        for(Integer i = 0; i < jsonArray.length(); i++){
                            int_class.get(jsonArray.getJSONObject(i).getInt("cday")).add(i);
                        }
                        // 排序
                        for(Integer i = 0; i < 7; i++){
                            Collections.sort(int_class.get(i), new myCompare());
                        }
                        // 构建课表
                        Integer point_color = 1; // 1-7的颜色遍历
                        for(Integer i = 0; i < 7; i++){ // 星期
                            Integer point_class = 1; // 1-15节课的遍历
                            Integer point_intClass = 0; // 数组int_class.get(i)的遍历
                            if(int_class.get(i).isEmpty()) classTable.setNoClass(day[i],15, 0, MainActivity_SecondWeakReference.get());
                            while(point_intClass < int_class.get(i).size()) {
                                // 当前课程的JSON
                                JSONObject point_json = jsonArray.getJSONObject(int_class.get(i).get(point_intClass));
                                if(point_json.getInt("cc_begin") > point_class) {
                                    classTable.setNoClass(day[i],point_json.getInt("cc_begin") - point_class,0, MainActivity_SecondWeakReference.get());
                                }
                                classTable.setClass(day[i],point_json.getString("cname"), point_json.getString("cplace"),
                                        point_json.getString("clast"), point_json.getString("ctime"),
                                        point_json.getInt("cc_end") - point_json.getInt("cc_begin") + 1, point_color,
                                        MainActivity_SecondWeakReference.get(), OnListener);
                                if(point_color == 7) point_color = 1;
                                else point_color++;
                                point_class = point_json.getInt("cc_end") + 1;
                                if(point_intClass == int_class.get(i).size() - 1 && point_class <= 15){
                                    classTable.setNoClass(day[i],15 - point_class + 1,0, MainActivity_SecondWeakReference.get());
                                }
                                point_intClass++;
                            }
                        }
                        break;
                    case 3: // 数据库操作失败
                        Toast.makeText(MainActivity_SecondWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 9: // 网络连接错误
                        Toast.makeText(MainActivity_SecondWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity_SecondWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        class myCompare implements Comparator{
            @Override
            public int compare(Object lhs, Object rhs) {
                Integer int1 = (Integer) lhs;
                Integer int2 = (Integer) rhs;
                int result = -1;
                try {
                    if (jsonArray.getJSONObject(int1).getInt("cc_begin") > jsonArray.getJSONObject(int2).getInt("cc_begin"))
                        result = 1;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return result;
            }
        }
    }

    static class OnClickClassListener implements View.OnClickListener {

        public void onClick(View v) {
            String title;
            title = (String) ((TextView)v.findViewById(R.id.item_title)).getText();
            //Toast.makeText(context, "你点击的是:" + title, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, WeeklyHomeworks.class);
            intent.putExtra("cname", title);
            context.startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*finish();
        Intent intent = new Intent(MainActivity_Third.this, MainActivity_Third.class);
        startActivity(intent);*/
        connectTo();
    }
}