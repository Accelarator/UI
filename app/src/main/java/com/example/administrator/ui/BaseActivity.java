package com.example.administrator.ui;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Administrator on 2016/4/17.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public void exit() {
        ActivityCollector.finishAll();
    }

}
