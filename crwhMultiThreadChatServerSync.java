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
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

// the Server class
public class crwhMultiThreadChatServerSync {

   // The server socket.
  private static ServerSocket crwhServerSocket = null;

  // The client socket.
  private static Socket crwhClientSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int crwhMAXClientsCount = 10;
  private static final crwhClientThread[] crwhThreads = new crwhClientThread[crwhMAXClientsCount];

  public static void main(String args[]) {
    // The default port number.
    int portNumber = 2222;
    if (args.length < 1) {

      System.out.println("Usage: Java crwhMultiThreadChatServerSync [portNumber]\n"
          + "Now using port number = " + portNumber);

    } else {
      portNumber = Integer.parseInt(args[0]);
    }

    try {
      crwhServerSocket = new ServerSocket(portNumber);

    } catch (IOException e) {
      System.out.println(e);
    }

    while (true) {
      try {
        crwhClientSocket = crwhServerSocket.accept();
        int i = 0;
        for (i = 0; i < crwhMAXClientsCount; i++) {
          if (crwhThreads[i] == null) {
            (crwhThreads[i] = new crwhClientThread(crwhClientSocket, crwhThreads)).start();
            break;
          }
        }

        if (i == crwhMAXClientsCount) {
          PrintStream os = new PrintStream(crwhClientSocket.getOutputStream());
          os.println("Unfortunately, the server is too busy at the moment. Please try again later.");
          os.close();
          crwhClientSocket.close();
        }

      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }  
}
//END