package com.zxy.libs.threads;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import com.zxy.libs.threads.ThreadTask.OnThreadTaskListener;


/**
 * 
 * ����ģʽ�̳߳�
 * 
 */
public final class ThreadPool {

	private static ThreadPool mThreadPool = null;
	private static final int DEFAULT_THREADNUM = 5;
	private ExecutorService mThreadPools;
	private ConcurrentHashMap<ThreadTask, Future<?>> queue = new ConcurrentHashMap<ThreadTask, Future<?>>();

	private OnThreadTaskListener listener = new OnThreadTaskListener() {

		// ����
		@Override
		public void running(ThreadTask threadTask) {

		}

		@Override
		public void runOver(ThreadTask threadTask) {
			removeFromQueue(threadTask, false);

		}

		@Override
		public void cancelTask(ThreadTask threadTask) {
	 
			removeFromQueue(threadTask, true);

		}
	};

	/**
	 * ��ȡָ���������̳߳�
	 * 
	 * @param threadNum
	 *            �̳߳���Ŀ
	 * @return
	 */
	public synchronized static ThreadPool getInstance(int threadNum) {
		if (mThreadPool == null) {
			mThreadPool = new ThreadPool(threadNum);
		}
		return mThreadPool;

	}

	/**
	 * ʵ����Ĭ���߳��������̳߳أ�Ĭ��������5��
	 * 
	 * @return
	 */
	public static ThreadPool getInstance() {

		return getInstance(DEFAULT_THREADNUM);

	}

	private ThreadPool(int threadNum) {
		try {
			mThreadPools = Executors.newFixedThreadPool(threadNum);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����߳�����
	 * 
	 * @param threadTask
	 * @throws AMapCoreException
	 */
	// TODO �ظ��߼�Ŀǰ����ʽ�ǲ���
	public void addTask(ThreadTask threadTask) {

		try {
			if (containKey(threadTask)) {
				return;
			}
			if (mThreadPools == null || mThreadPools.isShutdown()) {
				return;
			}
			threadTask.mTaskListener = listener;
			Future<?> future = null;
			try {
				future = mThreadPools.submit(threadTask);
			} catch (RejectedExecutionException e) {
				return;
			}
			if (future == null) {
				return;
			}
			addToQueue(threadTask, future);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * �����̳߳�
	 */
	public static synchronized void onDestroy() {
		try {
			if (mThreadPool != null) {
				mThreadPool.destroy();
				mThreadPool = null;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void destroy() {
		try {
			for (Iterator<Entry<ThreadTask, Future<?>>> localIterator = this.queue
					.entrySet().iterator(); localIterator.hasNext();) {
				Entry<ThreadTask, Future<?>> localIteratorEntry = localIterator
						.next();
				ThreadTask key = localIteratorEntry.getKey();
				Future<?> localFuture = queue.get(key);
				try {
					if (localFuture != null) {
						localFuture.cancel(true);

					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			this.queue.clear();
			mThreadPools.shutdown();
		} catch (Throwable e) {

			e.printStackTrace();
		}
	}

	private synchronized boolean containKey(ThreadTask threadTask) {
		boolean isContain = false;
		try {
			isContain = queue.containsKey(threadTask);
		} catch (Throwable e) {
			 
			e.printStackTrace();
		}
		return isContain;
	}

	private synchronized void addToQueue(ThreadTask threadTask, Future<?> future) {

		try {
			queue.put(threadTask, future);
		} catch (Throwable e) {
			 
			e.printStackTrace();
		}
		 
	}

	private synchronized void removeFromQueue(ThreadTask threadTask,
			boolean isCancel) {
		try {
			Future<?> future = queue.remove(threadTask);
			if (isCancel && future != null) {
				future.cancel(true);
			}
		} catch (Throwable e) {
	 
			e.printStackTrace();
		}

	}

}
