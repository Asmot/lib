package com.zxy.libs.threads;



/**
 * 
 * ���̳߳����ʹ��
 * 
 */
public abstract class ThreadTask implements Runnable {

	OnThreadTaskListener mTaskListener;

	@Override
	public final void run() {
		try {
			if (mTaskListener != null) {
				mTaskListener.running(this);
			}

			if (Thread.interrupted()) {
				return;
			}
			runTask();
			if (Thread.interrupted()) {
				return;
			}
			if (mTaskListener != null) {
				mTaskListener.runOver(this);
			}
		} catch (Throwable e) {
			 
			e.printStackTrace();
		}
	}

	/**
	 * �߳̾���ִ���߼�
	 */
	public abstract void runTask();

	/**
	 * �߳�ȡ�������յ��õ���Future<?>��cancel(true)���� ��
	 */
	public final void cancelTask() {
		try {
			if (mTaskListener != null) {
				mTaskListener.cancelTask(this);
			}
		} catch (Throwable e) {
		 
			e.printStackTrace();
		}
	}

	interface OnThreadTaskListener {
		void running(ThreadTask threadTask);

		void runOver(ThreadTask threadTask);

		void cancelTask(ThreadTask threadTask);
	}

	void setTaskListener(OnThreadTaskListener listener) {
		this.mTaskListener = listener;
	}

}
