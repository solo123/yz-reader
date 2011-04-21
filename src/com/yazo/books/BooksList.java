package com.yazo.books;

public class BooksList {
	String[] book_list;
	String[] value_list;
	int list_size = 20;
	public int current_item = -1;
	
	public BooksList(String list_name){
		book_list = new String[list_size];
		value_list = new String[list_size];
	}
}
