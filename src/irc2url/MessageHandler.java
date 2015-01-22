package irc2url;

import org.apache.log4j.Logger;

import irc2url.modes;


public class MessageHandler {

	static Logger logger = Logger.getLogger(MessageHandler.class.getName());

	private Msg msg;

	private handle_http handle_http;
	private handle_weather handle_weather;

	public MessageHandler() {
		handle_http = new handle_http();
		handle_weather = new handle_weather();
	}

	public void process_raw(String raw) {
		msg = new Msg();
		
        //ping check
        String[] format = raw.split(" :");
       
		if(format[0].equals("PING")){
        	ircconnection.send_string("PONG");
        }
		else{
			msg.decode(raw);
			triggers();
		}//else not ping
		
		format = null;
		msg = null;
		
	}//process_raw

	private void triggers() {
		//logger.debug("textmode: "+ msg.textmode);
		
		if(msg.mode == modes.PRIVMSG && msg.chan.contains("#") ){
			
			if(true){ 
				
				if(msg.text.contains("http://") || msg.text.contains("https://") ){
					handle_http.process(msg);
				}//if http://

				if(msg.text.startsWith("!weather") || msg.text.startsWith("!w") || msg.text.startsWith(".weather")){
					handle_weather.process(msg);
				}
			}

		}
		else if(msg.textmode.equals("422")) {
			logger.info(msg.text);
		}
				
	}//triggers
	
}
