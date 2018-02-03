import java.io.* ;
import java.net.* ;
import java.util.* ;

public class WebServer implements Runnable{
	ArrayList <Book> bib = null;
	private Thread currentThread = null;
	private int port;
	ServerSocket serverSocket = null;
	public boolean isRunning = true;
	
	public static void main(String[] args) {
		new WebServer(5555);
	}
	
//	
	public WebServer(int port) {
		this.port=port;
		
		this.run();
	}

	
	
    public void run() {
    	
    	System.out.print("Yoo");
    	
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
		        new Thread(new CsapProtocol(clientSocket)).start();
		        
		        System.out.print("Yoooo");
		        
		    } catch (IOException e) {
		        System.err.println("Accept failed.");
		        System.exit(1);
		    }
			
		    // Start the thread.
		}
    }
 
    
    
}
