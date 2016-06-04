package com.example.administrator.ui;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AddCourseTableActivity extends Activity {
    private Spinner spinner1,spinner2;
    private ArrayAdapter adapter1,adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_course_table);
        spinner1 = (Spinner) findViewById(R.id.spinner_xn);
        spinner2 = (Spinner) findViewById(R.id.spinner_xq);

        adapter1 = new ArrayAdapter<String>(AddCourseTableActivity.this, android.R.layout.simple_spinner_item, getXNDataSource());
        adapter2 = new ArrayAdapter<String>(AddCourseTableActivity.this, android.R.layout.simple_spinner_item, getXQDataSource());

        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);

        Button OKButton = (Button) findViewById(R.id.bt_ok);
        Button backButton = (Button) findViewById(R.id.bt_back);
        //完成按钮响应
        OKButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent2 = new Intent(AddCourseTableActivity.this, TabActivity.class);
                startActivity(intent2);
            }
        });
        //返回按钮
        backButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent3 = new Intent(AddCourseTableActivity.this, TabActivity.class);
                startActivity(intent3);
            }
        });
    }

    //年度学期下拉菜单初始化
    public List<String> getXNDataSource() {
        List<String> list=new ArrayList<String>();
        list.add("2013~2014");
        list.add("2014~2015");
        list.add("2015~2016");
        list.add("2016~2017");

        return list;
    }

    public List<String> getXQDataSource() {
        List<String> list=new ArrayList<String>();
        list.add("第一学期");
        list.add("第二学期");
        list.add("第三学期");
        return list;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

/*下拉菜单响应
spinner.setOnItemSelectedListener(new OnItemSelectedListener()
{

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1,
			int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
});

}
*/


}
