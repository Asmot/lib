package com.zxy.libs.tasks;

public class TaskItem {
	private static int index = 0;
	
	private String sid;
	
	private static String CreateId(String type) {
		index++;
		return type + index;
	}
	
	
	public TaskItem() {
	}
	
	public String getId() {
		if(sid == null) {
			sid = CreateId("TaskItem : ");
		}
		return this.sid;
	}
}
