package com.example.administrator.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2016/4/24.
 */
public class HomeworkPublish extends BaseActivity implements View.OnClickListener {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_publish);
    }*/

    private TextView source;
    private TextView content;
    private TextView deadline;
    private String cname;
    private Button submitButton;
    private static String cell;
    private Spinner sourceSpinner,typeSpinner;
    private ArrayAdapter<String> sourceSpinnerAdapter,typeSpinnerAdapter;


    private static SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.homework_publish);

        EditText scroll_text = (EditText) findViewById(R.id.homework_content);
        scroll_text.setMovementMethod(ScrollingMovementMethod.getInstance());

        //获取SharedPreferences对象
        SharedPreferences sp = HomeworkPublish.this.getSharedPreferences("DATA", MODE_PRIVATE);
        //存入数据
        editor = sp.edit();

        cell = sp.getString("cell", "none");

        content = (EditText)findViewById(R.id.homework_content);
        deadline = (EditText)findViewById(R.id.homework_deadline);

        submitButton = (Button) findViewById(R.id.homework_submit);
        submitButton.setOnClickListener(HomeworkPublish.this);

        Intent intent = getIntent();
        cname = intent.getStringExtra("cname");

        sourceSpinner = (Spinner) findViewById(R.id.homework_source); //增加了来源的Spinner
        typeSpinner = (Spinner) findViewById(R.id.homework_type);

        sourceSpinnerAdapter = new ArrayAdapter<String>(HomeworkPublish.this, android.R.layout.simple_spinner_item, getSourceData());
        typeSpinnerAdapter = new ArrayAdapter<String>(HomeworkPublish.this, android.R.layout.simple_spinner_item, getTypeData());

        sourceSpinner.setAdapter(sourceSpinnerAdapter);
        typeSpinner.setAdapter(typeSpinnerAdapter);

    }


    public List<String> getTypeData() {
        List<String> list=new ArrayList<String>();
        list.add("普通作业");
        list.add("大作业");
        return list;
    }

    public List<String> getSourceData() {
        List<String> list=new ArrayList<String>();
        list.add("书本");
        list.add("黑板");
        list.add("网站");
        list.add("其他");
        return list;
    }

    /*等待修改，添加来源的信息*/
    public void onClick(View view) {
        if (view.getId() == R.id.homework_submit) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    Message message = new Message();
                    try {
                        String str = "kind=HWPublish&uploadTime=";
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String uploadTime = sDateFormat.format(new Date());
                        Integer hwtype = 0;
                        if(typeSpinner.getSelectedItem().toString().equals("普通作业"))
                            hwtype = 0;
                        else if(typeSpinner.getSelectedItem().toString().equals("大作业"))
                            hwtype = 1;

                        str = str + uploadTime + "&cname=" + cname + "&source=" + source.getText().toString()
                                + "&question=" + content.getText().toString() + "&hwtype=" + hwtype
                                + "&deadline=" + deadline.getText().toString() + "&cell=" + cell;

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


            Intent intent = new Intent(HomeworkPublish.this, HomeworkDetail.class);
            startActivity(intent);
        }
    }

    MyHandler handler = new MyHandler(HomeworkPublish.this);
    class MyHandler extends Handler {
        WeakReference<HomeworkPublish>HomeworkPublishWeakReference;
        HomeworkPublish PublishActivity;

        public MyHandler(HomeworkPublish Activity) {
            HomeworkPublishWeakReference = new WeakReference<>(Activity);
            PublishActivity = Activity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if(msg.what == 0) {// 上传成功
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeworkPublishWeakReference.get());
                    builder.setMessage("上传成功！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(HomeworkPublishWeakReference.get(), TabActivity.class);
                            intent.putExtra("CurrentTab", 2);
                            PublishActivity.startActivity(intent);
                            PublishActivity.finish();
                        }
                    });
                    builder.show();
                }
                else if(msg.what == 1) {
                    Toast.makeText(HomeworkPublishWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                }
                else if(msg.what == 3) {// 数据库操作失败
                    Toast.makeText(HomeworkPublishWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                }
                else if(msg.what == 9) {// 网络连接错误
                    Toast.makeText(HomeworkPublishWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(HomeworkPublishWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getDataSource() {
        List<String> list=new ArrayList<String>();
        list.add("大作业");
        list.add("普通作业");
        return list;
    }
}
