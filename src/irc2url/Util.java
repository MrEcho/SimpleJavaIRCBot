package irc2url;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class Util {

	public static String httpGET(String urlToRead) {
		URL url;
		HttpURLConnection conn = null;
		BufferedReader rd;
		String line;
		String result = "";
		
		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.99 Safari/537.36";
		
		try {
			url = new URL(urlToRead);
			
			conn = (HttpURLConnection) url.openConnection();

			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			conn.addRequestProperty("Referer", "google.com");
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", userAgent);
			conn.addRequestProperty("Accept", "*/*");
			conn.addRequestProperty("Host", url.getHost() );
			conn.setInstanceFollowRedirects(true);
			
			boolean redirect = false;
			
			int status = conn.getResponseCode();
			System.out.print("status: "+ status);
			
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
				redirect = true;
			}
			
			
			if (redirect) {
				 
				// get redirect url from "location" header field
				String newUrl = conn.getHeaderField("Location");
				System.out.println(">> "+ newUrl);
		 
				// get the cookie if need, for login
				String cookies = conn.getHeaderField("Set-Cookie");
		 
				// open the new connnection again
				conn = (HttpURLConnection) new URL(newUrl).openConnection();
				conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
				conn.addRequestProperty("User-Agent", userAgent);
				conn.addRequestProperty("Accept", "*/*");
				conn.addRequestProperty("Referer", "google.com");
				conn.setRequestProperty("Cookie", cookies);
				conn.setInstanceFollowRedirects(true);
				
			}
			
			String contentType = conn.getContentType();
			System.out.print(contentType);
			
			if(contentType.contains("html") || contentType.contains("json")) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
			}
			
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Retrieves the INI file for a specific component<br>
	 * 
	 * @return INI file for a specific component
	 */
	public static File getIniFile(String filename) {
		File file = null;
		
		File f_ls = new File("./"+ filename);
		if (f_ls.exists()){
			file = new File("./" + filename);
		}
		else {
			System.out.println("Could not load: "+ filename);
		}
		
		return file;
	}
	
	/**
	 * Sends messages submitted to <code>System.out</code> to the output log and messages to
	 * <code>System.err</code> to the error log.
	 */
    public static void tieSystemOutAndErrToLog(Logger logger) {
        System.setOut(createLoggingProxy(System.out, logger));
        System.setErr(createLoggingProxy(System.err, logger));
    }
    
    /**
	 * Used by tieSystemOutAndErrToLog.
	 */
    private static PrintStream createLoggingProxy(final PrintStream realPrintStream, final Logger logger) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                //realPrintStream.print(string);
                logger.info(string.trim());
            }
        };
    }
	
}
