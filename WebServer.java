package Assignment_One;
import java.io.* ;
import java.net.* ;
import java.util.* ;

public class WebServer implements Runnable{
	ArrayList <Book> bib = null;
	private Thread currentThread = null;
	final int port;
	ServerSocket serverSocket = null;
	public boolean isRunning = true;
	public WebServer(int port) {
		this.port=port;
	}
    public void run() {
    	
    this.currentThread = Thread.currentThread();
	try {
		this.serverSocket =  new ServerSocket(port);
	}	catch(IOException e) {
		System.err.format("Could not listen on port : %d",port);
		System.exit(1);
	}
	
	while (isRunning) {
		Socket clientSocket = null;
		try {
	        clientSocket = serverSocket.accept();
	        new Thread(new CsapProtocol(clientSocket)).start();;
	    } catch (IOException e) {
	        System.err.println("Accept failed.");
	        System.exit(1);
	    }
		
	    // Start the thread.
	}
    }
 
    
    
}

