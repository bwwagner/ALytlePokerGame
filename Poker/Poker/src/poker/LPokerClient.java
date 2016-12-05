/*
 * GUI Client for LytlePoker
 */
package Poker;

import java.awt.Dimension;
import java.util.*;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.application.Application;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;

/**
 * Runs the client as an application. Looks for a server on localhost at port
 * 9090. Connects to receive message then exits. Adapted from code at:
 * http://cs.lmu.edu/~ray/notes/javanetexamples/
 *
 * @author bwwagner
 * @author jrlytle
 */
public class LPokerClient extends Application
{
    protected String Server;
    String[][] tradeChoice = new String[5][2];
    String[] handOne = new String[5];  // player's hand
    String[] handTwo = new String[5];  // opponent's hand
    boolean[] myClickedCards = new boolean[5];
    
    public ArrayList<Integer> numOfKind(String[] hand) //To find One Pair, Two Pair, Three of a Kind, Full House, and Four of a Kind
    {
        ArrayList<Integer> results = new ArrayList<>();
        int similarKind = 0;
        for (int i = 0; i < hand.length; i++)
        {
            String[] currentCard = hand[i].split("_");
            for (int j = i + 1; j < hand.length; j++)
            {
                String[] nextCard = hand[j].split("_");
                if (currentCard[0].equals(nextCard[0]))
                {
                    similarKind++;
                }
            }
            if (similarKind > 0)
            {
                results.add(similarKind);
                similarKind = 0;
            }
        }
        return results;
    }

    public int[] getHandRank(String[] hand)
    {
        int[] handRank = new int[5];
        for (int i = 0; i < hand.length; i++)
        {
            handRank[i] = getCardRank(hand[i]);
        }
        Arrays.sort(handRank);
        return handRank;
    }

    public int getCardRank(String card)
    {
        String[] types = new String[]
        {
            "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two"
        };
        for (int i = 0; i < types.length; i++)
        {
            if (card.equals(types[i]))
            {
                return i + 2;
            }
        }
        return 0; //Will never be reached
    }

    public String rankToCard(int rank)
    {
        String[] types = new String[]
        {
            "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two"
        };
        return types[rank - 2];
    }

    public boolean onePair(String[] hand) //Rank 8
    {
        ArrayList<Integer> results = numOfKind(hand);
        if (!results.isEmpty())
        {
            if (results.get(0) == 1)
            {
                return true;
            }
        }
        return false;
    }

    public boolean twoPair(String[] hand) //Rank 7
    {
        ArrayList<Integer> results = numOfKind(hand);
        if (!results.isEmpty())
        {
            if (results.size() == 2 && results.get(0) == 1 && results.get(1) == 1)
            {
                return true;
            }
        }
        return false;
    }

    public boolean threeOfAKind(String[] hand) //Rank 6
    {
        ArrayList<Integer> results = numOfKind(hand);
        if (!results.isEmpty())
        {
            if (results.size() == 2 && results.get(0) == 2 && results.get(1) == 1)
            {
                return true;
            }
        }
        return false;
    }
    
    public <E> boolean contains(E[] array, E value)
    {
        for (int i = 0; i < 5; i++)
        {
            if (array[i].equals(value))
            {
                return true;
            }
        }
        return false;
    }

    public String maxCard(String[] hand)
    {
        int max = -1;
        for (int i = 0; i < 5; i++)
        {
            if (getCardRank(hand[i]) > max)
            {
                max = getCardRank(hand[i]);
            }
        }
        return rankToCard(max);
    }
    
