
import java.net.*;
import java.io.*;
import java.time.*;

public class MeasurementClient {

    // helper function
    public static String payloader(int messageSize) {
        String payload = "";
        for (int i = 0; i < messageSize; i++) {
            payload += "s";
        }
        return payload;
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java MeasurementClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in))) {

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                String[] usermsg = userInput.split(" ");

                String protocolPhase = usermsg[0];
                String measurementType = usermsg[1];
                int numProbes = 0;
                int serverDelay;
                int messageSize = 1;
                
                String servermsg;

                out.println(userInput);
                System.out.println(userInput);
                if (usermsg[0].equals("t")) {
                        System.out.println("Connection closed.");
                        out.close();
                        System.exit(0);
                    }
                if (usermsg.length == 5) {

                    numProbes = Integer.parseInt(usermsg[2]);
                    messageSize = Integer.parseInt(usermsg[3]);
                    serverDelay = Integer.parseInt(usermsg[4]);
                    

                }
                if (usermsg.length != 5) {
                    
                    if ((usermsg[0].equals("m")) == false) {
                        System.err.println("404 ERROR: Invalid Connection Setup Message");
                        System.exit(1);
                    }
                    if (usermsg.length != 3) {
                        System.err.println("404 ERROR: Invalid Measurement Message");
                        System.exit(1);
                    }

                }

                // if message is received, start measurement phase
                long totalTime = 0;
                servermsg = in.readLine();

                if (servermsg.equals("200 OK:Ready")) {
                    for (int i = 1; i < numProbes + 1; i++) {
                        String payload = payloader(messageSize);
                        long sent = System.currentTimeMillis();

                        out.println("m " + i + " " + payload + "\n");
                        System.out.println("echo: " + in.readLine());

                        long received = System.currentTimeMillis();

                        long delay = received - sent;
                        System.out.println("delay: " + delay + "ms");
                        totalTime += delay;
                    }
                    if (measurementType.equals("rtt")) // Average Round Trip Time Measurement of all payload (ms)
                    {

                        double rtt = (double) totalTime / (double) numProbes;
                        System.out.println("Avg rtt: " + rtt + "ms");
                    } else if (measurementType.equals("tput")) { // Throughput Measurement of all payload (bits/sec)
                        double tput = (numProbes * messageSize * 8) / (double) totalTime;
                        System.out.println("throughput is: " + tput);
                    } else {
                        System.err.println("404 ERROR: Invalid Measurement Type");
                        System.out.println("Connection closed.");
                        in.close();
                        System.exit(0);
                    }
                }

                if (servermsg.equals("200 OK: Closing Connection")) {
                    socket.close();
                    System.out.println("Connection closed.");
                    System.exit(1);
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
