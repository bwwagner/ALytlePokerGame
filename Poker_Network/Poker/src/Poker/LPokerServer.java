/*
 * Server for LytlePoker
 */
package Poker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A TCP server that runs on port 9090. Waits for clients to connect and
 * transmits a "Connection Successful!" string. 
 * Adapted from code at: http://cs.lmu.edu/~ray/notes/javanetexamples/
 *
 * @author bwwagner
 */
public class LPokerServer {

    /**
     * Runs the server.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    PrintWriter out
                            = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Connection Successful!");
                } finally {
                    socket.close();
                }
            }
        } finally {
            listener.close();
        }
    }
}//LPokerServer
