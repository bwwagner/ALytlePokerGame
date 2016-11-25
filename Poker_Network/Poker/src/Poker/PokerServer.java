import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class PokerServer
{
  public static void main(String[] args) throws IOException 
  {
    ServerSocket listener = new ServerSocket(9090);
    try 
    {
      while (true) 
      {
        Socket socket = listener.accept();
        try 
        {
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          out.println("Connection Successful!");
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

class Game 
{
  Player currentPlayer;
  
  boolean[] clickedHandOne = new boolean[5];
  boolean[] clickedHandTwo = new boolean[5];
  
  boolean playerOneDone = false;
  boolean playerTwoDone = false;
  
  ImageView[] handOneCards = fillHands();
  ImageView[] handTwoCards = fillHands();
  
  String[][] h1 = new String[5][3];
  String[][] h2 = new String[5][3];
  public ImageView[] fillHands()
  {
    ImageView[] hand = new ImageView[5];
    for (int i = 0; i < hand.length; i++)
    {
      hand[i] = new ImageView("file:///F:/Cards/Blank.png");
    }
    return hand;
  }
  
  public ArrayList<Integer> numOfKind(String[] hand) //To find One Pair, Two Pair, Three of a Kind, Full House, and Four of a Kind
  {
    ArrayList<Integer> results = new ArrayList<>();
    int similarKind = 0;
    for (int i = 0; i < hand.length; i++)
    {
      String[] currentCard = hand[i].split("\\s+");
      for (int j = i + 1; j < hand.length; j++)
      {
        String[] nextCard = hand[j].split("\\s+");
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
  String[] types = new String[]
  {
    "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two"
  };
  public int getCardRank(String card)
  {
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
  
  public boolean straight(String[] hand) //Rank 5
  {
    String[] firstCard = hand[0].split("\\s+");
    String[] secondCard = hand[1].split("\\s+");
    String[] thirdCard = hand[2].split("\\s+");
    String[] fourthCard = hand[3].split("\\s+");
    String[] fifthCard = hand[4].split("\\s+");
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
    String[] firstCard = hand[0].split("\\s+");
    String[] secondCard = hand[1].split("\\s+");
    String[] thirdCard = hand[2].split("\\s+");
    String[] fourthCard = hand[3].split("\\s+");
    String[] fifthCard = hand[4].split("\\s+");
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
  
  public boolean boolContains(boolean[] clickedCards, boolean value)
  {
    for (boolean clickedCard : clickedCards)
    {
      if (clickedCard == value)
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
  
  public void fillDeck(ArrayList<String> deck)
  {
    String[] suits = new String[]
    {
      "Hearts", "Diamonds", "Spades", "Clubs"
    };
    for (String type : types)
    {
      for (String suit : suits)
      {
        deck.add(type + " of " + suit);
      }
    }
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
        handOneTypes[i] = handOne[i].split("\\s+")[0];
        handTwoTypes[i] = handTwo[i].split("\\s+")[0];
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
  public Image getCard(String[] card)
  {
    String fileName = "file:///F:/Cards/";
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
  public void tradeCards(ArrayList<String> deck, String[] handOne, String[] handTwo)
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
  }
  
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
  
  class Player extends Thread
  {
    Player opponent;
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    
    public Player(Socket socket)
    {
      this.socket = socket;
      try
      {
        input = new BufferedReader(
                                   new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
        output.println("MESSAGE Waiting for opponent to connect");
      }
      catch (IOException e)
      {
        System.out.println("Player died: " + e);
      }
    }
    
    public void setOpponent(Player opponent)
    {
      this.opponent = opponent;
    }
    
    public void run()
    {
      try
      {
        output.println("MESSAGE All players connected");
        while (true)
        {
          String command = input.readLine();
          if (command.startsWith("MOVE"))
          {
            int location = Integer.parseInt(command.substring(5));
          }
          else if (command.startsWith("QUIT"))
          {
            return;
          }
        }
      }
      catch (IOException e)
      {
        System.out.println("Player died: " + e);
      }
      finally
      {
        try {socket.close();} catch (IOException e)
        {
          
        }
      }
    }
  }
}