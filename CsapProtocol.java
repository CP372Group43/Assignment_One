package Assignment_One;
import java.net.*;
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
	
	protected Socket client=null;
	public CsapProtocol(Socket client) {
		this.client=client;
	}
	public void run() {
		
	}
	
	
}