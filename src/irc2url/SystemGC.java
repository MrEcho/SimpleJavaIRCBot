package irc2url;

import java.util.TimerTask;

public class SystemGC extends TimerTask{
	
	public void run() { 
		System.gc();
		System.out.print("GC "+ System.currentTimeMillis());	
	}
}
