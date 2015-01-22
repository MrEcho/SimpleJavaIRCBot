package irc2url;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class handle_weather {

	static Logger logger = Logger.getLogger(handle_weather.class.getName());

	private String apikey = "";

	public handle_weather() {
		loadSettings();
	}

	public void process(Msg msg) {

		String outstring = null;

		String[] parts = msg.text.split(" ");
		if (parts.length >= 2 && parts[1] != null) {
			String userquery = parts[1].trim();
			if (parts.length >= 3) {
				userquery += " " + parts[2].trim();
			}
			if (parts.length >= 4) {
				userquery += " " + parts[3].trim();
			}

			String query = "";
			JsonParser parser = new JsonParser();

			try {
				URI uri = new URI("http", "api.wunderground.com", "/api/" + apikey + "/conditions/q/" + userquery + ".json", null);
				query = uri.toASCIIString();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}

			String result = Util.httpGET(query);
			logger.debug(query);

			try {
				// http://stackoverflow.com/questions/8233542/parse-a-nested-json-using-gson

				JsonElement array = parser.parse(result);
				// logger.debug(array.getAsJsonObject().get("current_observation").getAsJsonObject().get("temperature_string"));
				// logger.debug(array.getAsJsonObject().get("current_observation").getAsJsonObject().get("display_location").getAsJsonObject().get("full"));

				String temp = array.getAsJsonObject()
						.get("current_observation").getAsJsonObject()
						.get("temperature_string").toString();
				String weather = array.getAsJsonObject()
						.get("current_observation").getAsJsonObject()
						.get("weather").toString();
				String location = array.getAsJsonObject()
						.get("current_observation").getAsJsonObject()
						.get("display_location").getAsJsonObject().get("full")
						.toString();
				String humidity = array.getAsJsonObject()
						.get("current_observation").getAsJsonObject()
						.get("relative_humidity").toString();
				String localtime = array.getAsJsonObject()
						.get("current_observation").getAsJsonObject()
						.get("local_time_rfc822").toString();

				outstring = "Location: " + location + " Temp:" + temp + " "	+ weather + ", Humidity:" + humidity + " " + localtime;
				outstring = outstring.replace("\"", "");

				// outstring = msg.name +": "+ outstring;

				parser = null;
				array = null;
			} catch (Exception e) {
				logger.error(e);
			}

			result = null;

			if (outstring == null || outstring.equals("")) {
				logger.info("Trying yahoo...");

				URI uri = null;
				try {
					uri = new URI(
							"https",
							"query.yahooapis.com",
							"/v1/public/yql",
							"q=select * from weather.forecast where woeid in (select woeid from geo.places(1) where text='"	+ userquery + "')&format=json", null);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

				query = uri.toASCIIString();
				result = Util.httpGET(query);
				logger.debug(query);

				try {
					JsonElement array = parser.parse(result);
					JsonElement results = array.getAsJsonObject().get("query")
							.getAsJsonObject().get("results").getAsJsonObject()
							.get("channel");

					String tempunit = results.getAsJsonObject().get("units")
							.getAsJsonObject().get("temperature").toString();

					// String location =
					// results.getAsJsonObject().get("item").getAsJsonObject().get("title").toString();
					// location = location.replace("Conditions for ","");

					JsonElement locationarray = results.getAsJsonObject().get(
							"location");

					String city = locationarray.getAsJsonObject().get("city")
							.toString();
					String country = locationarray.getAsJsonObject()
							.get("country").toString();
					String region = locationarray.getAsJsonObject()
							.get("region").toString();

					String location = city + ", " + region + ", " + country;

					String temp = results.getAsJsonObject().get("item")
							.getAsJsonObject().get("condition")
							.getAsJsonObject().get("temp").toString();
					temp = temp.replace("\"", "");

					float c = ((Integer.decode(temp) - 32) * 5) / 9;
					String Celsius = String.format("%.2g", c);
					Celsius = "(" + Celsius + "C)";

					String weather = temp
							+ ""
							+ tempunit
							+ " "
							+ Celsius
							+ " "
							+ results.getAsJsonObject().get("item")
									.getAsJsonObject().get("condition")
									.getAsJsonObject().get("text").toString();

					String humidity = results.getAsJsonObject()
							.get("atmosphere").getAsJsonObject()
							.get("humidity").toString();
					humidity = humidity + "%";

					// Location: Sun City, CA Temp:50.0 F (10.0 C) Clear,
					// Humidity:33% Tue, 18 Nov 2014 08:05:12 -0800

					outstring = "Location: " + location + " Temp:" + weather + ", Humidity:" + humidity;
					outstring = outstring.replace("\"", "");

					parser = null;
					results = null;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (outstring != null) {
				ircconnection.send_msg(msg.chan, msg.getMode(), outstring);
			}

		}// if not null

	}

	private void loadSettings() {
		Properties props = new Properties();
		String r = "";
		File f = Util.getIniFile("wunderground.ini");

		if (f.exists()) {
			try {
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				props.load(is);
				is.close();
			} catch (IOException e) {
				logger.error(e);
			}

			r = props.getProperty("apikey");
			if (r != null) {
				apikey = r.toString();
			}

		}
	}// load settings

}
