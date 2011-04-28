package com.yazo.application;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;



public class FlashCanvas extends Canvas{
	private int width;
	private int height;
	private int h;
	MIDlet midlet;
	Image bar;
	Image loading;
	
	private String error="请选择移动梦网/CMWAP";
	
	private int x;
	private int barW;
	private int barX=x;
	private int w1;
	private int barY=0;
	private Timer timer;
	private int time=1000/12;
	private String midletname="";
	
	public FlashCanvas(MIDlet midlet){
		this.setFullScreenMode(true);
		this.midlet = midlet;
		width=this.getWidth();
		height=this.getHeight();
		
		try{
			midletname=midlet.getAppProperty("MIDlet-Name");
			bar=Image.createImage("/bar.png");
			loading=Image.createImage("/loading.png");
			x=bar.getWidth();
			barW=bar.getWidth();
			barX=x;
			w1=this.getWidth()-(x<<1);
		}catch(Exception e){
			e.printStackTrace();
		}
		startTimer();
	}
	Font font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	private boolean right=true;
	protected void paint(Graphics g) {
		g.setColor(255, 255, 255);
		g.fillRect(0, 0, width, height);
//		g.drawImage(Images[randomIndex], width>>1, height/4, Graphics.TOP|Graphics.HCENTER);
		g.setFont(font);
		g.setColor(0);
		g.drawString("欢迎使用"+"怡红书苑", width>>1, height/4+h+10, Graphics.TOP|Graphics.HCENTER);
		
		g.drawString(error, width>>1, height/4+h+40, Graphics.TOP|Graphics.HCENTER);
	
		barY=height/4+h+60;
		//ImageDiv.drawJiuGong(g,bar, x, barY, w1, bar.getHeight());//画背景条
		//ImageDiv.drawJiuGong(g, loading, barX+5, barY, barW<<1, loading.getHeight());//画滚动条
	if(right){
			
			if(barX<w1-barW*2){
				barX+=barW;
				
			}else{
				barX-=barW;
				right=false;
			}
		}else{
			if(barX>x){
				barX-=barW;
			}else{
				barX+=barW;
				right=true;
			}
		}
	}
	public void setError(String error){
		this.error=error;
		repaint();
	}
	protected void keyPressed(int keyCode) {
		super.keyPressed(keyCode);
	}

	protected void keyReleased(int keyCode) {
		super.keyReleased(keyCode);
	}
//	public void commandAction(Command c, Displayable d) {
//		if(c==exit){
//			midlet.quit();
//		}
//	}
//	int randomIndex=0;
//	int timertask=0;
	boolean live=false;
	private class SpinnerTask extends TimerTask {
		public void run() {
			repaint();
			/*timertask++;
			if(timertask%20==0){
				live=true;
				randomIndex++;
				if(randomIndex>4){
					randomIndex=0;
				}
			}*/
		}
	}
	public void startTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new SpinnerTask(), 100, time);
			// System.out.print("run");
		}
	}
	
	public void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		bar=null;
		loading=null;
	}
	
	

}
