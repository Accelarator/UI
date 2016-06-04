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
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private Button registerButton;
    private static SharedPreferences.Editor editor;
    private static String cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //获取SharedPreferences对象
        SharedPreferences sp = LoginActivity.this.getSharedPreferences("DATA", MODE_PRIVATE);
        //存入数据
        editor = sp.edit();

        accountEdit = (EditText) findViewById(R.id.login_account);
        passwordEdit = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    MyHandler handler = new MyHandler(LoginActivity.this);
    static class MyHandler extends Handler {
        WeakReference<LoginActivity> loginActivityWeakReference;

        public MyHandler(LoginActivity loginActivity) {
            loginActivityWeakReference = new WeakReference<>(loginActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: // 登陆成功
                    editor.putString("cell", cell);
                    editor.apply();
                    Intent intent = new Intent(loginActivityWeakReference.get(), TabActivity.class);
                    loginActivityWeakReference.get().startActivity(intent);
                    break;
                case 1: // 账号不存在
                    Toast.makeText(loginActivityWeakReference.get(), "账号不存在", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // 密码不正确
                    Toast.makeText(loginActivityWeakReference.get(), "密码不正确", Toast.LENGTH_SHORT).show();
                    break;
                case 3: // 数据库操作失败
                    Toast.makeText(loginActivityWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case 9: // 网络连接错误
                    Toast.makeText(loginActivityWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(loginActivityWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            if (accountEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals(""))
                Toast.makeText(this, "请输入帐号和密码", Toast.LENGTH_SHORT).show();
            else {
                cell = accountEdit.getText().toString();
                httplogin();
            }
        }
        else if (view.getId() == R.id.register_button) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void httplogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Message message = new Message();
                try {
                    String str = "kind=login&username=" + accountEdit.getText() + "&password=" + passwordEdit.getText();
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
