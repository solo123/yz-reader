package com.yazo.ui;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public class UiContainer {
	private Vector controls = new Vector(10);
	public void addControl(UiControl control){
		controls.addElement(control);
	}
	public void paint(Graphics g){
// #ifdef DBG		
		System.out.println("container paint.");
// #endif
		Enumeration cs = controls.elements();
	    while(cs.hasMoreElements()){
	    	((UiControl)cs.nextElement()).paint(g);
	    }
	}
}
