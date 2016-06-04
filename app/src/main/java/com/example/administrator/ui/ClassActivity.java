package com.example.administrator.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClassActivity extends AppCompatActivity {

    private int colors[] = {
            Color.rgb(0xee, 0xff, 0xff),
            Color.rgb(0xf0,0x96,0x09),
            Color.rgb(0x8c,0xbf,0x26),
            Color.rgb(0x00,0xab,0xa9),
            Color.rgb(0x99,0x6c,0x33),
            Color.rgb(0x3b,0x92,0xbc),
            Color.rgb(0xd5,0x4d,0x34),
            Color.rgb(0xcc,0xcc,0xcc)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    void setClass(LinearLayout day, String title, String place, String last, String time, int classes, int color,
                  Context context, View.OnClickListener OnClickClassListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_item, null);
        view.setMinimumHeight(dip2px(context, classes * 48));
        view.setBackgroundColor(colors[color]);
        ((TextView)view.findViewById(R.id.item_title)).setText(title);
        ((TextView)view.findViewById(R.id.item_place)).setText(place);
        ((TextView)view.findViewById(R.id.item_last)).setText(last);
        ((TextView)view.findViewById(R.id.item_time)).setText(time);

        view.setOnClickListener(OnClickClassListener);
        TextView blank1 = new TextView(context);
        TextView blank2 = new TextView(context);
        blank1.setHeight(dip2px(context, classes));
        blank2.setHeight(dip2px(context, classes));
        day.addView(blank1);
        day.addView(view);
        day.addView(blank2);
    }

    void setNoClass(LinearLayout day, int classes, int color, Context context) {
        TextView blank = new TextView(context);
        if (color == 0)
            blank.setMinHeight(dip2px(context, classes * 50));
        blank.setBackgroundColor(colors[color]);
        day.addView(blank);
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
