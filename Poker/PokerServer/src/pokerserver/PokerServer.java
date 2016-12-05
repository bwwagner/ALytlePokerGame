/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokerserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class PokerServer
{

    final static Integer ante = 1000;            //initial ante value
    final static Integer initialBank = 100000;   //initial player bank

    boolean[] clickedHandOne = new boolean[5];
    boolean[] clickedHandTwo = new boolean[5];

    boolean playerOneDone = false;
    boolean playerTwoDone = false;

    ImageView[] handOneCards = fillHands();
    ImageView[] handTwoCards = fillHands();

    static String[][] h1 = new String[5][3];
    static String[][] h2 = new String[5][3];

    static String[] myHand = new String[5];
    static String[][] temp = new String[5][5];

    static boolean send = false;

    static DataOutputStream dataOut;
    static DataInputStream dataIn;

    static ArrayList<PlayerThread> players = new ArrayList();
    static int index = 0;

    static Random r = new Random();
    static int draw;
    

    public ImageView[] fillHands()
    {
        ImageView[] hand = new ImageView[5];
        for (int i = 0; i < hand.length; i++)
        {
            hand[i] = new ImageView("file:./src/Resources/Images/Cards/Blank.png");
        }
        return hand;
    }

    public Image getCard(String[] card)
    {
        String fileName = "file:./src/Resources/Images/Cards/";
        for (int i = 0; i < card.length; i++)
        {
            fileName += card[i];
            if (i < card.length - 1)
            {
                fileName += "_";
            }
        }
        fileName += ".png";
        return new Image(fileName);
    }

    /*public void tradeCards(ArrayList<String> deck, String[] handOne, String[] handTwo)
    {
        //Alternate through each player's cards needing to be traded. 
        //If only one player wishes to trade cards, then that players cards will be the only one traded.
        Random r = new Random();
        int draw;
        while (boolContains(clickedHandOne, true) || boolContains(clickedHandTwo, true))
        {
            for (int i = 0; i < 5; i++)
            {
                if (clickedHandOne[i])
                {
                    draw = r.nextInt(deck.size());
                    handOne[i] = deck.get(draw);
                    h1[i] = handOne[i].split("\\s+");
                    handOneCards[i].setImage(getCard(h1[i]));
                    deck.remove(draw);
                    clickedHandOne[i] = false;
                }
                if (clickedHandTwo[i])
                {
                    draw = r.nextInt(deck.size());
                    handTwo[i] = deck.get(draw);
                    h2[i] = handTwo[i].split("\\s+");
                    handTwoCards[i].setImage(getCard(h2[i]));
                    deck.remove(draw);
                    clickedHandTwo[i] = false;
                }
            }
        }
    }*/

    public String numToString(int value)
    {
        switch (value)
        {
            case 1:
                return "One";
            case 2:
                return "Two";
            case 3:
                return "Three";
            case 4:
                return "Four";
            case 5:
                return "Five";
            default:
                return "Zero";
        }
    }

    /*public static void setBets(Betting apot) {
        //values from above
        apot.setAnte(ante);
        apot.setBank(initialBank);
    }*/
    public static void main(String[] args) throws IOException
    {
        //ante mode
        //Betting player1bets = new Betting(0, 1000);
        //Betting player2bets = new Betting(0, 1000);
        //setBets(player1bets);
        //setBets(player2bets);
        

        ArrayList<String[]> allHands = new ArrayList<String[]>();
        
        String[] temp1 = new String[5];
        String[] temp2 = new String[5];
        ServerSocket listener = new ServerSocket(9090);
        try
        {
            while (true)
            {
                Socket socket = listener.accept();
                
                PlayerThread p = new PlayerThread(index, dataIn, dataOut);
                index++;
                players.add(p);
                p.start();

                try
                {
                    Thread.sleep(50); //Need to slow down send to change cards to file location
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(PokerServer.class.getName()).log(Level.SEVERE, null, ex);
                }

                for (int i = 0; i < players.size(); i++)
                {
                    if (allHands.isEmpty())
                    {
                        allHands.add(players.get(i).myHand);
                    }
                    else if (!allHands.get(0)[0].equals(players.get(i).myHand[0]))
                    {
                        allHands.add(players.get(i).myHand);
                    }
                }
                //System.out.println(faceOff(allHands.get(0), allHands.get(1)));
                try
                {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(Arrays.toString(p.myHand));

                    out.flush();
                    
                }
                finally
                {
                    socket.close();
                }
            }
        }
        finally
        {
            listener.close();
        }
    }
}

class PlayerThread extends Thread
{
    int index;
    final String INET_ADDR = "224.0.0.3"; //Change to IUS network address, for constant sending to all clients
    InetAddress addr = InetAddress.getByName(INET_ADDR);
    final int PORT = 8888; //For constant sending to all clients
    DatagramSocket serverSocket = new DatagramSocket();
    Random r = new Random();
    String[] myHand = new String[5];
    ArrayList<String> deck = fillDeck();

