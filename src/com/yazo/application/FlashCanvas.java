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

public class FlashCanvas extends Canvas {
	private int width;
	private int height;
	private int h;
	MIDlet midlet;
	Image bar;
	Image loading;
	Image logo;
	private String error = "请选择使用/CMWAP";

	private int x;
	private int barW;
	private int barX = x;
	private int w1;
	private int barY = 0;
	private Timer timer;
	private int time = 1000 / 12;
	private String midletname = "";

	public FlashCanvas(MIDlet midlet) {
		this.setFullScreenMode(true);
		this.midlet = midlet;
		width = this.getWidth();
		height = this.getHeight();

		try {
			midletname = midlet.getAppProperty("MIDlet-Name");
			bar = Image.createImage("/bar.png");
			loading = Image.createImage("/loading.png");
			logo = Image.createImage("/book.png");
			x = bar.getWidth();
			barW = bar.getWidth();
			barX = x;
			w1 = this.getWidth() - (x << 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		startTimer();
	}

	Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
			Font.SIZE_LARGE);
	private boolean right = true;

	protected void paint(Graphics g) {
		int font_height = font.getHeight();
		System.out.println("font_height:" + font_height);
		g.setColor(255, 255, 255);
		g.fillRect(0, 0, width, height);
		g.setFont(font);
		g.setColor(0);
		g.drawImage(logo, this.getWidth() / 2, this.getHeight() / 5,
				Graphics.TOP | Graphics.HCENTER);
		g.drawString("欢迎使用" + "怡红书苑", width >> 1, height - font_height * 2
				- height / 8, Graphics.TOP | Graphics.HCENTER);
		g.drawString(error, width >> 1, height - font_height - height / 8,
				Graphics.TOP | Graphics.HCENTER);
		barY = height / 4 + h + 60;
		this.drawFlash(g, bar, x, barY + this.getHeight() / 2, w1 + 20,
				bar.getHeight());// ��������
		this.drawFlash(g, loading, barX + 3, barY + this.getHeight() / 2,
				barW << 1, loading.getHeight());// ��������
		if (right) {
			if (barX < w1 - barW * 2) {
				barX += barW;
			} else {
				barX -= barW;
				right = false;
			}
		} else {
			if (barX > x) {
				barX -= barW;
			} else {
				barX += barW;
				right = true;
			}
		}
	}

	public void setError(String error) {
		this.error = error;
		repaint();
	}

	protected void keyPressed(int keyCode) {
		super.keyPressed(keyCode);
	}

	protected void keyReleased(int keyCode) {
		super.keyReleased(keyCode);
	}

	boolean live = false;

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

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		bar = null;
		loading = null;
	}

	public void drawFlash(Graphics g, Image img, int x, int y, int width,
			int height) {
		int blockWidth = img.getWidth() / 2;
		int blockHeight = img.getHeight() / 2;
		g.setClip(x, y, blockWidth, blockHeight);
		g.drawImage(img, x, y, Graphics.LEFT | Graphics.TOP);
		int length = width / blockWidth - 2;
		for (int i = 0; i < length; i++) {
			g.setClip(x + blockWidth * (i + 1), y, blockWidth, blockHeight);
			g.drawImage(img, -blockWidth + x + blockWidth * (i + 2), y,
					Graphics.LEFT | Graphics.TOP);
		}
	}
<<<<<<< HEAD
}
=======
}
>>>>>>> 8c5d2b6c0ebd88b8d93bf4c924fc4633115c93c1
