# SimpleJavaIRCBot

This is a simple Java IRC Bot that will do weather look ups and titles for URL's

It was designed to be connected to a ZNC IRC Boncer, I have not checked to see if it will directly connect to an real irc server.

## Setup

There are 3 .ini files that need to be setup.
Just remove the .sample off the filenames.

##### connection.ini 
  This is the server, username , password for the IRC connection

##### http.ini
  urlserver: Is a system for me to log urls on my server with a web interface. But is not needed.
  badwords: Is a filtered list of words for url strings.
  
##### wunderground.ini
  apikey: You will need to signup for your own key, sorry. But the fallback is to Yahoo's API
  
## Run

There is just a simple start.sh file
Because there is no packaged .jar (yet) its just a simple run
