package com.zxy.libs.tasks;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.zxy.libs.threads.ThreadPool;
import com.zxy.libs.threads.ThreadTask;

/**
 * 自定义任务处理框架
 * 包含，开始，暂停，从新开始，删除任务等操作
 * @author zxy94400
 *
 */
public class TaskManager {
	
	private static TaskManager mTaskManager;
	
	private ThreadPool mThreadPool;
	
	private static final int DEFAULT_THREADNUM = 5;
	
	private Hashtable<String, ThreadTask> tasks = new Hashtable<>();

	/**
	 * 获取实例，可传入线程池数量（数量以第一次调用时传入的参数为准）
	 * 
	 * @param context
	 * @param threadNum
	 * @return
	 */
	public static TaskManager getInstance(int threadNum) {
		return getInstance(true, threadNum);
	}

	/**
	 * 获取实例，默认的线程池数量为5
	 * 
	 * @param context
	 * @return
	 */
	public static TaskManager getInstance() {
		return getInstance(true, DEFAULT_THREADNUM);
	}

	/**
	 * 获取实例，如果设置为true，则需要core里提供线程池，如果设置为false则不提供线程池
	 * 
	 * @param isNeedThreadPool
	 * @return
	 */
	public static TaskManager getInstance(boolean isNeedThreadPool) {
		return getInstance(isNeedThreadPool, DEFAULT_THREADNUM);
	}

	/**
	 * 获取实例，传入线程池对象
	 * 
	 * @param threadPool
	 * @return
	 */
	public synchronized static TaskManager getInstance(ThreadPool threadPool) {
		if (mTaskManager == null) {
			mTaskManager = new TaskManager(threadPool);
		} else {
			if (mTaskManager.mThreadPool == null) {
				mTaskManager.mThreadPool = threadPool;
			}
		}
		return mTaskManager;
	}

	private synchronized static TaskManager getInstance(boolean isNeedThreadPool,
			int threadNum) {
		try {
			if (mTaskManager == null) {
				mTaskManager = new TaskManager(isNeedThreadPool, threadNum);
			} else {
				if (isNeedThreadPool && mTaskManager.mThreadPool == null) {
					mTaskManager.mThreadPool = ThreadPool.getInstance(threadNum);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return mTaskManager;
	}

	private TaskManager(ThreadPool threadPool) {
		try {
			this.mThreadPool = threadPool;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private TaskManager(boolean isNeedThreadPool, int threadNum) {
		try {
			if (isNeedThreadPool) {

				mThreadPool = ThreadPool.getInstance(threadNum);
			}
		} catch (Throwable e) {

			e.printStackTrace();
		}
	}
	
	public void doTask(TaskItem item) throws Exception {
		startTask(item);
	}
	
	public void stopTask(TaskItem item) {
		ThreadTask task = tasks.get(item.getId());
		if(task == null) {
			return;
		}
		task.cancelTask();
		tasks.remove(item.getId());
	}
	
	public void startTask(TaskItem item) throws Exception {
		if(mThreadPool == null) {
			throw new Exception("threadpool is null ");
		}
		if(!tasks.containsKey(item.getId())) {
			tasks.put(item.getId(), new TaskWorker(item));
		}
		mThreadPool.addTask(tasks.get(item.getId()));
	}
}
