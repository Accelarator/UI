package com.example.administrator.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by Administrator on 2016/4/17.
 */
public class TabActivity extends android.app.TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent(TabActivity.this, MainActivity_First.class);
        spec = tabHost.newTabSpec("tab1").setIndicator("作业", res.getDrawable(R.drawable.ic_launcher)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(TabActivity.this, MainActivity_Second.class);
        spec = tabHost.newTabSpec("tab2").setIndicator("课表", res.getDrawable(R.drawable.ic_launcher)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(TabActivity.this, MainActivity_Third.class);
        spec = tabHost.newTabSpec("tab3").setIndicator("我", res.getDrawable(R.drawable.ic_launcher)).setContent(intent);
        tabHost.addTab(spec);

        Intent intent_now = getIntent();
        Integer currentTab = intent_now.getIntExtra("CurrentTab", 0);
        tabHost.setCurrentTab(currentTab);
    }
}
