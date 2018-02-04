import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.lang.*;

public class CsapProtocol implements Runnable{
	private static final int WAITING = 0;
	private static final int SENT = 1;
	private static final int SENTPACK = 2;
	private static final int ANOTHER = 3;
	
	private static final int OK = 100;
    private static final int DATAEXISTS = 200;
    private static final int DATANOTEXIST = 201;
    private static final int BADREQUEST = 300;
    private static final int NOTFOUND = 301;
    private static final int INVALIDENTRY = 302;
    private static final int INTERNALERROR = 400;
    
    
	private int state = WAITING;
	
	private static ArrayList <Book> bib = null;
	
	protected Socket client=null;
	public CsapProtocol(Socket client, ArrayList<Book> bib) {
		this.client=client;
		this.bib = bib;
	}
	
	public void run() {
		try {
			processRequest();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void processRequest() throws Exception{
		DataInputStream input = new DataInputStream(client.getInputStream());
		DataOutputStream output = new DataOutputStream(client.getOutputStream());
		                 String instring;

		@SuppressWarnings("deprecation")
		String requestLine=input.readLine();
		// Convert the request message into an array
		String requestData[] = requestLine.split(",");
		

		System.out.println(requestData.length);
		System.out.println("------");
		
		// the request type
		String requestType = requestData[0];
		
		// Organize the request attributes into a nice data structure		
		Map<String, String> attrMap = new HashMap<String, String>();		
		
		for(int i = 1; i < requestData.length; i++) {
			String dataLine[] = requestData[i].split(" ");
			
			System.out.println(requestData[i]);
			
			// get the key from the first part of the line 
			String attrKey = dataLine[0];
			
			// add the rest of the elements from the line to the value
			String attrVal = "";
			for(int j = 1; j < dataLine.length; j++) {
				if(j != 1) {
					attrVal += " ";	
				}
				attrVal += dataLine[j];
			}
			
			// add the attr key and value to the array
			attrMap.put(attrKey, attrVal);
		}
				
		if(requestType.equals("SUBMIT")) { // submit a new book
			// first check if a book with that ISBN already exists
			if(findBooks(null, null, null, 0, attrMap.get("ISBN")).size() > 0) {
				output.writeBytes("REDIRECTION " + DATAEXISTS + ".-newline-A book with that ISBN already exists.");
			} else {
				int bookYear = 0	;
				if(attrMap.get("YEAR") != null) {
					bookYear = Integer.parseInt(attrMap.get("YEAR"));
				}
				Book newBook = new Book(attrMap.get("AUTHOR"), attrMap.get("TITLE"), attrMap.get("PUBLISHER"), bookYear, attrMap.get("ISBN"));
				bib.add(newBook);
				
				output.writeBytes("SUCCESS " + OK + ".-newline-The book was successfully added.");
			}
		} else if(requestType.equals("UPDATE")) {
			ArrayList<Book> foundBooks = findBooks(null, null, null, 0, attrMap.get("ISBN"));
			
			// if the book is not found
			if(foundBooks.size() == 0) {
				output.writeBytes("REDIRECTION " + DATANOTEXIST + ".-newline-A book with that ISBN doesn't exist.");
			} else {
				// update the books attributes
				if(attrMap.get("AUTHOR") != null) {
					foundBooks.get(0).updateAuthor(attrMap.get("AUTHOR"));					
				}
				if(attrMap.get("TITLE") != null) {
					foundBooks.get(0).updateTitle(attrMap.get("TITLE"));					
				}
				if(attrMap.get("PUBLISHER") != null) {
					foundBooks.get(0).updatePublisher(attrMap.get("PUBLISHER"));					
				}
				if(attrMap.get("YEAR") != null) {
					int bookYear = 0	;
					if(attrMap.get("YEAR") != null) {
						bookYear = Integer.parseInt(attrMap.get("YEAR"));
					}
					foundBooks.get(0).updateYear(bookYear);					
				}
				output.writeBytes("SUCCESS " + OK + ".-newline-The book was successfully updated.");
			}
			
		} else if(requestType.equals("GET")) {
			System.out.print(requestData[1]);
			String bibString = "SUCCESS 100.-newline-";
			if(requestData[1].equals("ALL")) {
				// if there are no books found
				if(bib.size() == 0) {
					bibString += "No books found.";
				} else {
					for(int i=0;i<bib.size();i++) {
						bibString += bib.get(i).getString() + "-newline-";
					}
				}
				output.writeBytes(bibString);
			} else {
				int bookYear = 0	;
				if(attrMap.get("YEAR") != null) {
					bookYear = Integer.parseInt(attrMap.get("YEAR"));
				}
				ArrayList<Book> foundBooks = findBooks(attrMap.get("AUTHOR"), attrMap.get("TITLE"), attrMap.get("PUBLISHER"), bookYear, attrMap.get("ISBN"));
				// if there are no books found
				if(foundBooks.size() == 0) {
					bibString += "No books found.";
				} else {
					for(int i=0;i<foundBooks.size();i++) {
						bibString += foundBooks.get(i).getString() + "-newline-";
					}
				}
				output.writeBytes(bibString);
			}
		} else if(requestType.equals("REMOVE")) {
			// find books to remove
			int bookYear = 0	;
			if(attrMap.get("YEAR") != null) {
				bookYear = Integer.parseInt(attrMap.get("YEAR"));
			}
			int removeCount = removeBooks(attrMap.get("AUTHOR"), attrMap.get("TITLE"), attrMap.get("PUBLISHER"), bookYear, attrMap.get("ISBN"));

			// if the book is not found
			if(removeCount == 0) {
				output.writeBytes("REDIRECTION " + DATANOTEXIST + ".-newline-No books could be found with the requested parameters.");
			} else {
				output.writeBytes("SUCCESS " + OK + ".-newline-" + removeCount + " book(s) removed successfully.");
			}
		} else {
			output.writeBytes("ERROR " + NOTFOUND + ".-newline-The requested method could not be found.");
		}
		
		input.close();
		output.close();
		printBooks();
		System.out.println("DONE\n");
	}
	
	// returns a set of books from the BOOKS array
	private static ArrayList<Book> findBooks(String author, String title, String publisher, int year, String isbn) {
		ArrayList <Book> found = new ArrayList<Book>();
		
		// go through each book
		for(int i = 0; i < bib.size(); i++) {
			Book b = bib.get(i);
			
			// check if the book meets the filter requirments
			if(author != null && !b.getAuthor().equals(author)) {
				continue;
			} else if(title != null && !b.getTitle().equals(title)) {
				continue;
			} else if(publisher != null && !b.getPublisher().equals(publisher)) {
				continue;
			} else if(year != 0 && b.getYear() != year) {
				continue;
			} else if(isbn != null && !b.getIsbn().equals(isbn)) {
				continue;
			}
			
			// if the book passed all the checks append it to our found list
			found.add(b);
		}
		
		return found;
	}
	
	// returns the number of books that were removed
	private static int removeBooks(String author, String title, String publisher, int year, String isbn) {
		int removeCount = 0;
		// go through each book
		for(int i = 0; i < bib.size(); i++) {
			Book b = bib.get(i);
			
			// check if the book meets the filter requirments
			if(author != null && !b.getAuthor().equals(author)) {
				continue;
			} else if(title != null && !b.getTitle().equals(title)) {
				continue;
			} else if(publisher != null && !b.getPublisher().equals(publisher)) {
				continue;
			} else if(year != 0 && b.getYear() != year) {
				continue;
			} else if(isbn != null && !b.getIsbn().equals(isbn)) {
				continue;
			}
			
			// if the book passed all the checks remove it
			bib.remove(i);
			removeCount++;
		}
		
		return removeCount;
	}
	public static void printBooks() {
		int i=0;
		for(i=0;i<bib.size();i++) {
			System.out.println(bib.get(i).getString());
		}
			
	}
	
		public boolean isbnCmp(String isbn, String clientIsbn) {
	 		boolean isEqual = false;
	 		if(isbn.equals(clientIsbn)) {
	 			isEqual=true;
	 		}
	 		return isEqual;
	 	}
	
}