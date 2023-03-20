package SocketBasedCS;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import java.awt.Color;

public class ClientGUI extends JFrame implements ActionListener {

	private JPanel contentPane;
	private final JButton connectButton = new JButton("Connect");
	private Socket connection;
	private BufferedReader inputChannel;
	private PrintWriter outputChannel;
	private final JButton openButton = new JButton("Open File");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea theText = new JTextArea();
	private final ArrayList<String> theLines = new ArrayList<String>();
	private final JButton sendButton = new JButton("Send to Server");
	private final JButton responseButton = new JButton("Get Response");
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JTextArea theResponse = new JTextArea();
	private final JButton closeButton = new JButton("Close");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientGUI() {
		setTitle("PR3 C/S Text Processing");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 886, 414);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		this.connectButton.addActionListener(this);
		this.connectButton.setBounds(29, 58, 135, 23);
		
		this.contentPane.add(this.connectButton);
		this.openButton.setEnabled(false);
		this.openButton.addActionListener(this);
		this.openButton.setBounds(29, 92, 135, 23);
		
		this.contentPane.add(this.openButton);
		this.scrollPane.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Text to process", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(255, 0, 0)));
		this.scrollPane.setBounds(192, 29, 317, 322);
		
		this.contentPane.add(this.scrollPane);
		this.theText.setEditable(false);
		
		this.scrollPane.setViewportView(this.theText);
		this.sendButton.setEnabled(false);
		this.sendButton.addActionListener(this);
		this.sendButton.setBounds(29, 126, 135, 23);
		
		this.contentPane.add(this.sendButton);
		this.responseButton.setEnabled(false);
		this.responseButton.addActionListener(this);
		this.responseButton.setBounds(29, 160, 135, 23);
		
		this.contentPane.add(this.responseButton);
		this.scrollPane_1.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Server Response (result)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		this.scrollPane_1.setBounds(529, 29, 317, 322);
		
		this.contentPane.add(this.scrollPane_1);
		
		this.scrollPane_1.setViewportView(this.theResponse);
		this.closeButton.addActionListener(this);
		this.closeButton.setEnabled(false);
		this.closeButton.setBounds(29, 193, 135, 23);
		
		this.contentPane.add(this.closeButton);
	}
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == this.closeButton) {
			closeButtonActionPerformed(arg0);
		}
		if (arg0.getSource() == this.responseButton) {
			responseButtonActionPerformed(arg0);
		}
		if (arg0.getSource() == this.sendButton) {
			sendButtonActionPerformed(arg0);
		}
		if (arg0.getSource() == this.openButton) {
			openButtonActionPerformed(arg0);
		}
		if (arg0.getSource() == this.connectButton) {
			connectButtonActionPerformed(arg0);
		}
	}
	protected void connectButtonActionPerformed(ActionEvent arg0) {
		try {
			this.connection = new Socket("localhost", 4445);
			inputChannel = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			outputChannel = new PrintWriter(connection.getOutputStream(), true);
			JOptionPane.showMessageDialog(this,
				    "Connection with server established");
			this.connectButton.setEnabled(false);
			this.openButton.setEnabled(true);
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this,
				    "Impossible to establish connection with server. Client will terminate",
				    "Connection error",
				    JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	protected void openButtonActionPerformed(ActionEvent arg0) {
		JFileChooser fc = new JFileChooser(".");
		fc.showOpenDialog(this);
		File file = fc.getSelectedFile();
		BufferedReader fileBR;
		try {
			fileBR = new BufferedReader(new FileReader(file));
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				    "Cannot open file. Does it exist?",
				    "File Issue",
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			String line;
			line = fileBR.readLine();
			while (line!=null) {
				theText.append(line); theText.append("\n");
				theLines.add(line);
				line = fileBR.readLine();
			}
			theLines.add("\u001a");
			fileBR.close();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				    "Error while reading file. Program will close",
				    "Error ",
				    JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		this.openButton.setEnabled(false);		
		this.sendButton.setEnabled(true);
	}
	
	protected void sendButtonActionPerformed(ActionEvent arg0) {
		// this sends the contents of the file to the server
		// simulating a delay but without interfering with the swing thread
		final ProgressMonitor monitor = new ProgressMonitor(this, "", "Sending lines. DO NOT CANCEL", 0, theLines.size());;
		Runnable worker = ()->{
			EventQueue.invokeLater( ()->{
				monitor.setMillisToDecideToPopup(0);
				monitor.setMillisToPopup(0);}
			);
			for (int i=0; i<theLines.size(); i++) {
				final int j = i;
				this.outputChannel.println(theLines.get(i));
					EventQueue.invokeLater( ()->{
					monitor.setProgress(j);
					}
				);
				// take your time. Simulate it takes long to send...
				try {Thread.sleep(300);} catch(InterruptedException ie) {}
			}
			monitor.close();
			EventQueue.invokeLater(()->{
				JOptionPane.showMessageDialog(this,
				    "Text sent to server");
				this.sendButton.setEnabled(false);
				this.responseButton.setEnabled(true);
			});
		}; // end of worker
		
		Thread th = new Thread(worker);
		th.start();
		
	}
	
	
	protected void responseButtonActionPerformed(ActionEvent arg0) {
		Runnable worker = ()-> {
			try {
				String response = this.inputChannel.readLine();
				while (!response.equals("SERVICE_TERMINATED")) {
					final String line = response;
					EventQueue.invokeLater(()->{
						this.theResponse.append(line+"\n");
					}
					);
					try{Thread.sleep(300);}catch(Exception e) {}
					response = this.inputChannel.readLine();
				}
				EventQueue.invokeLater(()-> {
					JOptionPane.showMessageDialog(this,
						    "Response completed. Service terminated");
				}
				);
				try {
					this.inputChannel.close();
					this.outputChannel.close();
					this.connection.close();
				}
				catch(Exception e) {}
				EventQueue.invokeLater(()-> {
					this.responseButton.setEnabled(false);
					this.closeButton.setEnabled(true);
				}
				);
			}
			catch(Exception e) {
				EventQueue.invokeLater(()->{
					JOptionPane.showMessageDialog(this,
						    "Error while receiving response. Program will close",
						    "Error receiving",
						    JOptionPane.ERROR_MESSAGE);
					System.exit(0);
					}
				);
				
			}
		}; // end of worker
		Thread th = new Thread(worker);
		th.start();
	}
	
	protected void closeButtonActionPerformed(ActionEvent arg0) {
		System.exit(0);
	}
}
