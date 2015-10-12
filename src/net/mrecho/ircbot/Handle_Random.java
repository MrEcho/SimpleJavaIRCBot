package net.mrecho.ircbot;



import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Handle_Random {

	static Logger logger = Logger.getLogger(Handle_Random.class.getName());
	
	public Handle_Random() {
	}
	
	public void process(Msg msg) {

		String outstring = "";
		
		String query = "https://www.fourmilab.ch/cgi-bin/Hotbits?nbytes=128&fmt=password&npass=1&lpass=80&pwtype=2";
		
		try {

			Document doc = Jsoup.connect(query).followRedirects(false).get();
			
			Elements password = doc.getElementsByAttribute("name");

			doc = null;
			
			if(!password.html().equals("")){
				outstring = password.html();
			}

		} catch(Exception e){
			logger.error(e);
		}
				
		if (outstring != null) {
			IRCConnection.send_msg(msg.chan, msg.getMode(), outstring);
		}
	}
}
