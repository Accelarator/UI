package com.example.administrator.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HomeworkMessageAdapter extends ArrayAdapter<HomeworkMessage> {
	
	private int resourceId;
	
	public HomeworkMessageAdapter(Context context, int textViewResourceId, List<HomeworkMessage> objects)
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
			view =LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.homeworkCourse = (TextView) view.findViewById(R.id.text_course_name);
			viewHolder.homeworkTime = (TextView) view.findViewById(R.id.text_homework_time);
			viewHolder.homeworkEditor = (TextView) view.findViewById(R.id.text_publishman);
			viewHolder.homeworkContent = (TextView) view.findViewById(R.id.text_homework_content);
			view.setTag(viewHolder); //��ViewHolder�洢��view
		}
		else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.homeworkCourse.setText(homeworkmessage.getCname());
		viewHolder.homeworkTime.setText(homeworkmessage.getTime());
		viewHolder.homeworkEditor.setText(homeworkmessage.getEditor());
		viewHolder.homeworkContent.setText(homeworkmessage.getContent());
		return view;
		
	}
	
	class ViewHolder {

		TextView homeworkCourse;
		TextView homeworkTime;
		TextView homeworkEditor;
		TextView homeworkContent;
	
	}

}
