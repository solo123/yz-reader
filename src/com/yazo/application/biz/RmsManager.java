package com.yazo.application.biz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.RecordStore;

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
	
	/**
	 * 保存USERid
	 * @param userid
	 */
	public  void saveUserID(String userid){
		
		try{
			rs = RecordStore.openRecordStore("user_id", true);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeUTF(userid);
			byte[] buf = bos.toByteArray();
			int num = rs.getNumRecords();
			if(1>num)
			{
				byte[] b1 = {(byte)0};
				for(;num<1;num++)
				{
					rs.addRecord(b1, 0, b1.length);
				}
			}if(rs.getNumRecords()<1){
				rs.addRecord(buf, 0, buf.length);
			} else {
				rs.setRecord(1, buf, 0, buf.length);
			}
			
			dos.close();
			bos.close();
			rs.closeRecordStore();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 获取userid
	 * @return
	 */
	public  String getUserID(){
		String userId="";
		
		
		try{
			rs = RecordStore.openRecordStore("user_id", true);
			if(rs.getNumRecords()>0){
				byte[] data=rs.getRecord(1);
				ByteArrayInputStream bais=new ByteArrayInputStream(data);
				DataInputStream dis=new DataInputStream(bais);
				userId=dis.readUTF();
				dis.close();
				bais.close();
				rs.closeRecordStore();
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return userId;
	}
}
