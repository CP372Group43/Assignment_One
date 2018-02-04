import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
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

    StringReader read = null;
	public Socket clientSocket = null;
	public static void main(String[] args) {
		CsapClient client = new CsapClient();
        client.setVisible(true);
	}
	
	public CsapClient() {
		setTitle("CSAP Client");
		setSize(WIDTH, HEIGHT);
    		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		setResizable(false);

    		
    		JPanel server_panel = new JPanel(new FlowLayout());
        	JPanel message_panel = new JPanel();
        	JPanel response_panel = new JPanel();
        	JPanel wrapper_panel = new JPanel();
        	JPanel actions_panel = new JPanel();
    		
    		host_text_field = new JTextField("", 10);
    		host_text_field.setBackground(Color.WHITE);
    		
    		port_text_field = new JTextField("", 10);
    		port_text_field.setBackground(Color.WHITE);
    		
    		body_text_area = new JTextArea(10, 20);
    		body_text_area.setBackground(Color.WHITE);
    		body_text_area.setLineWrap(true);
    		body_text_area.setWrapStyleWord(true);
    		JScrollPane body_scroll_panel = new JScrollPane(body_text_area);
    		
    		response_text_area = new JTextArea(10, 20);
    		response_text_area.setEditable(false);
    		response_text_area.setBackground(Color.WHITE);
    		response_text_area.setLineWrap(true);
    		response_text_area.setWrapStyleWord(true);
    		JScrollPane response_scroll_panel = new JScrollPane(response_text_area);
    		
//    		setLayout(new FlowLayout());
        
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
        JButton connect_button = new JButton("Connect"); 
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
			a.printStackTrace();
		}
		}else if("send".equals(e.getActionCommand()))
			try {
				send();
			} catch(Exception a) {
				a.printStackTrace();
			}
		}
	public void send() {
        DataInputStream in = null;
        DataOutputStream out = null;
        // connecting to the server
        try {
    		out= new DataOutputStream(clientSocket.getOutputStream());
    		in=new DataInputStream(clientSocket.getInputStream());

    		String intxt = body_text_area.getText().replace('\n', ',');
    		checkInput(intxt);
    		intxt+="\n";
    		
            out.writeBytes(intxt);
       		String inptxt = in.readLine();
       		
       	response_text_area.setText(inptxt.replaceAll("-newline-", "\n"));
        } catch (UnknownHostException e) {
        	response_text_area.setText(e.toString());
        } catch (IOException e) {
        	response_text_area.setText(e.toString());
        } catch(Exception e) {
        	response_text_area.setText(e.toString());
        }

	}
	
	public void connect(String host, int port) throws IOException {
		if(clientSocket==null) {
        try {
            clientSocket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.err.println("The host could not be found: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for connection to: " + host);
            System.exit(1);
        }
		}else if (clientSocket.isConnected()){
			clientSocket.close();
		}
	}
	public void checkInput(String input) throws Exception{
		String[] texts = input.split(",");
		String i = "",isbn="";
		
		if((input.contains("SUBMIT")||input.contains("UPDATE")) && !input.contains("ISBN")) {
				throw new Exception("no ISBN entered");
		}else if(input.contains("SUBMIT")||input.contains("UPDATE")) {
			int j=0;
			for (j=0;j<texts.length;j++) {
				i=texts[j];
				if(i.contains("ISBN")) {
					try {
					isbn=i.substring(5, i.length());
					}catch(Exception e) {
						throw new Exception("invalid ISBN entered");
					}
				}
			}
			isbn.replaceAll( "-", "" );

	        //must be a 13 digit ISBN
	        if ( isbn.length() != 13 )
	        {
	            throw new Exception("invalid ISBN entered");
	        }
			try
	        {
	            int tot = 0;
	            for ( int y = 0; y < 12; y++ )
	            {
	                int digit = Integer.parseInt( isbn.substring( y, y + 1 ) );
	                tot += (y % 2 == 0) ? digit * 1 : digit * 3;
	            }

	            //checksum must be 0-9. If calculated as 10 then = 0
	            int checksum = 10 - (tot % 10);
	            if ( checksum == 10 )
	            {
	                checksum = 0;
	            }
	            
	            if(!(checksum == Integer.parseInt( isbn.substring( 12 ) ))) {
	            	throw new Exception("invalid ISBN entered");
	            }
	        }
	        catch ( NumberFormatException nfe )
	        {
	            //to catch invalid ISBNs that have non-numeric characters in them
	        	throw new Exception("invalid ISBN entered");
	        }
		}
	}
	
}