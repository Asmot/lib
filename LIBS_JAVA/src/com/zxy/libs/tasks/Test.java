package com.zxy.libs.tasks;

public class Test {

	public static void main(String[] args) throws Exception {
//		TaskManager manager = new TaskManager();
//		manager.startTask(new TaskItem());
//		manager.startTask(new TaskItem());
//		manager.startTask(new TaskItem());
//		manager.startTask(new TaskItem());
		
		TaskManager manager = TaskManager.getInstance(2);
		
		TaskItem item = new TaskItem();
		
		manager.doTask(item);
//		manager.doTask(new TaskItem());
//		manager.doTask(new TaskItem());
//		manager.doTask(new TaskItem());
////		manager.doTask(new TaskItem());
////		manager.doTask(new TaskItem());
////		manager.doTask(new TaskItem());
////		manager.doTask(new TaskItem());
//		
//		
		Thread.sleep(1000);
		manager.stopTask(item);
		
//		Thread.sleep(1000);
		manager.startTask(item);
		manager.startTask(item);
		manager.startTask(item);
		manager.startTask(item);
		

	}

}
