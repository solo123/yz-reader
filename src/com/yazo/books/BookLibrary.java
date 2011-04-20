package com.yazo.books;

public class BookLibrary {
	int list_size = 20;
	
	public String[] GetList(int list_type){
		String[] result = new String[list_size];
		for (int i=0; i<list_size; i++){
			result[i] = "List no:" + i;
		}
		return result;
	}
	
	public String GetContent(int book_id, int chapter_id, int page_id){
		String content = "Hello, world!!";
		return content; 
	}

}
