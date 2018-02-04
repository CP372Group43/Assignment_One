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
	
	private static ArrayList <Book> bib = new ArrayList<Book>();
	
	protected Socket client=null;
	public CsapProtocol(Socket client) {
		this.client=client;
	}
	
	public void run() {
		try {
			processRequest();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void processRequest() throws Exception{
	//	InputStream input = client.getInputStream();
		//OutputStream output = client.getOutputStream();
	//	BufferedReader br = new BufferedReader(new InputStreamReader(input));
	//	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
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
		
		System.out.println("Year: " + attrMap.get("YEAR"));
		
		if(requestType.equals("SUBMIT")) { // submit a new book
			// first check if a book with that ISBN already exists
			if(findBooks(null, null, null, 0, attrMap.get("ISBN")).size() > 0) {
				output.writeBytes("REDIRECTION 200. A book with that ISBN already exists.");
			} else {
				int bookYear = 0	;
				if(attrMap.get("YEAR") != null) {
					bookYear = Integer.parseInt(attrMap.get("YEAR"));
				}
				Book newBook = new Book(attrMap.get("AUTHOR"), attrMap.get("TITLE"), attrMap.get("PUBLISHER"), bookYear, attrMap.get("ISBN"));
				bib.add(newBook);
				
				output.writeBytes("SUCCESS 100. The book was successfully added.");
			}
		} else if(requestType.equals("UPDATE")) {
			ArrayList<Book> foundBooks = findBooks(null, null, null, 0, attrMap.get("ISBN"));
			
			// if the book is not found
			if(foundBooks.size() == 0) {
				output.writeBytes("REDIRECTION 201. A book with that ISBN doesn't exist.");
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
				output.writeBytes("SUCCESS 100. The book was successfully updated.");
			}
			
		} else if(requestType == "GET") {
			
		} else if(requestType == "REMOVE") {
			
		}
		
		input.close();
		output.close();
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
	
}