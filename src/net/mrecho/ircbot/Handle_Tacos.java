package net.mrecho.ircbot;


import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Handle_Tacos {

	static Logger logger = Logger.getLogger(Handle_Tacos.class.getName());
	
	public Handle_Tacos(){
		
	}
	
	public void process(Msg msg) {
		
		String[] parts = msg.text.split(" ");
		if (parts.length >= 2 && parts[1] != null) {
			String userquery = " "+parts[1].trim();
			if (parts.length >= 3) {
				userquery += " " + parts[2].trim();
			}
			if (parts.length >= 4) {
				userquery += " " + parts[3].trim();
			}
		
		
		String query = "";

		try {
			URI uri = new URI("https", "ajax.googleapis.com", "/ajax/services/search/images","v=1.0&q=tacos"+ userquery, null);
			query = uri.toASCIIString();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		String result = Util.httpGET(query);
		logger.debug(query);
		
		String outstring = "";

		try {
			
			JsonParser parser = new JsonParser();
			JsonElement array = parser.parse(result);
			JsonArray imageArray = (JsonArray)array.getAsJsonObject().get("responseData").getAsJsonObject().get("results").getAsJsonArray();
			logger.debug(imageArray.get(0).getAsJsonObject().get("url"));
			
			outstring = imageArray.get(0).getAsJsonObject().get("url").toString();
			outstring += "  "+imageArray.get(0).getAsJsonObject().get("originalContextUrl").toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		
		if (outstring != null) {
			IRCConnection.send_msg(msg.chan, msg.getMode(), outstring);
		}
		
		}//split
		else {
			IRCConnection.send_msg(msg.chan, msg.getMode(), "!tacos query");
		}
		
	}
}
