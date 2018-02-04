package Assignment_One;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import java.io.*;
import java.net.*;


public class CsapClient extends JFrame implements ActionListener {
	
	public static final int WIDTH = 400;
    public static final int HEIGHT = 500;
    
    public static JTextField type_text_field;
    public static JTextField host_text_field;
    public static JTextField port_text_field;
    public static JTextArea body_text_area;
    public static JTextArea response_text_area;
    public static JButton connect_button;

    StringReader read = null;
	public Socket clientSocket = null;
	public static void main(String[] args) {
		CsapClient client = new CsapClient();
        client.setVisible(true);
	}
	
	public CsapClient() {
		// setting up the JFrame
		setTitle("CSAP Client");
		setSize(WIDTH, HEIGHT);
    		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		setResizable(false);

    		// init all our panels
    		JPanel server_panel = new JPanel(new FlowLayout());
        	JPanel message_panel = new JPanel();
        	JPanel response_panel = new JPanel();
        	JPanel wrapper_panel = new JPanel();
        	JPanel actions_panel = new JPanel();
    		
        	// host field
    		host_text_field = new JTextField("", 10);
    		host_text_field.setBackground(Color.WHITE);
    		
    		// port field
    		port_text_field = new JTextField("", 10);
    		port_text_field.setBackground(Color.WHITE);
    		
    		// body text area
    		body_text_area = new JTextArea(10, 20);
    		body_text_area.setBackground(Color.WHITE);
    		body_text_area.setLineWrap(true);
    		body_text_area.setWrapStyleWord(true);
    		JScrollPane body_scroll_panel = new JScrollPane(body_text_area);
    		
    		// response text area
    		response_text_area = new JTextArea(10, 20);
    		response_text_area.setEditable(false);
    		response_text_area.setBackground(Color.WHITE);
    		response_text_area.setLineWrap(true);
    		response_text_area.setWrapStyleWord(true);
    		JScrollPane response_scroll_panel = new JScrollPane(response_text_area);
    		
    		// adding labels and inputs to panels
    		server_panel.add(new JLabel("Host: "));
    		server_panel.add(host_text_field);
    		server_panel.add(new JLabel("Port: "));
    		server_panel.add(port_text_field);
    		message_panel.add(new JLabel("Request Body: "));
    		message_panel.add(body_scroll_panel);
    		response_panel.add(new JLabel("Response: "));
    		response_panel.add(response_scroll_panel);
    		wrapper_panel.add(server_panel, BorderLayout.NORTH);
    		wrapper_panel.add(message_panel, BorderLayout.CENTER);
    		
    		// add Send Button
        JButton send_button = new JButton("Send"); 
        send_button.setActionCommand("send");
        send_button.addActionListener(this);
        actions_panel.add(send_button); 
        
        // add Connect/Disconnect Button
        connect_button = new JButton("Connect"); 
        connect_button.setActionCommand("connect");
        connect_button.addActionListener(this);
        actions_panel.add(connect_button);
        
        wrapper_panel.add(actions_panel, BorderLayout.CENTER);
        wrapper_panel.add(response_panel, BorderLayout.CENTER);
    		
    		add(wrapper_panel);
    		
	}
	
	public void actionPerformed(ActionEvent e) {
		if("connect".equals(e.getActionCommand())) {
			try {
				connect(host_text_field.getText(),Integer.parseInt(port_text_field.getText()));
			} catch(Exception a) {
        			response_text_area.setText(a.toString());
			}
		}else if("send".equals(e.getActionCommand())) {
			try {
				send();
			} catch(Exception a) {
        			response_text_area.setText(a.toString());
			}
		}
	}
	
