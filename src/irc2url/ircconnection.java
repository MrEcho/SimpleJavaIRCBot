package irc2url;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;


public class ircconnection {

	static Logger logger = Logger.getLogger(ircconnection.class.getName());
	
	private static ircconnection ircconnection_object;

	private static String SERVER = "";
	private static int PORT = 0;
	private static String PASS = "";
	private static String USER = "";
	private static String NICK = "";
	
	private Socket clientSocket = null;
	private static java.io.DataOutputStream clientOUT;
	BufferedReader clientIN = null;
	
	private ircconnection(){
		//start();
	}
	
	public static ircconnection getInstance() {
		if(ircconnection_object == null) {
			ircconnection_object = new ircconnection();
		}
		
		return ircconnection_object;
	}
	
	public void start() {

		new Thread(new Runnable() {

			public void run() {
					Thread.currentThread().setName("IRC Connection");

					loadSettings();
					
					try{
						clientSocket = new Socket(SERVER, PORT);
						clientSocket.setSoTimeout(600000);
						clientSocket.setKeepAlive(true);

						clientOUT = new DataOutputStream(clientSocket.getOutputStream());
						clientIN = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"iso-8859-1"), 16);
						
					}
					catch(Exception e) {
						logger.error("ircconnection: "+  e);
					}
				
					try{
						Thread.currentThread();
						Thread.sleep(1000);	
					}catch(InterruptedException e){ }
					
					ircconnection.connectionStart();

			}
		}).start();

	}// start
	
	protected static void connectionStart() {
		
		ircconnection.send_string("PASS "+ PASS);
		ircconnection.send_string("NICK "+ NICK);
		
		int len = USER.length();
		ircconnection.send_string("USER "+USER+" "+len+" * : "+ USER);
		
	}
	
	public void reNick() {
		ircconnection.send_string("NICK "+ NICK);
	}

	public static void send_string(String s){

		logger.info("SENDING>>>"+s);

		try {
			String charset = "UTF-8";
			clientOUT.write(s.getBytes(charset));
			clientOUT.write((byte)0x0D);
			clientOUT.write((byte)0x0A);
			clientOUT.flush();
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}

	}

	public static void send_msg(String chan, String mode, String string) {
		send_string(mode +" "+ chan +" :"+ string);	
	}
	
	private void loadSettings(){
		Properties props = new Properties();
		String r = "";
		File f = Util.getIniFile("connection.ini");
		
		if (f.exists()){
			try{
				InputStream is =new BufferedInputStream(new FileInputStream(f));
				props.load(is);
				is.close();	
			}catch(IOException e){ logger.error(e); }
			
			r = props.getProperty("SERVER");
			if (r != null) {
				SERVER = r.toString();
			}
			
			r = props.getProperty("PORT");
			if (r != null) {
				PORT = Integer.valueOf(r.toString());
			}

			r = props.getProperty("PASS");
			if (r != null) {
				PASS = r.toString();
			}
			
			r = props.getProperty("USER");
			if (r != null) {
				USER = r.toString();
			}
			
			r = props.getProperty("NICK");
			if (r != null) {
				NICK = r.toString();
			}

		}	
	}//load settings
	
}
