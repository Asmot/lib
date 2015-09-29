package com.zxy.libs.threads;



/**
 * 
 * 与线程池配合使用
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
	 * 线程具体执行逻辑
	 */
	public abstract void runTask();

	/**
	 * 线程取消（最终调用的是Future<?>的cancel(true)方法 ）
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
