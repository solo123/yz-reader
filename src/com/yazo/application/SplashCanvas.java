package com.yazo.application;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Loader;

public class SplashCanvas extends Canvas{
	private int width;
	private int height;
	private String error="请选择移动梦网/CMWAP";
	private Timer timer;
	private int time=1000/12;
	private MainMIDlet midlet;
	private Image splash_image;
	private int c1,c2,c3, cw;
	private int retry_times = 0;
	
	public SplashCanvas(MainMIDlet midlet){
		this.setFullScreenMode(true);
		width=this.getWidth();
		height=this.getHeight();
		this.midlet = midlet;
		c1 = 0xaaaaaa;
		c2 = 0x333333;
		c3 = cw = 0;
		splash_image = null;
		
		try{
			splash_image = Image.createImage("/book_icon.png");
		}catch(Exception e){}
		startTimer();
	}
	public void retryNetwork(){
		retry_times++;
	}
	protected void paint(Graphics g) {
		Font font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		int font_height = font.getHeight();
		int posx = width/2;
		int posy = height/3;
		g.setColor(0xfff5c5);
		g.fillRect(0, 0, width, height);
		
		if (splash_image!=null) 
			g.drawImage(splash_image, posx, posy, Graphics.VCENTER|Graphics.HCENTER);
		
		posy += splash_image.getHeight()/2 + font_height;
		g.setFont(font);
		g.setColor(0);
		g.drawString("欢迎使用"+Configuration.APP_NAME, posx, posy, Graphics.TOP|Graphics.HCENTER);
		posy += font_height + font_height/4;
		g.drawString(error, posx, posy, Graphics.TOP|Graphics.HCENTER);
		int barw = font.stringWidth(error);
		
		posx = (width-barw)/2;
		posy += font_height+font_height;
		cw += 2;
		if (cw>barw){
			c3 = c1;
			c1 = c2;
			c2 = c3;
			cw = 0;
		}
		g.setColor(c1);
		g.fillRect(posx, posy, barw, font_height);
		g.setColor(c2);
		g.fillRect(posx, posy, cw, font_height);
		
		if (retry_times > 0 ){
			posy += font_height +font_height;
			g.drawString("连接网络错误，重试第"+retry_times+"次", width/2, posy, Graphics.TOP|Graphics.HCENTER);
		}
		g.drawString("退出程序", width-4, height, Graphics.BOTTOM|Graphics.RIGHT);
		
	}
	public void setError(String error){
		this.error=error;
		repaint();
	}

	protected void keyReleased(int keyCode) {
		if (keyCode==-7){
			midlet.quit();
		}
	}

	private class SpinnerTask extends TimerTask {
		public void run() {
			repaint();
		}
	}
	public void startTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new SpinnerTask(), 100, time);
		}
	}
	
	public void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		splash_image = null;
	}
	
	

}
