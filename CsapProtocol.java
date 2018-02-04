package Assignment_One;
import java.net.*;
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
	
	private static Book[] BOOKS = new Book[1000];
	
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
		output.writeBytes("we are okay");
		
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
			// TODO: first check if a book with that ISBN already exists
			
			int bookYear = 0	;
			if(attrMap.get("YEAR") != null) {
				bookYear = Integer.parseInt(attrMap.get("YEAR"));
			}
			Book newBook = new Book(attrMap.get("AUTHOR"), attrMap.get("TITLE"), attrMap.get("PUBLISHER"), bookYear, attrMap.get("ISBN"));
			BOOKS[0] = newBook;
		//	bw.write("we are okay");
	///		bw.flush();
	//		
		} else if(requestType == "UPDATE") {
			
		} else if(requestType == "GET") {
			
		} else if(requestType == "REMOVE") {
			
		}
		
		input.close();
		output.close();
	}
	
	// returns a set of books from the BOOKS array
	private static void findBooks(String author, String title, String publisher, int year, String isbn) {
		
	}
	
}