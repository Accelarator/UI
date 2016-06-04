package com.example.administrator.ui;

public class HomeworkMessage {

	private String time;
	private String content;
	private String editor;
	private String cname;
	private String type;
	private String pubTime;
	private String source;
	private Boolean isGood;

	private int hid;

	public HomeworkMessage(String time, String content, String editor, String source, int hid) {
		this.time=time;
		this.content=content;
		this.editor=editor;
		this.source = source;
		this.hid=hid;
	}

	public HomeworkMessage(String time, String content, String editor, String cname, String type, int hid){
		this.time=time;
		this.content=content;
		this.editor=editor;
		this.cname=cname;
		this.type=type;
		this.hid=hid;
	}

	public HomeworkMessage(String time, String content, String editor, String type, String pubTime, Boolean isGood, int hid) {
		this.time=time;
		this.content=content;
		this.editor=editor;
		this.type=type;
		this.pubTime=pubTime;
		this.isGood=isGood;
		this.hid=hid;
	}

	public String getTime(){
		return time;
	}

	public String getContent(){
		return content;
	}

	public String getEditor(){
		return editor;
	}

	public String getCname(){
		return cname;
	}

	public Integer getHid(){
		return hid;
	}

	public String getType() {
		return type == "0" ? "普通作业" : "大作业";
	}

	public String getPubTime() {
		return pubTime;
	}

	public String getSource() {
		return source;
	}

	public Boolean getIsGood() {
		return isGood;
	}
}

