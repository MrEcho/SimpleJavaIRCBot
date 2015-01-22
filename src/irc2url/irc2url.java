package irc2url;

import java.io.IOException;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class irc2url {

	static Logger logger = Logger.getLogger(irc2url.class.getName());
	
	public irc2url() {
		
		Util.tieSystemOutAndErrToLog(logger);
		
		ircconnection c = ircconnection.getInstance();
		c.start();
		
		try{
			Thread.currentThread();
			Thread.sleep(2000);	
		}catch(InterruptedException e){ }
		
		c.reNick();
		
		
		Timer mTimer = new Timer("GC");
		SystemGC mQueueTask=new SystemGC();
		mTimer.schedule(mQueueTask,10000,60000);
		
		logger.info("Starting While Loop");
		
		MessageHandler mh = new MessageHandler();

		try{
			while(true){
				String raw = c.clientIN.readLine();
				logger.debug("RAW>>>"+raw);
				
				mh.process_raw(raw);

			}
		} catch (IOException e1) { logger.error("while: "+e1); }

		logger.info("Running");
		
	}
	
	public static void main(String[] args) {
				
		System.setProperty("file.name","ircbot.log");	
		PropertyConfigurator.configure("log4j.config");

		new irc2url();
	}

}
