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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

// Class to manage Client chat Box.
public class crwhChatClient 
{
    static class ChatAccess extends Observable {
        private Socket crwhSocket;
        private OutputStream crwhOutputStream;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        public void InitSocket(String server, int port) throws IOException {
            crwhSocket = new Socket(server, port);
            crwhOutputStream = crwhSocket.getOutputStream();
            
            Thread receivingThread = new Thread() {
                @Override
                
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(crwhSocket.getInputStream()));

                        String line;

                        while ((line = reader.readLine()) != null) {
                            notifyObservers(line);
                        }

                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            receivingThread.start();
        }

        private static final String CRLF = "\r\n"; // newline
        public void send(String text) {

            try {
                crwhOutputStream.write((text + CRLF).getBytes());
                crwhOutputStream.flush();

            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        public void close() {
            try {
                crwhSocket.close();

            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }


    static class ChatFrame extends JFrame implements Observer {
        private JTextArea crwhTextArea;
        private JTextField crwhInputTextField;
        private JButton crwhSendButton;
        private ChatAccess crwhChatAccess;
        public ChatFrame(ChatAccess chatAccess) {

            this.crwhChatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        private void buildGUI() {
            crwhTextArea = new JTextArea(20, 50);
            crwhTextArea.setEditable(false);
            crwhTextArea.setLineWrap(true);
            add(new JScrollPane(crwhTextArea), BorderLayout.CENTER);
            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);

            crwhInputTextField = new JTextField();
            crwhSendButton = new JButton("Send");
            box.add(crwhInputTextField);
            box.add(crwhSendButton);

            // Action for the inputTextField and the goButton
            ActionListener sendListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String str = crwhInputTextField.getText();
                    if (str != null && str.trim().length() > 0) {
                        crwhChatAccess.send(str);
                    }

                    crwhInputTextField.selectAll();
                    crwhInputTextField.requestFocus();
                    crwhInputTextField.setText(" ");
                }
            };

            crwhInputTextField.addActionListener(sendListener);
            crwhSendButton.addActionListener(sendListener);
            
            this.addWindowListener(new WindowAdapter() {
                @Override

                public void windowClosing(WindowEvent e) {
                    crwhChatAccess.close();
                }
            });
        }


        public void update(Observable o, Object arg) {
            final Object finalArg = arg;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    crwhTextArea.append(finalArg.toString());
                    crwhTextArea.append("\n");
                }
            });
        }
    }

    public static void main(String[] args) {
        String server = args[0];
        int port = 2222;

        ChatAccess access = new ChatAccess();
        JFrame frame = new ChatFrame(access);
        frame.setTitle("Caesar's Excellent Fortune Teller - is now connected to " + server + ":" + port);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        try {
            access.InitSocket(server, port);

        } catch (IOException ex) {
            System.out.println("I'm sorry, but I cannot connect to " + server + ":" + port);
            System.exit(0);
        }
    }
}
//END