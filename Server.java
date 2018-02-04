package Assignment_One;
import java.io.* ;
import java.net.* ;
import java.util.* ;

public class Server implements Runnable{
	static ArrayList <Book> bib = new ArrayList<Book>();
	private int port;
	ServerSocket serverSocket = null;
	public boolean isRunning = true;
	
	public static void main(String[] args) {
		new Server(Integer.parseInt(args[0]));
	}
	
//	
	public Server(int port) {
		this.port=port;
		
		this.run();
	}

	
	
    public void run() {
    	
    	System.out.print("Server On \n");
		try {
			this.serverSocket =  new ServerSocket(port);
			System.out.print(this.serverSocket.getLocalSocketAddress());
			
		}	catch(IOException e) {			
			System.err.format(e.getMessage());
			System.exit(1);
		}
		while (isRunning) {
			Socket clientSocket = null;
			try {
		        clientSocket = serverSocket.accept();
		        new Thread(new CsapProtocol(clientSocket,bib)).start();
		    } catch (IOException e) {
		        System.err.println("Accept failed.");
		        isRunning=false;
		        System.exit(1);
		    }			
		    // Start the thread.
		}
		
		
   }
 
    
    
}
