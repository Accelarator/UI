package com.example.administrator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
import java.util.Date;
import java.util.List;

public class MainActivity_First extends BaseActivity {

    private static List<HomeworkMessage> homeworkList = new ArrayList<>();
    private String courses[] = {
            "软件工程实验",
            "编译原理",
            "云计算概论",
            "通信原理",
            "软件工程导论",
            "Android应用设计与开发"
    };

    private HomeworkMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework);
        initHomework();
        adapter = new HomeworkMessageAdapter(MainActivity_First.this, R.layout.homework_item, homeworkList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.example.administrator.ui.HomeworkMessage homework = homeworkList.get(position);
                //Toast.makeText(MainActivity_First.this, homework.getContent(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity_First.this, HomeworkDetail.class);
                intent.putExtra("deadline",homework.getTime());
                intent.putExtra("cname",homework.getCname());
                startActivity(intent);
            }
        });
    }


    private void initHomework() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                HttpURLConnection connection = null;
                Message message = new Message();
                try {
                    String str = "kind=getHWList&number=" + courses.length;
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

    MyHandler handler = new MyHandler(MainActivity_First.this);
    class MyHandler extends Handler {
        WeakReference<MainActivity_First> MainActivity_FirstWeakReference;
        JSONArray jsonArray;
        public MyHandler(MainActivity_First Activity) {
            MainActivity_FirstWeakReference = new WeakReference<>(Activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0: // 显示本周作业
                        JSONObject json_all = (JSONObject) msg.obj;
                        jsonArray = json_all.getJSONArray("HWData");

                        ArrayList<Integer> int_hw = new ArrayList<>();
                        for(Integer i = 0; i < jsonArray.length(); i++) {
                            int_hw.add(i);
                        }
                        // 对ddl排序
                        Collections.sort(int_hw, new myCompare());

                        // 显示作业列表
                        homeworkList.clear(); // 清除原来的作业

                        for(Integer i = 0; i < int_hw.size(); i++) {
                            JSONObject currentJson = jsonArray.getJSONObject(int_hw.get(i));
                            // 日期时间，去掉结尾的“.0”
                            // 本周作业参数顺序：截止日期，内容，发布人，课程名称，作业类型，作业id
                            //Toast.makeText(MainActivity_First.this, "课程名称："+currentJson.getString("deadline")+" deadline:", Toast.LENGTH_SHORT).show();
                            if (currentJson.getString("deadline").isEmpty() || currentJson.getString("question").isEmpty() || currentJson.getString("cname").isEmpty() || currentJson.getString("hwtype").isEmpty() || currentJson.getString("hid").isEmpty()) {
                                Toast.makeText(MainActivity_First.this, "获取数据出错", Toast.LENGTH_SHORT).show();
                                continue;
                            }

                            HomeworkMessage firstHomework = new HomeworkMessage(currentJson.getString("deadline").substring(0,19),
                                    currentJson.getString("question"), "by  " + "NULL", currentJson.getString("cname"), currentJson.getString("hwtype"), currentJson.getInt("hid"));
                            homeworkList.add(firstHomework);
                        }
                        adapter.notifyDataSetChanged();

                        break;
                    case 3: // 数据库操作失败
                        Toast.makeText(MainActivity_FirstWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 9: // 网络连接错误
                        Toast.makeText(MainActivity_FirstWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity_FirstWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        class myCompare implements Comparator {
            @Override
            public int compare(Object lhs, Object rhs) {
                Integer int1 = (Integer) lhs;
                Integer int2 = (Integer) rhs;
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                int result = -1;
                try {
                    Date dt1 = sdf.parse(jsonArray.getJSONObject(int1).getString("deadline"));
                    Date dt2 = sdf.parse(jsonArray.getJSONObject(int2).getString("deadline"));
                    if (dt1.getTime() > dt2.getTime())
                        result = 1;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return result;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*finish();
        Intent intent = new Intent(MainActivity_Third.this, MainActivity_Third.class);
        startActivity(intent);*/
        initHomework();
    }
}
