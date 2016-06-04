package com.example.administrator.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/5/16.
 */
public class EditUserInfos extends BaseActivity implements View.OnClickListener {

    private EditText editInfoPhoneNumber;
    private EditText editInfoMajor;
    private EditText editInfoGrade;
    private EditText editInfoSex;
    private EditText editInfoPlace;
    private Button submitButton;
    private ImageView backButton;
    private String oldcell;
    private static SharedPreferences.Editor editor;
    private static String cell;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_info);

        //获取SharedPreferences对象
        SharedPreferences sp = EditUserInfos.this.getSharedPreferences("DATA", MODE_PRIVATE);
        //存入数据
        editor = sp.edit();

        editInfoPhoneNumber = (EditText) findViewById(R.id.edit_info_phone_number);
        editInfoMajor = (EditText) findViewById(R.id.edit_info_major);
        editInfoGrade = (EditText) findViewById(R.id.edit_info_grade);
        editInfoSex = (EditText) findViewById(R.id.edit_info_sex);
       // editInfoPlace = (EditText) findViewById(R.id.edit_info_place);
        submitButton = (Button) findViewById(R.id.edit_info_submit);
        backButton = (ImageView) findViewById(R.id.edit_user_info_backbutton);

        submitButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        Intent intent = getIntent();
        oldcell = intent.getStringExtra("cell");
        editInfoPhoneNumber.setText(intent.getStringExtra("cell"));
        editInfoMajor.setText(intent.getStringExtra("major"));
        editInfoGrade.setText(intent.getStringExtra("grade"));
        editInfoSex.setText(intent.getStringExtra("sex"));

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_info_submit:
                cell = editInfoPhoneNumber.getText().toString();
                submitEditMessage();
                break;

            case R.id.edit_user_info_backbutton:
                finish();
                break;

            default:
                break;
        }
    }

    public void submitEditMessage() {
        /*在这里添加发送信息到后台的代码*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Message message = new Message();
                try {
                    String str = "kind=setUserInfo&oldcell=" + oldcell + "&newcell=" + editInfoPhoneNumber.getText() + "&major="
                            + editInfoMajor.getText() + "&grade=" + editInfoGrade.getText()
                            + "&sex=" + editInfoSex.getText();
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

    MyHandler handler = new MyHandler(EditUserInfos.this);
    static class MyHandler extends Handler {
        WeakReference<EditUserInfos> EditUserInfosWeakReference;
        EditUserInfos editActivity;
        public MyHandler(EditUserInfos Activity) {
            EditUserInfosWeakReference = new WeakReference<>(Activity);
            editActivity = Activity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0: // 修改成功
                        editor.putString("cell", cell);
                        editor.apply();
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserInfosWeakReference.get());
                        builder.setMessage("提交成功！");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(EditUserInfosWeakReference.get(), TabActivity.class);
                                intent.putExtra("CurrentTab", 2);
                                editActivity.startActivity(intent);
                                editActivity.finish();
                            }
                        });
                        builder.show();
                        break;
                    case 1: // 该手机号已注册
                        Toast.makeText(EditUserInfosWeakReference.get(), "该手机号已注册", Toast.LENGTH_SHORT).show();
                        break;
                    case 3: // 数据库操作失败
                        Toast.makeText(EditUserInfosWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 9: // 网络连接错误
                        Toast.makeText(EditUserInfosWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(EditUserInfosWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
