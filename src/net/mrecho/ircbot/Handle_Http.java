package net.mrecho.ircbot;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.mina.util.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Handle_Http {

	static Logger logger = Logger.getLogger(Handle_Http.class.getName());
	private static String[]  BAD = {"google.com","wiki","fuck","shit","imgur","cocks","dick","mrecho","angrycoder.org"};
	private Msg msg;
	private String urlserver = "";
	private String badwords = "";

	public Handle_Http() {
		loadSettings();
		setBadWords();
	}

	private void setBadWords() {
		BAD = badwords.split(",");
	}

	public void process(Msg msg) {
		this.msg = msg;

		try {

			String[] tparts = msg.text.split(" ");

			for(String part : tparts){
				part = part.trim();

				if(stringContainsItemFromList(msg.text, BAD)){
					logger.info("Bad words! "+ msg.text);
				}

				else if( part.startsWith("http://") || part.startsWith("https://") ){
					logger.info("HTTP>>> "+ part);

					do_query(part);
				}//if
				else{
					logger.info("nope");
				}
			}//for

		} catch(Exception e) {
			e.printStackTrace();
		}

	}//handle_http

	private void do_query(String string) {

		String title = null;

		try {

			Document doc = Jsoup.connect(string).followRedirects(true).get();
			title = doc.title();

			logger.info("title>>>"+title);
			if(!title.equals("")){
				if(title.length() >= 100){
					title = title.substring(0,100);
				}
				title = title.trim();
			}

			//doc.remove();
			doc = null;

		} catch(Exception e){
			logger.error(e);
		}

		String clean = strip(string);
		logger.debug("clean_url="+clean);

		if(title != null){

			//if(out.contains("-")){
			//	String[] otsplit = out.split("-");
			//	out = otsplit[0];
			//}

			String ot = strip(title);
			logger.debug("title strip="+ ot);
			String[] s_url_array = clean.split(" ");

			String[] s_title_array = ot.split(" ");

			float f_title_array = s_title_array.length;

			Collection<String> listOne = new ArrayList<String>(Arrays.asList(s_url_array));
			Collection<String> listTwo = new ArrayList<String>(Arrays.asList(s_title_array));

			listOne.retainAll( listTwo );

			float size = listOne.size() / f_title_array;
			logger.info("Fraction="+size);

			if(title != null && size <= 0.5 && listTwo.size() >= 2 && title.length() >= 10 ){
				title = StringEscapeUtils.unescapeHtml4(title);
				IRCConnection.send_msg(msg.chan,msg.getMode(), title);
			}
			else{
				logger.debug("Too much of a match");
			}
		}
	}//do_query


	private String strip(String s){
		String clean = s;
		if(s != null && !s.equals("")){
			clean = s.replace("_", " ");
			clean = s.replace(",", " ");
			clean = clean.replace("-", " ");
			clean = clean.replace("|", " ");
			clean = clean.replace(".com", "");
			clean = clean.replace(".net", "");
			clean = clean.replace(".org", "");
			clean = clean.replace(".", " ");
			clean = clean.replace("/", " ");
			clean = clean.replace(":", " ");
			clean = clean.replace("http", "");
			clean = clean.replace("https", "");
			clean = clean.replace("www", "");

			clean = clean.replace("  ", " ");
			clean = clean.replace("   ", " ");
			clean = clean.replace("  ", " ");

			clean = clean.toLowerCase().trim();
		}
		return clean;
	}//strip

	private static boolean stringContainsItemFromList(String inputString, String[] bad){

		for(int i =0; i < bad.length; i++){
			if(inputString.contains(bad[i])){
				return true;
			}
		}
		return false;
	}

	private void loadSettings(){
		Properties props = new Properties();
		String r = "";
		File f = Util.getIniFile("http.ini");

		if (f.exists()){
			try{
				InputStream is =new BufferedInputStream(new FileInputStream(f));
				props.load(is);
				is.close();	
			}catch(IOException e){ logger.error(e); }

			r = props.getProperty("urlserver");
			if (r != null) {
				urlserver = r.toString();
			}

			r = props.getProperty("badwords");
			if (r != null) {
				badwords = r.toString();
			}

		}	
	}//load settings

}