    public static ArrayList<String> fillDeck()
    {
        ArrayList<String> deck = new ArrayList<String>();

        String[] suits = new String[]
        {
            "Hearts", "Diamonds", "Spades", "Clubs"
        };
        String[] types = new String[]
        {
            "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two"
        };
        for (String type : types)
        {
            for (String suit : suits)
            {
                deck.add(type + " of " + suit);
            }
        }
        return deck;
    }
    

    public PlayerThread(int index, DataInputStream in, DataOutputStream out) throws UnknownHostException, SocketException
    {
        PokerServer.dataIn = in;
        PokerServer.dataOut = out;
        this.index = index;
    }
    int drawCards;
    @Override
    public void run()
    {

        for (int i = 0; i < 5; i++)
        {
            drawCards = r.nextInt(deck.size());
            myHand[i] = deck.get(r.nextInt(deck.size()));
            deck.remove(drawCards);
        }
        for (int i = 0; i < 5; i++)
        {
            String newMyHand = myHand[i].replaceAll(" ", "_");
            myHand[i] = newMyHand;
        }
        int counter = 0;
        while (counter < 10)
        {
            String msg = Arrays.toString(myHand);

            // Create a packet that will contain the data
            // (in the form of bytes) and send it.
            DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, PORT);
            try
            {
                serverSocket.send(msgPacket);
            }
            catch (IOException ex)
            {
                Logger.getLogger(PokerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            counter++;
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //System.out.println(in.read());
        byte[] buf = new byte[256];
        final int PORT = 8888;
        final String INET_ADDR = "224.0.0.3"; //Change to IUS network address
        InetAddress address = null;
        try
        {
            address = InetAddress.getByName(INET_ADDR);
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        MulticastSocket clientSocket = null;
        try
        {
            clientSocket = new MulticastSocket(PORT);
        }
        catch (IOException ex)
        {
            Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            //Joint the Multicast group.
            clientSocket.joinGroup(address);
        }
        catch (IOException ex)
        {
            Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] temp1 = new String[5];
        String[] temp2 = new String[5];
        while (true)
        {
            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
            try
            {
                clientSocket.receive(msgPacket);
            }
            catch (IOException ex)
            {
                Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            String msg = new String(buf, 0, buf.length);

            if (msg.contains("/") && !msg.contains("["))
            {
                if(temp1[0] == null)
                {
                    temp1 = msg.split(", ");
                }
                else if(!temp1[0].equals(msg.split(", ")[0]))
                {
                    temp2 = msg.split(", ");
                    break;
                }
            }
        }
        String[][] replaceOne = new String[5][2];
        String[][] replaceTwo = new String[5][2];
        for(int i = 0; i < temp1.length; i++)
        {
            replaceOne[i] = temp1[i].split(" / ");
            replaceTwo[i] = temp2[i].split(" / ");
        }
        for(int i = 0; i < replaceOne.length; i++)
        {
            if(replaceOne[i][1].substring(0,3).equals("true") && replaceOne[i][1].length() > 4)
            {
                replaceOne[i][1] = "true";
            }
            if(replaceOne[i][1].substring(0,4).equals("false") && replaceOne[i][1].length() > 5)
            {
                replaceOne[i][1] = "false";
            }
        }
        System.out.println("Hands Before: ");
        for(int i = 0; i < myHand.length; i++)
        {
            System.out.println(myHand[i]);
        }
        System.out.println();
        
        for(int i = 0; i < replaceOne.length; i++)
        {
            System.out.println(replaceOne[i][0] + " " + replaceOne[i][1]);
        }
        System.out.println();
        for(int i = 0; i < replaceTwo.length; i++)
        {
            System.out.println(replaceTwo[i][0] + " " + replaceTwo[i][1]);
        }
        System.out.println();
        
        int draw;
        System.out.println("Replacing these Cards:");
        for(int i = 0; i < 5; i++)
        {
            if(myHand[i].equals(replaceOne[i][0]) && replaceOne[i][1].equals("true"))
            {
                System.out.println(replaceOne[i][0]);
                draw = r.nextInt(deck.size());
                myHand[i] = deck.get(r.nextInt(deck.size()));
                deck.remove(draw);
            }
            if(myHand[i].equals(replaceTwo[i][0]) && replaceTwo[i][1].equals("true"))
            {
                System.out.println(replaceTwo[i][0]);
                draw = r.nextInt(deck.size());
                myHand[i] = deck.get(r.nextInt(deck.size()));
                deck.remove(draw);
            }
        }
        System.out.println();
        System.out.println("Hands After: ");
        for(int i = 0; i < myHand.length; i++)
        {
            System.out.println(myHand[i]);
        }
        System.out.println();
        
        counter = 0;
        /*while (counter < 10)
        {
            String msg = Arrays.toString(myHand);
            System.out.println(msg);
            // Create a packet that will contain the data
            // (in the form of bytes) and send it.
            DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, PORT);
            try
            {
                serverSocket.send(msgPacket);
            }
            catch (IOException ex)
            {
                Logger.getLogger(PokerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            counter++;
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
    }
}

