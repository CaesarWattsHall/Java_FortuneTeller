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
import javax.swing.*;

//Class to precise who is connected : Client or Server
public class crwhClientServer 
{

    public static void main(String[] args) {
        Object[] selectioValues = {"Server", "Client"};
        String initialSection = "Server";
        Object selection = JOptionPane.showInputDialog(null, "Login as : ", "Caesar's Excellent Fortune Teller", JOptionPane.QUESTION_MESSAGE, null, selectioValues, initialSection);

        if (selection.equals("Server")) {
            String[] arguments = new String[]{};
            crwhMultiThreadChatServerSync.main(arguments);

        } else if (selection.equals("Client")) {
            String IPServer = JOptionPane.showInputDialog("Now, please register the Server IP Address: ");
            String[] arguments = new String[]{IPServer};
            crwhChatClient.main(arguments);
        }
    }
}
//END