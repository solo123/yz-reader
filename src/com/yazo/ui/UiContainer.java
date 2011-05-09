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
		Enumeration cs = controls.elements();
	    while(cs.hasMoreElements()){
	    	System.out.println("paint control.");
	    	((UiControl)cs.nextElement()).paint(g);
	    }
	}
}
