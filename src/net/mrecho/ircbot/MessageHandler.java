package net.mrecho.ircbot;

import org.apache.log4j.Logger;

public class MessageHandler {

	static Logger logger = Logger.getLogger(MessageHandler.class.getName());

	private Msg msg;

	private Handle_Http handle_http;
	private Handle_Weather handle_weather;

	public MessageHandler() {
		handle_http = new Handle_Http();
		handle_weather = new Handle_Weather();
	}

	public void process_raw(String raw) {
		msg = new Msg();

		// ping check
		String[] format = raw.split(" :");

		if (format[0].equals("PING")) {
			IRCConnection.send_string("PONG");
		} else {
			msg.decode(raw);
			triggers();
		}// else not ping

		format = null;
		msg = null;

	}// process_raw

	private void triggers() {
		// logger.debug("textmode: "+ msg.textmode);

		if (msg.mode == modes.PRIVMSG && msg.chan.contains("#")) {

			if (true) {

				if (msg.text.contains("http://") || msg.text.contains("https://")) {
					handle_http.process(msg);
				}// if http://

				if (msg.text.startsWith("!weather") || msg.text.startsWith("!w") || msg.text.startsWith(".weather")) {
					handle_weather.process(msg);
				}
			}

		} else if (msg.textmode.equals("422")) {
			logger.info(msg.text);
		}

	}// triggers

}
