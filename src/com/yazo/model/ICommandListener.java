package com.yazo.model;

public interface ICommandListener {
	public void execute_command(int command, Object data);
}