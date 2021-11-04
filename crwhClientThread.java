/*
*By: Caesar R. Watts-Hall
*Date: April 23, 2019
*Class: JAVA II
*Instructor: Dr.Primo
*Assignment: Indie Assignment #2
*Due Date: April 23, 2019 @ 11:55PM
*/
//START
package chatbot;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

// For every client's connection we call this class
public class crwhClientThread extends Thread {

    private String crwhClientName = null;
    private DataInputStream crwhInputSt = null;
    private PrintStream crwhOutputSt = null;
    private Socket crwhClientSocket = null;

    private final crwhClientThread[] crwhThreaded;
    private int crwhClientsCounter;
    public crwhClientThread(Socket clientSocket, crwhClientThread[] threads) {
        this.crwhClientSocket = clientSocket;
        this.crwhThreaded = threads;
        crwhClientsCounter = threads.length;
    }

    public void run() {
        int crwhClientCounted = this.crwhClientsCounter;
        crwhClientThread[] crwhThreads = this.crwhThreaded;
        try {

            crwhInputSt = new DataInputStream(crwhClientSocket.getInputStream());
            crwhOutputSt = new PrintStream(crwhClientSocket.getOutputStream());
            String name;

            while (true) {
                crwhOutputSt.println("Please register your chatbot name: ");
                name = crwhInputSt.readLine().trim();
                if (name.indexOf('@') == -1) {
                    break;

                } else {
                    crwhOutputSt.println("The name should not contain '@' character.");
                }
            }

            /* Welcome the new the client. */
            crwhOutputSt.println("Hello and Welcome, " + name
                    + " , to Caesar's Excellent Fortune Teller! \nTo leave enter '/quit' in a new line.");

            synchronized (this) {
                for (int i = 0; i < crwhClientCounted; i++) {

                    if (crwhThreaded[i] != null && crwhThreaded[i] == this) {
                        crwhClientName = "@" + name;
                        break;
                    }
                }

                for (int i = 0; i < crwhClientCounted; i++) {
                    if (crwhThreaded[i] != null && crwhThreaded[i] != this) {
                        crwhThreaded[i].crwhOutputSt.println("** Hold Up! A new user called, " + name
                                + " ,has now entered the Fortune Teller ChatBox !!! **");
                    }
                }
            }

            /* Start the conversation. */
            while (true) {
                String line = crwhInputSt.readLine();
                if (line.startsWith("/quit")) {
                    break;
                }

                /* If the message is private sent it to the given client. */
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < crwhClientCounted; i++) {

                                    if (crwhThreaded[i] != null && crwhThreaded[i] != this
                                            && crwhThreaded[i].crwhClientName != null
                                            && crwhThreaded[i].crwhClientName.equals(words[0])) {

                                        crwhThreaded[i].crwhOutputSt.println("[" + name + "]: " + words[1]);

                                        this.crwhOutputSt.println(">" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else {
                    /* The message is public, broadcast it to all other clients. */
                    synchronized (this) {
                        for (int i = 0; i < crwhClientCounted; i++) {
                            if (crwhThreaded[i] != null && crwhThreaded[i].crwhClientName != null) {
                                crwhThreaded[i].crwhOutputSt.println("[" + name + "]: " + line);
                            }
                        }
                    }
                }
            }

            synchronized (this) {
                for (int i = 0; i < crwhClientCounted; i++) {
                    if (crwhThreaded[i] != null && crwhThreaded[i] != this
                            && null != crwhThreaded[i].crwhClientName) {

                        crwhThreaded[i].crwhOutputSt.println("*** The user " + name
                                + " has left the chat room !!! ***");
                    }
                }
            }
            crwhOutputSt.println("*** See ya around, " + name + " ***");

            synchronized (this) {
                for (int i = 0; i < crwhClientCounted; i++) {
                    if (crwhThreaded[i] == this) {
                        crwhThreaded[i] = null;
                    }
                }
            }

            crwhInputSt.close();
            crwhOutputSt.close();
            crwhClientSocket.close();

        } catch (IOException e) {
        }
    }
}
//END