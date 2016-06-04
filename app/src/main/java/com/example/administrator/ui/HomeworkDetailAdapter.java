package com.example.administrator.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2016/4/24.
 */
public class HomeworkDetailAdapter extends ArrayAdapter<HomeworkMessage> {

    private int resourceId;

    public HomeworkDetailAdapter(Context context, int textViewResourceId, List<HomeworkMessage> objects)
    {
        super(context,textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        HomeworkMessage homeworkmessage = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.homeworkTime = (TextView) view.findViewById(R.id.homework_detail_time);
            viewHolder.homeworkEditor = (TextView) view.findViewById(R.id.homework_detail_publishman);
            viewHolder.homeworkContent = (TextView) view.findViewById(R.id.homework_detail_content);
            viewHolder.homeworkType = (TextView) view.findViewById(R.id.homework_detail_type);
            viewHolder.homeworkPubTime = (TextView) view.findViewById(R.id.homework_detail_pulishtime);

            viewHolder.radioButtonA = (RadioButton) view.findViewById(R.id.homework_detail_good);
            viewHolder.radioButtonB = (RadioButton) view.findViewById(R.id.homework_detail_bad);


            viewHolder.commentRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
            view.setTag(viewHolder); //��ViewHolder�洢��view
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.homeworkTime.setText(homeworkmessage.getTime());
        viewHolder.homeworkEditor.setText(homeworkmessage.getEditor());
        viewHolder.homeworkContent.setText(homeworkmessage.getContent());
        viewHolder.homeworkType.setText(homeworkmessage.getType());
        viewHolder.homeworkPubTime.setText(homeworkmessage.getPubTime());

        if (homeworkmessage.getIsGood()) {
            /*在这里添加是否将赞图标显示为已被赞*/
        }

        //菜单子项中的单选钮事件
        viewHolder.commentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.homework_detail_good:

                        //点赞事件
                        Toast.makeText(getContext(), "a", Toast.LENGTH_SHORT).show();
//					viewHolder.radioButtonB.setChecked(false);
                        break;
                    case R.id.homework_detail_bad:
                        //点差事件
                        Toast.makeText(getContext(), "b", Toast.LENGTH_SHORT).show();
//					viewHolder.radioButtonA.setChecked(false);
                        break;
                }

            }

        });
        return view;

    }

    class ViewHolder {

        TextView homeworkTime;
        TextView homeworkType;
        TextView homeworkPubTime;
        TextView homeworkEditor;
        TextView homeworkContent;
        RadioGroup commentRadioGroup;
        RadioButton radioButtonA;
        RadioButton radioButtonB;
    }
}