    public boolean straight(String[] hand) //Rank 5
    {
        String[] firstCard = hand[0].split("_");
        String[] secondCard = hand[1].split("_");
        String[] thirdCard = hand[2].split("_");
        String[] fourthCard = hand[3].split("_");
        String[] fifthCard = hand[4].split("_");
        String[] cardValues = new String[]
        {
            firstCard[0], secondCard[0], thirdCard[0], fourthCard[0], fifthCard[0]
        };
        String max = maxCard(cardValues);
        if (getCardRank(max) != 14 && getCardRank(max) > 5)
        {
            for (int i = getCardRank(max); i > getCardRank(max) - 5; i--)
            {
                for (int j = 0; j < 5; j++)
                {
                    if (contains(cardValues, rankToCard(i)))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean flush(String[] hand) //Rank 4
    {
        String[] firstCard = hand[0].split("_");
        String[] secondCard = hand[1].split("_");
        String[] thirdCard = hand[2].split("_");
        String[] fourthCard = hand[3].split("_");
        String[] fifthCard = hand[4].split("_");
        return firstCard[2].equals(secondCard[2]) && firstCard[2].equals(thirdCard[2]) && firstCard[2].equals(fourthCard[2]) && firstCard[2].equals(fifthCard[2]);
    }

    public boolean fullHouse(String[] hand) //Rank 3
    {
        ArrayList<Integer> results = numOfKind(hand);
        if (!results.isEmpty())
        {
            if (results.size() == 3 && ((results.get(0) == 2 && results.get(1) == 1 && results.get(2) == 1) || (results.get(0) == 1 && results.get(1) == 2 && results.get(2) == 1)))
            {
                return true;
            }
        }
        return false;
    }

    public boolean fourOfAKind(String[] hand) //Rank 2
    {
        ArrayList<Integer> results = numOfKind(hand);
        return !results.isEmpty() && (results.get(0) == 3 && results.get(1) == 2 && results.get(2) == 1);
    }

    public boolean straightFlush(String[] hand) //Rank 1
    {
        return straight(hand) && flush(hand);
    }

    public int initialRank(String[] hand)
    {
        if (straightFlush(hand))
        {
            return 1;
        }
        if (fourOfAKind(hand))
        {
            return 2;
        }
        if (fullHouse(hand))
        {
            return 3;
        }
        if (flush(hand))
        {
            return 4;
        }
        if (straight(hand))
        {
            return 5;
        }
        if (threeOfAKind(hand))
        {
            return 6;
        }
        if (twoPair(hand))
        {
            return 7;
        }
        if (onePair(hand))
        {
            return 8;
        }
        return 9; //High Card
    }
    
    public String stringRank(String[] hand)
    {
        if (straightFlush(hand))
        {
            return "Straight Flush";
        }
        if (fourOfAKind(hand))
        {
            return "Four of a Kind";
        }
        if (fullHouse(hand))
        {
            return "Full House";
        }
        if (flush(hand))
        {
            return "Flush";
        }
        if (straight(hand))
        {
            return "Straight";
        }
        if (threeOfAKind(hand))
        {
            return "Three of a Kind";
        }
        if (twoPair(hand))
        {
            return "Two Pair";
        }
        if (onePair(hand))
        {
            return "One Pair";
        }
        return "High Card";
    }
    
    public int faceOff(String[] handOne, String[] handTwo)
    {
        int rankOne = initialRank(handOne);
        int rankTwo = initialRank(handTwo);

        if (rankOne < rankTwo)
        {
            return 1; //Player 1 wins
        }
        if (rankTwo < rankOne)
        {
            return 2; //Player 2 wins
        }
        if (rankOne == rankTwo)
        {
            //Neither Player wins, both have same type of hand.
            //Check internal rank for winner
            String[] handOneTypes = new String[5];
            String[] handTwoTypes = new String[5];
            for (int i = 0; i < 5; i++)
            {
                handOneTypes[i] = handOne[i].split("_")[0];
                handTwoTypes[i] = handTwo[i].split("_")[0];
            }
            int[] handOneRanks = getHandRank(handOneTypes);
            int[] handTwoRanks = getHandRank(handTwoTypes);
            for (int i = 4; i >= 0; i--)
            {
                if (handOneRanks[i] > handTwoRanks[i])
                {
                    return 1; //Player 1 wins internal rank
                }
                if (handOneRanks[i] < handTwoRanks[i])
                {
                    return 2; //Player 2 wins internal rank
                }
            }
            return 3; //Draw, both players have all same internal ranks
        }
        return -1; //Error if this point is reached
    }
    
    //p Socket server = null;
    public void start(Stage primaryStage)
    {

        ImageView[] handOneCards = fillHands();
        ImageView[] handTwoCards = fillHands();

        String[][] h1 = new String[5][3];
        String[][] h2 = new String[5][3];

        String[] tempOne = new String[5];
        String[] tempTwo = new String[5];
        //Betting player1bets = new Betting(0, 0);
        //Betting player2bets = new Betting(0, 0);

        //String inMessage = this.Server;
        String message = "";
        String serverAddress = "localhost";

        String[][] allHands;

        //int draw;
        for (int i = 0; i < handOne.length; i++)
        {
            handOne[i] = "";//new ImageView(new Image(handOne[i]));  // TODO: Get Card from Server
            handTwo[i] = "";//new ImageView(new Image(handTwo[i]));
        }

        final Pane root = new Pane();
        final VBox controls = new VBox(5);
        root.getChildren().add(controls);

        //Begin Background
        //bww - look for image in relative path ./src/Resources/Images/
        final ImageView background = new ImageView("file:./src/Resources/Images/Texas_Holdem_Poker_Table.jpg");
        background.setFitWidth(1850);
        background.setPreserveRatio(true);
        background.setSmooth(true);
        background.setCache(true);
        background.setLayoutX(13);
        background.setLayoutY(1);
        root.getChildren().add(background);
        //End Background

        //Server Address Entry
        final TextInputDialog ipAddr = new TextInputDialog("localhost");

        //Display IP Address Dialog
        ipAddr.setTitle("Connect to Poker Server");
        ipAddr.setHeaderText("Server IP Address");
        ipAddr.setContentText("Please enter the server's IP Address:");
        //End Display IP Address Dialog

        //DONE use this result to choose server IP
        //Get the response value.
        Optional<String> result = ipAddr.showAndWait();
        if (result.isPresent())
        {
            System.out.println("Your choice: " + result.get());
            serverAddress = result.get();
        }
        //end Server Address Entry

        // begin open socket
        try
        {
            Socket server = new Socket(serverAddress, 9090);
            BufferedReader input  = new BufferedReader(new InputStreamReader(server.getInputStream()));

            //Get Hand One Images
            message = input.readLine();
            message = message.replaceAll("\\[|\\]", "");
            handOne = message.trim().split(", ");  //converts comma delimited string

            byte[] buf = new byte[256];
            final String INET_ADDR = "224.0.0.3"; //Change to IUS network address
            final int PORT = 8888;
            InetAddress address = InetAddress.getByName(INET_ADDR);
            MulticastSocket clientSocket = new MulticastSocket(PORT);
            //Joint the Multicast group.
            clientSocket.joinGroup(address);
            String[] hands;
            while (true)
            {
                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(msgPacket);

                String msg = new String(buf, 0, buf.length);
                msg = msg.replaceAll("\\[", "");
                for (int i = 0; i < msg.length(); i++)
                {
                    if (msg.substring(i, i + 1).equals("]"))
                    {
                        msg = msg.substring(0, i);
                    }
                }
                if (tempOne[0] == null)
                {
                    tempOne = msg.split(", ");
                }
                else if (tempTwo[0] == null && !tempOne[0].equals(msg.split(", ")[0]))
                {
                    tempTwo = msg.split(", ");
                }
                if (tempOne[0] != null && tempTwo[0] != null)
                {
                    break;
                }

            }
            System.out.println(Arrays.toString(tempOne));
            System.out.println(Arrays.toString(tempTwo));
            if (handOne[0].equals(tempOne[0]))
            {
                handOne = tempOne;
                handTwo = tempTwo;
            }
            else
            {
                handOne = tempTwo;
                handTwo = tempOne;
            }
            String myHandFile = "";
            for (int i = 0; i < handOne.length; i++)
            {
                myHandFile = "file:./src/Resources/Images/Cards/" + handOne[i] + ".png";
                handOneCards[i] = new ImageView(myHandFile);
            }
        }
        catch (IOException e)
        {
            System.out.println("Connection Failed.");
        }
        // end open socket

        //put handOne info in h1
        for (int i = 0; i < handOne.length; i++)
        {
            h1[i][0] = handOne[i];
        }

        
        //TODO: Fix Conversion of h1 to handOne Imageview so Cards will display
        //Start Hand One and Hand Two Images
        for (int i = 0; i < handOneCards.length; i++)
        {
            //Hand One Start
            //handOneCards[i].setImage(getCard(h1[i]));

            handOneCards[i].relocate(400 + (i * 215), 590);

            root.getChildren().add(handOneCards[i]);
            //Hand One End
        }
        //End Hand One and Hand Two Images   
        
        ImageView clickedCardOne = new ImageView("file:./src/Resources/Images/Cards/Selected.png");
        clickedCardOne.relocate(395, 585);
        clickedCardOne.setVisible(false);
        root.getChildren().add(clickedCardOne);
        
        ImageView clickedCardTwo = new ImageView("file:./src/Resources/Images/Cards/Selected.png");
        clickedCardTwo.relocate(610, 585);
        clickedCardTwo.setVisible(false);
        root.getChildren().add(clickedCardTwo);
        
        ImageView clickedCardThree = new ImageView("file:./src/Resources/Images/Cards/Selected.png");
        clickedCardThree.relocate(825, 585);
        clickedCardThree.setVisible(false);
        root.getChildren().add(clickedCardThree);
        
        ImageView clickedCardFour = new ImageView("file:./src/Resources/Images/Cards/Selected.png");
        clickedCardFour.relocate(1040, 585);
        clickedCardFour.setVisible(false);
        root.getChildren().add(clickedCardFour);
        
        ImageView clickedCardFive = new ImageView("file:./src/Resources/Images/Cards/Selected.png");
        clickedCardFive.relocate(1255, 585);
        clickedCardFive.setVisible(false);
        root.getChildren().add(clickedCardFive);
        
        handOneCards[0].setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg)
            {
                myClickedCards[0] ^= true;
                clickedCardOne.setVisible(myClickedCards[0]);
            }
        });
        handOneCards[1].setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg)
            {
                myClickedCards[1] ^= true;
                clickedCardTwo.setVisible(myClickedCards[1]);
            }
        });
        handOneCards[2].setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg)
            {
                myClickedCards[2] ^= true;
                clickedCardThree.setVisible(myClickedCards[2]);
            }
        });
        handOneCards[3].setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg)
            {
                myClickedCards[3] ^= true;
                clickedCardFour.setVisible(myClickedCards[3]);
            }
        });
        handOneCards[4].setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg)
            {
                myClickedCards[4] ^= true;
                clickedCardFive.setVisible(myClickedCards[4]);
            }
        });

        Button myButton = new Button("Done");
        myButton.relocate(905, 905);
        root.getChildren().add(myButton);

        myButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent arg)
            {
                for (int i = 0; i < tradeChoice.length; i++)
                {
                    tradeChoice[i][0] = handOne[i];
                    tradeChoice[i][1] = String.valueOf(myClickedCards[i]);
                }
                String msg = "";
                for (int i = 0; i < tradeChoice.length; i++)
                {
                    msg += tradeChoice[i][0] + " / " + tradeChoice[i][1];
                    if(i < tradeChoice.length - 1)
                    {
                        msg += ", ";
                    }
                }
                DatagramSocket serverSocket = null;
                try
                {
                    serverSocket = new DatagramSocket();
                }
                catch (SocketException ex)
                {
                    Logger.getLogger(LPokerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                final String INET_ADDR = "224.0.0.3"; //Change to IUS network address, for constant sending to all clients
                InetAddress addr = null;
                try
                {
                    addr = InetAddress.getByName(INET_ADDR);
                }
                catch (UnknownHostException ex)
                {
                    Logger.getLogger(LPokerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                final int PORT = 8888; //For constant sending to all clients
                // Create a packet that will contain the data
                // (in the form of bytes) and send it.
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, PORT);
                try
                {
                    serverSocket.send(msgPacket);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(LPokerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                byte[] buf = new byte[256];
                MulticastSocket clientSocket = null;
                try
                {
                    clientSocket = new MulticastSocket(PORT);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(LPokerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*while (true)
                {
                    msgPacket = new DatagramPacket(buf, buf.length);
                    try
                    {
                        clientSocket.receive(msgPacket);
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(LPokerClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    msg = new String(buf, 0, buf.length);
                    System.out.println(msg);
                    
                }*/
            }
        });
        
        int winner = faceOff(handOne, handTwo);
        Text winnerDisplay = new Text();
        switch(winner)
        {
            case 1: winnerDisplay.setText("You Win with a " + stringRank(handOne)); break;
            case 2: winnerDisplay.setText("Player Two Wins with a " + stringRank(handTwo)); break;
            default: winnerDisplay.setText("Draw"); break;
        }
        winnerDisplay.relocate(850, 440);
        winnerDisplay.setFont(Font.font("Arial", 50));
        winnerDisplay.setFill(Color.WHITE);
        root.getChildren().add(winnerDisplay);
        
        String myHandFile = "";
        for (int j = 0; j < handOne.length; j++)
        {
            myHandFile = "file:./src/Resources/Images/Cards/" + handOne[j] + ".png";
            handOneCards[j] = new ImageView(myHandFile);
            myHandFile = "file:./src/Resources/Images/Cards/" + handTwo[j] + ".png";
            handTwoCards[j] = new ImageView(myHandFile);
        }
        for (int i = 0; i < handOneCards.length; i++)
        {
            //Hand One Start
            //handOneCards[i].setImage(getCard(h1[i]));

            handOneCards[i].relocate(400 + (i * 215), 590);

            root.getChildren().add(handOneCards[i]);
            //Hand One End
            //Hand Two Start
            //handTwoCards[i].setImage(getCard(h2[i]));

            handTwoCards[i].relocate(400 + (i * 215), 82);

            root.getChildren().add(handTwoCards[i]);
            //Hand Two End
        }
        
        Button yourButton = new Button("Done");
        yourButton.relocate(905, 20);
        root.getChildren().add(yourButton);
        // End add cards to root
        //Display Window Start
        primaryStage.setTitle("Poker Network");
        primaryStage.setScene(new Scene(root, 1876, 953));
        primaryStage.show();
        //Display Window End        

    }//end start

    public static void main(String[] args) throws IOException
    {
        launch(args);
        System.exit(0);
    }//main    

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

}//LPokerClient
