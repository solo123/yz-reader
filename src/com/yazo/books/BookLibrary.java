package com.yazo.books;

public class BookLibrary {
	int list_size = 20;
	
	public BookListItem[] GetList(String list_type){
		BookListItem[] result = new BookListItem[list_size];
		for (int i=0; i<list_size; i++){
			result[i] = new BookListItem("List " + i, "value " + i);
		}
		return result;
	}
	
	public String GetContent(int book_id, int chapter_id, int page_id){
		String content = "Hello, world!!";
		return content; 
	}

}
