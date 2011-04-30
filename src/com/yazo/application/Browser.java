package com.yazo.application;

import com.yazo.books.*;
import javax.microedition.lcdui.*;

public class Browser extends Canvas {
	public BookManager book_manager;
	public HeaderZone header_zone;
	public MainZone main_zone;
	public MenuZone menu_zone;
	int width, height, header_height, menu_height;
	public String[] history;
	public int history_count;
	int up, down, fire, right, left, key_ok, key_lefttab, key_righttab;

	public Browser() {
		init();
		history = new String[10];
		history_count = 0;
		header_height = 20;
		menu_height = 20;
		width = getWidth();
		height = getHeight();
		book_manager = new BookManager();

		header_zone = new HeaderZone(width, header_height);
		main_zone = new MainZone(width, height - header_height - menu_height);
		menu_zone = new MenuZone(width, menu_height);
		header_zone.setColor(0x7c90b3, 0xFFFFFF);
		main_zone.setColor(0xdde4ec, 0x363636);
		menu_zone.setColor(0xc2c2c2, 0);
		gotoUrl("Home");
	}

	public void gotoUrl(String url) {
		main_zone.current_cmd = url;
		book_manager.getPage(url);
		book_manager.content.markPages(height - header_height - menu_height
				- 20);

		header_zone.setHeader(book_manager.header);
		main_zone.setContent(book_manager.content);
		menu_zone.setBrowseMenu();
		repaint();
	}

	protected void paint(Graphics g) {
		g.drawImage(header_zone.image, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.drawImage(main_zone.image, 0, header_height, Graphics.TOP
				| Graphics.LEFT);
		g.drawImage(menu_zone.image, 0, height, Graphics.BOTTOM | Graphics.LEFT);
		if (menu_zone.state > 0) {
			g.drawImage(menu_zone.menuShadowImage, 7, height - 21,
					Graphics.BOTTOM | Graphics.LEFT);
			g.drawImage(menu_zone.menuImage, 4, height - 21 - 3,
					Graphics.BOTTOM | Graphics.LEFT);
		}
	}

	public void keyReleased(int keyCode) {
		int action = getGameAction(keyCode);
		if (keyCode == 57) {
			new fullSrceen(main_zone.current_cmd);
		}
		System.out.println(" action:" + action + ", keycode:" + keyCode);
		if (menu_zone.state > 0) {
			menuKeyAction(keyCode);
		} else {
			mainKeyAction(keyCode);
		}
		repaint();
	}

	private void menuKeyAction(int keyCode) {
		int action = getGameAction(keyCode);
		if (keyCode == -1) {
			menu_zone.cursorUp();
		} else if (keyCode == -2) {
			menu_zone.cursorDown();
		} else if (keyCode == -3) {
			menu_zone.cursorLeft();
		} else if (keyCode == -4) {
			menu_zone.cursorRight();
		} else if (keyCode == -5) {
			// TODO: menu action, gotoUrl(main_zone.current_cmd);
		} else if (keyCode == -6) {
			menu_zone.activeMenu();
		} else if (keyCode == -7) {
			menu_zone.activeMenu();
		} else if (action == 0 && keyCode == 49) {
			menu_zone.activeMenu();
		} else if (action == 0 && keyCode == 51) {
			menu_zone.activeMenu();
		} else if (keyCode == 53 || action == 8) {
		}
	}

	private void mainKeyAction(int keyCode) {
		int action = getGameAction(keyCode);
		// if (keyCode == -1) {
		// main_zone.cursorUp();
		// } else if (keyCode == -2) {
		// main_zone.cursorDown();
		// } else if (keyCode == -3) {
		// main_zone.prevPage();
		// } else if (keyCode == -4) {
		// main_zone.nextPage();
		// } else if (keyCode == -5) {
		// if (main_zone.next_cmd != null) {
		// history[history_count++] = main_zone.current_cmd;
		// gotoUrl(main_zone.next_cmd);
		// }
		//
		// } else if (keyCode == -6) {
		// menu_zone.activeMenu();
		// } else if (keyCode == -7) {
		// // TODO: back or quick
		// if (history_count > 0) {
		// gotoUrl(history[--history_count]);
		// }
		//
		// } else if (action == 0 && keyCode == 49) {
		// menu_zone.activeMenu();
		// } else if (action == 0 && keyCode == 51) {
		// if (history_count > 0) {
		// gotoUrl(history[--history_count]);
		// }
		// } else if (keyCode == 53 || action == 8) {
		// if (main_zone.next_cmd != null) {
		// history[history_count++] = main_zone.current_cmd;
		// gotoUrl(main_zone.next_cmd);
		// }
		// } else if (keyCode == 42) {
		// new fullSrceen(main_zone.current_cmd);
		// }
		switch (action) {
		case 1:
			main_zone.cursorUp();
			break;
		case 6:
			main_zone.cursorDown();
			break;
		case 2:
			main_zone.prevPage();
			break;
		case 5:
			main_zone.nextPage();
			break;
		case 8:
			if (main_zone.next_cmd != null) {
				history[history_count++] = main_zone.current_cmd;
				gotoUrl(main_zone.next_cmd);
			}
			break;
		case 0:
			if (keyCode == 51 || keyCode == -7) {
				if (history_count > 0) {
					gotoUrl(history[--history_count]);
				}
			} else {
				menu_zone.activeMenu();
			}
			break;
		}

	}

	public void init() {
		right = getKeyCode(RIGHT);
		left = getKeyCode(LEFT);
		up = getKeyCode(UP);
		down = getKeyCode(DOWN);
		fire = getKeyCode(FIRE);
		// key_ok=getKeyCode(KEY_OK);
	}
}
