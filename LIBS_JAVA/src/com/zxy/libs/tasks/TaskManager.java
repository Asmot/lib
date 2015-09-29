package com.zxy.libs.tasks;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.zxy.libs.threads.ThreadPool;
import com.zxy.libs.threads.ThreadTask;

/**
 * �Զ�����������
 * ��������ʼ����ͣ�����¿�ʼ��ɾ������Ȳ���
 * @author zxy94400
 *
 */
public class TaskManager {
	
	private static TaskManager mTaskManager;
	
	private ThreadPool mThreadPool;
	
	private static final int DEFAULT_THREADNUM = 5;
	
	private Hashtable<String, ThreadTask> tasks = new Hashtable<>();

	/**
	 * ��ȡʵ�����ɴ����̳߳������������Ե�һ�ε���ʱ����Ĳ���Ϊ׼��
	 * 
	 * @param context
	 * @param threadNum
	 * @return
	 */
	public static TaskManager getInstance(int threadNum) {
		return getInstance(true, threadNum);
	}

	/**
	 * ��ȡʵ����Ĭ�ϵ��̳߳�����Ϊ5
	 * 
	 * @param context
	 * @return
	 */
	public static TaskManager getInstance() {
		return getInstance(true, DEFAULT_THREADNUM);
	}

	/**
	 * ��ȡʵ�����������Ϊtrue������Ҫcore���ṩ�̳߳أ��������Ϊfalse���ṩ�̳߳�
	 * 
	 * @param isNeedThreadPool
	 * @return
	 */
	public static TaskManager getInstance(boolean isNeedThreadPool) {
		return getInstance(isNeedThreadPool, DEFAULT_THREADNUM);
	}

	/**
	 * ��ȡʵ���������̳߳ض���
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