	// send a CSAP message to the server
	public void send() {
        DataInputStream in = null;
        DataOutputStream out = null;
        // connecting to the server
        try {
        		if(clientSocket!=null) {
    			connectToSocket(host_text_field.getText(),Integer.parseInt(port_text_field.getText()));
        		}
	    		out= new DataOutputStream(clientSocket.getOutputStream());
	    		in=new DataInputStream(clientSocket.getInputStream());
	
	    		// replace \n with , so the intxt is a valid format for sending to the server
	    		String intxt = body_text_area.getText().replace('\n', ',');
	    		intxt += "\n";
	    		
	    		// valide the request message
	    		checkInput(intxt);
	    		
	    		// send the message to the server
	    		out.writeBytes(intxt);
	    		
	    		// retrieve the response from the server
	    		String inptxt = in.readLine();	

	    		// display the response in the response text area, and properly format the message
	       	response_text_area.setText(inptxt.replaceAll("-newline-", "\n"));
        } catch (UnknownHostException e) {
        		response_text_area.setText("Request not sent. Not connected ");
        } catch (IOException e) {
            	response_text_area.setText(e.getMessage());
        } catch(Exception e) {
        		response_text_area.setText(e.getMessage());
        }

	}
	
	// connect/disconnect to/from the server
	public void connect(String host, int port) throws IOException {
		// connect to the server
		if(clientSocket==null) {
			// connect to socket
			connectToSocket(host, port);
	    // disconnect from the server
		}else if (clientSocket.isConnected()){
			clientSocket.close();
			clientSocket = null;
			connect_button.setText("Connect");
			response_text_area.setText("Disconnected from server.");
			// enable editing of host and port while disconnected
			host_text_field.setEditable(true);
			port_text_field.setEditable(true);
		}
	}
	
	public void connectToSocket(String host, int port) throws IOException {
		// validate host and server
		if(host_text_field.getText() == null) {
			response_text_area.setText("Error. Please provide a host.");
			return;
		} else if(port_text_field.getText() == null) {
			response_text_area.setText("Error. Please provide a port.");
			return;
		}
		
        try {
            clientSocket = new Socket(host, port);
            connect_button.setText("Disconnect");
				response_text_area.setText("Connected to server.");
				// disable editing of host and port while connected
				host_text_field.setEditable(false);
				port_text_field.setEditable(false);
        } catch (UnknownHostException e) {
            response_text_area.setText("The host could not be found: " + host);
        } catch (IOException e) {
            response_text_area.setText("Couldn't get I/O for connection to: " + host);
        }
	}
	
	// validate the request body input
	public void checkInput(String input) throws Exception{
		String[] texts = input.split(",");
		String i = "",isbn="";
		
		if(input == null || input.equals("")) {
			throw new Exception("Request Not Sent. No request entered.");
		} else if((input.contains("SUBMIT")||input.contains("UPDATE")) && !input.contains("ISBN")) {
			throw new Exception("Request Not Sent. No ISBN entered.");
		}else if(input.contains("SUBMIT")||input.contains("UPDATE")) {
			int j=0;
			for (j=0;j<texts.length;j++) {
				i=texts[j];
				if(i.contains("ISBN")) {
					try {
					isbn=i.substring(5, i.length());
					}catch(Exception e) {
						throw new Exception("Request Not Sent. Invalid ISBN entered.");
					}
				}
			}
			 if ( isbn.length() != 13 )
		        {
		            throw new Exception("Request Not Sent. Invalid ISBN entered.");
		        }

	        //must be a 13 digit ISBN
	       
			try
	        {
	            int total = 0;
	            for ( int y = 0; y < 12; y++ )
	            {
	                int digit = Integer.parseInt( isbn.substring( y, y + 1 ) );
	                total += (y % 2 == 0) ? digit * 1 : digit * 3;
	            }

	            //checksum must be 0-9. If calculated as 10 then = 0
	            int checktotal = 10 - (total % 10);
	            if ( checktotal == 10 )
	            {
	                checktotal = 0;
	            }
	            
	            if(!(checktotal == Integer.parseInt( isbn.substring( 12 ) ))) {
	            	throw new Exception("Request Not Sent. Invalid ISBN entered.");
	            }
	        }
	        catch ( NumberFormatException nfe )
	        {
	            //to catch invalid ISBNs that have non-numeric characters in them
	        	throw new Exception("Request Not Sent. Invalid ISBN entered.");
	        }
		}
	}
	
}