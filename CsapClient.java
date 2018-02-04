package Assignment_One;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	
	public static void main(String[] args) {
		CsapClient client = new CsapClient();
        client.setVisible(true);
	}
	
	public CsapClient() {
		setTitle("CSAP Client");
		setSize(WIDTH, HEIGHT);
    		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		
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
    		
    		response_text_area = new JTextArea(10, 20);
    		response_text_area.setEditable(false);
    		response_text_area.setBackground(Color.WHITE);
    		
//    		setLayout(new FlowLayout());
        
    		server_panel.add(new JLabel("Host: "));
    		server_panel.add(host_text_field);
        
    		server_panel.add(new JLabel("Port: "));
    		server_panel.add(port_text_field);
        
    		message_panel.add(new JLabel("Request Body: "));
    		message_panel.add(body_text_area);
    		
    		response_panel.add(new JLabel("Response: "));
    		response_panel.add(response_text_area);
    		
    		wrapper_panel.add(server_panel, BorderLayout.NORTH);
    		wrapper_panel.add(message_panel, BorderLayout.CENTER);
    		
    		// add Send Button
        JButton send_button = new JButton("Send"); 
        send_button.addActionListener(this);
        actions_panel.add(send_button); 
        
        // add Connect/Disconnect Button
        JButton connect_button = new JButton("Connect"); 
        connect_button.addActionListener(this);
        actions_panel.add(connect_button);
        
        wrapper_panel.add(actions_panel, BorderLayout.CENTER);
        
        wrapper_panel.add(response_panel, BorderLayout.CENTER);
    		
    		add(wrapper_panel);
    		
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
		connect(host_text_field.getText(),Integer.parseInt(port_text_field.getText()));
		} catch(Exception a) {
			a.printStackTrace();
		}
		read = new StringReader(body_text_area.getText());
    }
	
	public void connect(String host, int port) throws IOException {
		Socket socket = null;
        InputStream input = null;
        DataInputStream in = null;
DataOutputStream out = null;
        // connecting to the server
        try {
            socket = new Socket(host, port);
    		OutputStream output = socket.getOutputStream();
//  		bw = new BufferedWriter(new OutputStreamWriter(output));
    		out= new DataOutputStream(socket.getOutputStream());
    		in=new DataInputStream(socket.getInputStream());

    		String intxt = body_text_area.getText().replace('\n', ',');
    		intxt+="\n";
            out.writeBytes(intxt);
            
          //  input = socket.getInputStream();
       //		br = new BufferedReader(new InputStreamReader(input));
       			String inptxt = in.readLine();
       		
       		//System.out.println(br.readLine());
       		response_text_area.setText(inptxt);
        } catch (UnknownHostException e) {
            System.err.println("The host could not be found: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for connection to: " + host);
            System.exit(1);
        }
       // bw.close();
      //  br.close();
        socket.close();

	}
	
}