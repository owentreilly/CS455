To use these programs, first compile using javac <filename>.java 
then use the java command java <Server-filename> <port number> on the machine you wish to be the server.
This command will begin the server and tell it to start listening. 
On a different terminal to connect to the server use
java <Client-filename> <hostname> <host port number>
The server accepts commands in the form 
s (rtt/tput) (Number of probes) (Probe Size) (Server delay)\

To close the connection gracefully type in "t"