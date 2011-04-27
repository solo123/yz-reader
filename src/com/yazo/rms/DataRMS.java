package com.yazo.rms;

import javax.microedition.rms.RecordStore;


public class DataRMS {

	/** 记录仓库 */
	private RecordStore rs;
	
	/**
	 * 初始化RMS
	 * 
	 * @param size	RMS大小
	 * @param name	RMS名称
	 * @return	初始化成功返回true 否则返回false
	 */
	public boolean init(String name)
	{
		try {
			rs = RecordStore.openRecordStore(name, true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 修改数据
	 * @param id
	 * @param data
	 * @return
	 */
	public boolean setData(int id, byte[] data)
	{
		try {
			int num = rs.getNumRecords();
			if(id>num)
			{
				byte[] b1 = {(byte)0};
				for(;num<id;num++)
				{
					rs.addRecord(b1, 0, b1.length);
				}
			}
			rs.setRecord(id, data, 0, data.length);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	
	}
	/**
	 * 添加数据
	 * @param data
	 * @return
	 */
	public boolean addData(byte[] data){
		try{
			rs.addRecord(data, 0, data.length);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int getIndexSum(){
		int sum=0;
		try{
			sum=rs.getNumRecords();
		}catch(Exception e){
			e.printStackTrace();
		}
		return sum;
	}
	
	/**
	 * 获取指定ID的记录
	 * 
	 * @param id	记录id
	 * @return
	 */
	public byte[] getData(int id)
	{
		byte[] data = null;
		try {
			if(id>rs.getNumRecords())
			{
				return null;
			}
			data = rs.getRecord(id);			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}
	

	/**
	 * 关闭记录仓库
	 * @return
	 */
	public boolean closeRMS()
	{
		try {
			if(rs!=null)
			{
				rs.closeRecordStore();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
