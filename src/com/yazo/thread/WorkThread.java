package com.yazo.thread;


/**
 * 线程池的工作线程
 * 
 */
class WorkThread extends Thread {
	private boolean isIdle;
	private WaitCallback callback;
	private Object state;
	private Object lock = new Object();
	private boolean isExit = false; //线程是否退出的标记

	public WorkThread() {
		this.isIdle = true;
	}

	public void run() {

		while (!isExit) {
			if (this.isIdle && this.callback != null) {

				try {
					this.isIdle = false;
					this.callback.execute(this.state);
					if(this.getPriority()==Thread.MAX_PRIORITY)	
				    {
						int size=ThreadPool.pooledWorkThreads.size();
						for (int i = 0; i < size; i++) {  //高等级运行完后回复正常等级
							if (!((WorkThread) ThreadPool.pooledWorkThreads.elementAt(i)).IsIdle()) {
								((WorkThread) ThreadPool.pooledWorkThreads.elementAt(i)).setPriority(WorkThread.NORM_PRIORITY);
							}
						}
				    }
				} catch (Exception ex) {
					ex.printStackTrace();
				}
		         this.isIdle = true;
				//System.out.println(this.callback + " exec complete!");
			}
           // 使线程进入休眠状态
			synchronized (lock) {
				try {
					lock.wait();
					this.isIdle = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 工作线程是否忙碌
	 * 
	 * @return
	 */
	boolean IsIdle() {
		return this.isIdle && !this.isExit;
	}

	void setWorkTaskData(WorkTaskData data) {
		if (data != null) {
			this.callback = data.Callback;
			this.state = data.State;

			// 唤醒休眠的线程
			synchronized (lock) {
				lock.notify();
			}
		}
	}

	/**
	 *退出等待循环
	 */
	void exit() {
		this.isExit = true;

		if (this.isIdle) {
			// 如果线程空闲，则唤醒线程，以便退出等待循环
			synchronized (lock) {
				this.lock.notify();
			}
		}
	}
}
