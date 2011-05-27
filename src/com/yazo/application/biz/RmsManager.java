package com.yazo.application.biz;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

public class RmsManager {
	private RecordStore rs = null;
	private static RmsManager instance = null;
	private RmsManager(){
		
	}
	public static RmsManager getInstance(){
		if(instance==null) instance = new RmsManager();
		return instance;
	}
	/*
	 * 保存配置到RMS（仅保存200>id>100的配置信息）
	 */
	public void save(Config config){
		if (config==null) return;
		byte[] buf = config.getBytes();
		if (buf==null || buf.length<1) return;
		
		try {
			rs = RecordStore.openRecordStore("CONFIG", true);
			if(rs.getNumRecords()<1){
				rs.addRecord(buf, 0, buf.length);
			} else {
				rs.setRecord(1, buf, 0, buf.length);
			}
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void load(Config config){
		try {
			rs = RecordStore.openRecordStore("CONFIG", true);
			if(rs.getNumRecords()>0){
				byte[] buf = rs.getRecord(1);
				config.loadBytes(buf);
			}
			rs.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
