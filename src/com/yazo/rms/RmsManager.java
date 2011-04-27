package com.yazo.rms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;

import com.yazo.util.ServiceData;

public class RmsManager {
	final static String SERVICE="service";
	final static String USERID="user_id";
	final static String CHARGES="charges";//收费章节
	/**
	 * 保存USERid
	 * @param userid
	 */
	public static void saveUserID(String userid){
		DataRMS rms=new DataRMS();
		rms.init(USERID);
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeUTF(userid);
			rms.setData(1, bos.toByteArray());
			
			dos.close();
			bos.close();
			rms.closeRMS();
			
		}catch(Exception e){
			rms.closeRMS();
		}
	}
	/**
	 * 获取userid
	 * @return
	 */
	public static String getUserID(){
		String userid="";
		
		DataRMS rms=new DataRMS();
		rms.init(USERID);
		try{
			if(rms.getIndexSum()==0){
				rms.closeRMS();
				return "";
			}
			
			byte[] data=rms.getData(1);
			ByteArrayInputStream bais=new ByteArrayInputStream(data);
			DataInputStream dis=new DataInputStream(bais);
			userid=dis.readUTF();
			dis.close();
			bais.close();
			rms.closeRMS();
		}catch(Exception e){
			rms.closeRMS();
			return userid;
		}
		return userid;
	}
	
	
	
	/**
	 * 保存，设置
	 * @param data
	 */
	public synchronized static void saveServiceSetting(ServiceData data){
		DataRMS rms=new DataRMS();
		rms.init(SERVICE);
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeUTF(data.OPERATE);
			dos.writeUTF(data.SERVICE);
			dos.writeUTF(data.FEECODE);
			dos.writeUTF(data.MSG1);
			dos.writeUTF(data.MSG2);
			dos.writeUTF(data.MSG3);
			dos.writeUTF(data.MSG4);
			dos.writeUTF(data.MSG5);
			rms.setData(1, bos.toByteArray());
			dos.close();
			bos.close();
			rms.closeRMS();
		}catch(Exception e){
			e.printStackTrace();
			rms.closeRMS();
		}
		
	}
	
	/**
	 * 保存内容id  章节id
	 * @param contentid
	 * @param chapterid
	 */
	public static void saveChanges(String contentid,String chapterid){
		DataRMS rms=new DataRMS();
		rms.init(CHARGES);
		try{
					
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeUTF(contentid);
			dos.writeUTF(chapterid);
			rms.addData(bos.toByteArray());
			dos.close();
			bos.close();
				
			rms.closeRMS();
		}catch(Exception e){
			e.printStackTrace();
			rms.closeRMS();
			
		}
	}
	/**
	 * 所有订购内容+章节id
	 * @return
	 */
	public static Vector getAllChanges(){
		Vector v=null;
		DataRMS rms=new DataRMS();
		rms.init(CHARGES);
		try{
			int sum=rms.getIndexSum();
			if(sum==0){
				rms.closeRMS();
				return null;
			}
			v=new Vector();
			for(int i=1;i<=sum;i++){
				byte[] data=rms.getData(i);
				ByteArrayInputStream bais=new ByteArrayInputStream(data);
				DataInputStream dis=new DataInputStream(bais);
				v.addElement(dis.readUTF());
				v.addElement(dis.readUTF());
				dis.close();
				bais.close();
			}
			rms.closeRMS();
		}catch(Exception e){
			e.printStackTrace();
			rms.closeRMS();
		}
		return v;
	}
}
