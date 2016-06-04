package com.example.administrator.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.List;

/**
 * Created by Administrator on 2016/5/21.
 */
public class WeeklyHomeworks extends BaseActivity  {

    private Bundle extras;
    private String courseName;
    private Context myContext;
    private TextView homeworkTitle;

    private WeeklyHomeworksAdapter adapter;
    private static List<HomeworkMessage> homeworkList = new ArrayList<>();

    private String courses[] = {
            "软件工程实验",
            "编译原理",
            "云计算概论",
            "通信原理",
            "软件工程导论",
            "Android应用设计与开发"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework);

        homeworkTitle = (TextView) findViewById(R.id.release_title_text);

        extras = getIntent().getExtras();
        if (extras != null) {
            courseName = extras.getString("cname");
        }

        homeworkTitle.setText(courseName);

        initHomework();
        adapter = new WeeklyHomeworksAdapter(WeeklyHomeworks.this, R.layout.homework_item, homeworkList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.example.administrator.ui.HomeworkMessage homework = homeworkList.get(position);
                //Toast.makeText(MainActivity_First.this, homework.getContent(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WeeklyHomeworks.this, HomeworkDetail.class);
                intent.putExtra("deadline",homework.getTime());
                intent.putExtra("cname",courseName);
                startActivity(intent);
            }
        });
    }

    public void initHomework() {
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
                    Toast.makeText(WeeklyHomeworks.this, str, Toast.LENGTH_SHORT).show();
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
                    message = Message.obtain(); // 重新获取message
                    handler.sendMessage(message);
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }

        }).start();
    }



    MyHandler handler = new MyHandler(WeeklyHomeworks.this);
    class MyHandler extends Handler {
        WeakReference<WeeklyHomeworks> WeeklyHomeworksWeakReference;
        JSONArray jsonArray;
        ClassActivity classTable;

        public MyHandler(WeeklyHomeworks Activity) {
            WeeklyHomeworksWeakReference = new WeakReference<>(Activity);
            classTable = new ClassActivity();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0: // 显示同一次作业
                        JSONObject json_all = (JSONObject) msg.obj;
                        jsonArray = json_all.getJSONArray("HWData");
                        ArrayList<Integer> int_hw = new ArrayList<>();
                        //Toast.makeText(HomeworkDetail.this, "课程名称："+cname+" deadline:"+time, Toast.LENGTH_SHORT).show();
                        //courseName.setText(cname);

                        for(Integer i = 0; i < jsonArray.length(); i++) {
                            //Toast.makeText(WeeklyHomeworks.this, "课程名称："+jsonArray.getJSONObject(i).getString("cname")+" deadline:", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(WeeklyHomeworks.this, "课程名称："+courseName+" deadline:", Toast.LENGTH_SHORT).show();
                            if(jsonArray.getJSONObject(i).getInt("mark") != -1
                                    &&jsonArray.getJSONObject(i).getString("cname").equals(courseName)) { //修改点1：选择数据库中名为courseName的数据
                                int_hw.add(i);
                            }
                        }
                        // 对评分排序
                        Collections.sort(int_hw, new myCompare());

                        // 显示作业列表
                        homeworkList.clear(); // 清除原来的作业
                        for(Integer i = 0; i < int_hw.size(); i++) {
                            JSONObject currentJson = jsonArray.getJSONObject(int_hw.get(i));
                            //Toast.makeText(WeeklyHomeworks.this, "课程名称："+currentJson.getString("cname")+" deadline:", Toast.LENGTH_SHORT).show();
                            // 日期时间，去掉结尾的“.0”
                            // 详细内容参数顺序为：截止日期，内容，发布人，作业来源， id
                            HomeworkMessage firstHomework = new HomeworkMessage(currentJson.getString("deadline").substring(0, 19),
                                    currentJson.getString("question"), "by  " + "NULL" , currentJson.getString("source"), currentJson.getInt("hid")); //修改点2：传入参数变化
                            homeworkList.add(firstHomework);
                            //Toast.makeText(HomeworkDetail.this, "课程名称："+currentJson.getString("deadline")+" deadline:"+time, Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();

                        break;
                    case 3: // 数据库操作失败
                        Toast.makeText(WeeklyHomeworksWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 9: // 网络连接错误
                        Toast.makeText(WeeklyHomeworksWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(WeeklyHomeworksWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        class myCompare implements Comparator {
            @Override
            public int compare(Object lhs, Object rhs) {
                Integer int1 = (Integer) lhs;
                Integer int2 = (Integer) rhs;

                int result = -1;
                try {
                    Integer mark1 = jsonArray.getJSONObject(int1).getInt("mark");
                    Integer mark2 = jsonArray.getJSONObject(int2).getInt("mark");
                    if (mark1 < mark2)
                        result = 1;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return result;
            }
        }
    }
}
