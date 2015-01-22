package irc2url;

import org.apache.log4j.Logger;


public class Msg {
	
	static Logger logger = Logger.getLogger(Msg.class.getName());

	public modes mode;
	public String text = "";
	public String name;
	public String chan = "";
	
	public String textmode = "";
	

	public void decode(String raw) {

		String[] parts = raw.split(" :");
		String left = parts[0];
		String right = "";
		if(parts.length == 2){
			right = parts[1];
		}
		
		String[] modes = left.split(" ");
		String mode = null;
		if(modes.length >= 3){
			mode = modes[1];
			textmode = mode;
			
			setMode(mode);
			
			setFrom(modes[0]);
			
			if(modes.length >= 3){
				setChan(modes[2]);
			}

			setText(right);
			
			Print();
			
		}//modes
		
	}//decode
	
	public void setMode(String m) {
		
		if(m.equals("PRIVMSG")){
			this.mode = modes.PRIVMSG ;
		}
		else{
			this.mode = modes.OTHER;
		}
		//log("Mode="+mode);

	}//setMode
	
	public void setText(String right) {
		text = right.trim();
		//log("Text="+text);
	}

	public void setFrom(String n) {
		//:zelazny.freenode.net 354 mrecho 152 #rantradio c4rc4s H+
		//:mrecho!mrecho@pdpc/supporter/professional/mrecho PRIVMSG #rantradio :.
		String sname = n.substring(1, n.length()); // removes the ":"
		
		if(sname.contains("!")){
			String[] na = sname.split("!");
			this.name = na[0].trim();
		}
		else{
			this.name = sname.trim();
		}
		
		if(this.name.equals("***")){
			this.mode = modes.OTHER;
		}
		
		//log("Name="+name);
	}//setFrom
	
	public void setChan(String c) {
		if(c.contains("#")){
			this.chan = c.trim();
		}
		else{
			
		}
		//log("Chan="+chan);
	}//setChan


	public String getMode() {
		return this.mode.toString();
	}
	
	public void Print() {
		if(mode!= null && mode == modes.PRIVMSG){
//			String tname = padRight(name,15,' ');
//			logger.info(""+ mode + " "+ chan + " "+ tname + " >>> "+ text);
		}	
	}
	
	public static String padRight(String str, Integer length, char car) {
		  return String.format("%" + (length - str.length()) + "s", "").replace(" ", String.valueOf(car)) + str; 
	}

}
