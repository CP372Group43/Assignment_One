package Assignment_One;
import java.lang.*;
import java.util.*;
public class Book {
	private String author,title,publisher, isbn;
	private Integer year = new Integer(-1);
	public Book(String author, String title, String publisher, int year, String isbn) {
		this.author=author;
		this.title=title;
		this.publisher=publisher;
		this.year=year;
		this.isbn=isbn;
	}
	public String getAuthor() {
		return this.author;
	}
	public void updateAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return this.title;
	}
	public void updateTitle(String title) {
		this.title = title;
	}
	public String getPublisher() {
		return this.publisher;
	}
	public void updatePublisher(String publisher) {
		this.publisher=publisher;
	}
	public int getYear() {
		return this.year;
	}
	public void updateYear(int year) {
		this.year=year;
	}
	public String getIsbn() {
		return this.isbn;
	}
	public String getString() {
		String book = "";
		book+=String.format("ISBN = {%s}-newline-", this.isbn);
		if(this.author!=null) {
		book+=String.format("AUTHOR = {%s}-newline-", this.author);
		}
		if(this.title!=null) {
			book+=String.format("TITLE = {%s}-newline-", this.title);
		}
		if(this.publisher!=null) {
			book+=String.format("PUBLISHER = {%s}-newline-", this.publisher);
		}
		if(this.year!=0) {
			book+=String.format("YEAR = {%d}-newline-", this.year);
		}
		return book;
	}
	
}