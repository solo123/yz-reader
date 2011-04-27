package com.yazo.thread;

/**
 * 定义工作任务的回掉
 *
 */
public interface WaitCallback {
	public void execute(Object state);
}
