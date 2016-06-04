package com.example.administrator.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/4/17.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Button registerButton;
    private EditText phoneNumberEdit;
    private EditText passwordEdit;
    private EditText passwordConfirmEdit;
    private EditText schoolEdit;
    private EditText majorEdit;
    private EditText gradeEdit;
    private static SharedPreferences.Editor editor;
    private static String cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        //获取SharedPreferences对象
        SharedPreferences sp = RegisterActivity.this.getSharedPreferences("DATA", MODE_PRIVATE);
        //存入数据
        editor = sp.edit();

        Button registerButton = (Button) findViewById(R.id.register_button_1);
        phoneNumberEdit = (EditText) findViewById(R.id.register_phone_number);
        passwordEdit = (EditText) findViewById(R.id.register_password);
        passwordConfirmEdit = (EditText) findViewById(R.id.register_password_confirm);
        schoolEdit = (EditText) findViewById(R.id.register_school);
        majorEdit = (EditText) findViewById(R.id.register_major);
        gradeEdit = (EditText) findViewById(R.id.register_grade);
        registerButton.setOnClickListener(this);
    }

    MyHandler handler = new MyHandler(RegisterActivity.this);
    static class MyHandler extends Handler {
        WeakReference<RegisterActivity> registerActivityWeakReference;
        public MyHandler(RegisterActivity activity){
            registerActivityWeakReference = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: // 注册成功
                    editor.putString("cell", cell);
                    editor.apply();
                    Intent intent = new Intent(registerActivityWeakReference.get(), TabActivity.class);
                    registerActivityWeakReference.get().startActivity(intent);
                    break;
                case 1: // 账号已存在
                    Toast.makeText(registerActivityWeakReference.get(), "账号已存在", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // 插入失败
                    Toast.makeText(registerActivityWeakReference.get(), "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3: // 数据库操作失败
                    Toast.makeText(registerActivityWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case 9: // 网络连接错误
                    Toast.makeText(registerActivityWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(registerActivityWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register_button_1) {
            if (phoneNumberEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals("")
                    || passwordConfirmEdit.getText().toString().equals(""))
                Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            else if (passwordEdit.getText().toString().equals(passwordConfirmEdit.getText().toString())) {
                cell = phoneNumberEdit.getText().toString();
                httpRegister();
            }
            else
                Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
        }
    }

    private void httpRegister() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Message message = new Message();
                try {
                    String str = "kind=register&username=" + phoneNumberEdit.getText() + "&password=" + passwordEdit.getText();
                    URL url = new URL("http://119.29.148.205:8080/hwServer/main");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.getOutputStream().write(str.getBytes("utf8"));
                    connection.getOutputStream().flush();
                    connection.getOutputStream().close();
                    InputStream input = connection.getInputStream();

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    input.close();
                    byte[] data = outputStream.toByteArray();
                    String sdata = new String(data);
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
}
