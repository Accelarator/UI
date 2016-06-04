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
import android.widget.ImageView;
import android.widget.TextView;
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
 * Created by Administrator on 2016/4/17.
 */
public class MainActivity_Third extends BaseActivity implements View.OnClickListener {

    private String cell;

    private ImageView exitImageView;
    private Button editUserInfoButton;
    private ImageView userImageView;
    private static TextView userInfoUserName; //用户姓名
    private static TextView userInfoSchool; //用户学校信息
    private static TextView userInfoPhoneNumber; //用户手机号
    private static TextView userInfoMajor; //用户专业
    private static TextView userInfoGrade; //用户年级
    private static TextView userInfoSex; //用户性别
    private TextView userInfoPlace; //用户所在地

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_page);

        SharedPreferences sp = MainActivity_Third.this.getSharedPreferences("DATA", MODE_PRIVATE);
        cell = sp.getString("cell", "none");

        exitImageView = (ImageView) findViewById(R.id.page_image_exit);
        userImageView = (ImageView) findViewById(R.id.info_page_user_image);
        editUserInfoButton = (Button) findViewById(R.id.edit_user_infos_btn);
        userInfoUserName = (TextView) findViewById(R.id.text_username_id);
        userInfoSchool = (TextView) findViewById(R.id.text_school_id);
        userInfoPhoneNumber = (TextView) findViewById(R.id.user_info_phone_number);
        userInfoMajor = (TextView) findViewById(R.id.user_info_major);
        userInfoGrade = (TextView) findViewById(R.id.user_info_grade);
        userInfoSex = (TextView) findViewById(R.id.user_info_sex);
        //userInfoPlace = (TextView) findViewById(R.id.user_info_place);

        exitImageView.setOnClickListener(this);
        userImageView.setOnClickListener(this);
        editUserInfoButton.setOnClickListener(this);

        connectTo();
    }

    private void connectTo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Message message = new Message();
                try {
                    String str = "kind=getUserInfo&cell=" + cell;
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
                    while ((line = in.readLine()) != null) {
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

    MyHandler handler = new MyHandler(MainActivity_Third.this);
    static class MyHandler extends Handler {
        WeakReference<MainActivity_Third> MainActivity_ThirdWeakReference;
        public MyHandler(MainActivity_Third Activity) {
            MainActivity_ThirdWeakReference = new WeakReference<>(Activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0: // 显示个人信息
                        JSONObject json_all = (JSONObject) msg.obj;
                        JSONObject json = json_all.getJSONObject("UserData");

                        userInfoUserName.setText(json.getString("uname")); //用户姓名
                        userInfoSchool.setText(json.getString("school")); //用户学校信息
                        userInfoPhoneNumber.setText(json.getString("cell")); //用户手机号
                        userInfoMajor.setText(json.getString("major")); //用户专业
                        userInfoGrade.setText(json.getString("grade")); //用户年级
                        userInfoSex.setText(json.getString("sex")); //用户性别
                        break;
                    case 1: // 账号不存在
                        Toast.makeText(MainActivity_ThirdWeakReference.get(), "当前账号不存在", Toast.LENGTH_SHORT).show();
                        break;
                    case 3: // 数据库操作失败
                        Toast.makeText(MainActivity_ThirdWeakReference.get(), "数据库操作失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 9: // 网络连接错误
                        Toast.makeText(MainActivity_ThirdWeakReference.get(), "网络连接错误。可能网络没有开启，或服务端停止服务。",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity_ThirdWeakReference.get(), "传输失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.page_image_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_Third.this);
                builder.setTitle("退出");
                builder.setMessage("确定退出吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
                    }
                });
                builder.show();
                break;

            case R.id.info_page_user_image:
                Toast.makeText(this, "userImage", Toast.LENGTH_SHORT).show();
                break;

            case R.id.edit_user_infos_btn:
                Intent intent = new Intent(MainActivity_Third.this, EditUserInfos.class);
                intent.putExtra("grade", userInfoGrade.getText());
                intent.putExtra("major", userInfoMajor.getText());
                intent.putExtra("cell", userInfoPhoneNumber.getText());
                intent.putExtra("school", userInfoSchool.getText());
                intent.putExtra("sex", userInfoSex.getText());
                intent.putExtra("uname", userInfoUserName.getText());
                startActivity(intent);
                break;
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    finish();
                    Intent intent = new Intent(MainActivity_Third.this, MainActivity_Third.class);
                    startActivity(intent);
                }
                break;
            default:
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        /*finish();
        Intent intent = new Intent(MainActivity_Third.this, MainActivity_Third.class);
        startActivity(intent);*/
        connectTo();
    }
}
