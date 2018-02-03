import java.lang.*;
import java.util.*;
public class Book {
	private String author,title,publisher, isbn;
	private int year;
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
	
}