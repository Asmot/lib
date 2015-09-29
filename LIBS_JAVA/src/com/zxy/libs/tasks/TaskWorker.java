package com.zxy.libs.tasks;

import com.zxy.libs.threads.ThreadTask;

public class TaskWorker extends ThreadTask{

	private TaskItem item ;
	boolean flag = true;
	
	public TaskWorker(TaskItem item) {
		this.item = item;
	}
	
	
	//任务需要执行的操作
	@Override
	public void runTask() {
		int index = 0;
		while(flag) {
			try {
				System.out.println("execute : " + item.getId() + " - " + index);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag = false;
			}
			index ++;
			if(index == 5) {
				flag = false;
			}
		}
	}
	
	
}
