/*
 * GUI Client for LytlePoker
 */
package Poker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * Runs the client as an application. Looks for a server on localhost at port
 * 9090. Connects to receive message then exits. 
 * Adapted from code at: http://cs.lmu.edu/~ray/notes/javanetexamples/
 *
 * @author bwwagner
 */
public class LPokerClient {

    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        Socket s = new Socket(serverAddress, 9090);
        BufferedReader input
                = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String answer = input.readLine();
        JOptionPane.showMessageDialog(null, answer);
        System.exit(0);
    }//main    

}//LPokerClient
