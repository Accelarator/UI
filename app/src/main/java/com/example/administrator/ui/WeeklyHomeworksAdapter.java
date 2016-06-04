package com.example.administrator.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/5/21.
 */
public class WeeklyHomeworksAdapter extends ArrayAdapter<HomeworkMessage> {

    private int resourceId;

    public WeeklyHomeworksAdapter(Context context, int textViewResourceId, List<HomeworkMessage> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        HomeworkMessage homeworkMessage = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (contentView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.homeworkDeadline = (TextView) view.findViewById(R.id.text_homework_time);
            viewHolder.homeworkSource = (TextView) view.findViewById(R.id.text_course_name);
            viewHolder.homeworkContent = (TextView) view.findViewById(R.id.text_homework_content);
            viewHolder.homeworkPublishman = (TextView) view.findViewById(R.id.text_publishman);
            view.setTag(viewHolder);
        } else {
            view = contentView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.homeworkDeadline.setText(homeworkMessage.getTime());
        viewHolder.homeworkSource.setText(homeworkMessage.getSource());
        viewHolder.homeworkContent.setText(homeworkMessage.getContent());
        viewHolder.homeworkPublishman.setText(homeworkMessage.getEditor());

        return view;
    }

    class ViewHolder {
        TextView homeworkDeadline;
        TextView homeworkSource;
        TextView homeworkContent;
        TextView homeworkPublishman;
    }
}
