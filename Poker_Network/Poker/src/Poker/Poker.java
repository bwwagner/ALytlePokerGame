/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Poker;

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

/**
 *
 * @author jrlytle
 */
public class Poker extends Application
{

    boolean[] clickedHandOne = new boolean[5];
    boolean[] clickedHandTwo = new boolean[5];

    boolean playerOneDone = false;
    boolean playerTwoDone = false;

    ImageView[] handOneCards = fillHands();
    ImageView[] handTwoCards = fillHands();

    String[][] h1 = new String[5][3];
    String[][] h2 = new String[5][3];

    int inc = 0;

    int winner = -1;

    String[] types = new String[]
    {
        "Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two"
    };

    //High Card - Rank 9
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
                    if (contains(cardValues, rankToCard(i)))//(!contains(cardValues, rankToCard(i)))
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

    /*public boolean contains(String[] hand, String value)
    {
        for (String card : hand)
        {
            if (card.equals(value))
            {
                return true;
            }
        }
        return false;
    }*/

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
    
    public ImageView[] fillHands()
    {
        ImageView[] hand = new ImageView[5];
        for (int i = 0; i < hand.length; i++)
        {
            hand[i] = new ImageView("file:///F:/Cards/Blank.png");
        }
        return hand;
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

    @Override
    public void start(Stage primaryStage)
    {
        Random r = new Random();
        ArrayList<String> deck = new ArrayList<>();
        String[] handOne = new String[5];
        String[] handTwo = new String[5];
        int draw;

        fillDeck(deck);
        Collections.shuffle(deck);

        for (int i = 0; i < 10; i++)
        {
            draw = r.nextInt(deck.size());
            if (i % 2 == 0)
            {
                handOne[(i / 2)] = deck.get(draw);
            }
            else
            {
                handTwo[((i - 1) / 2)] = deck.get(draw);
            }
            deck.remove(draw);
        }

        final Pane root = new Pane();
        final VBox controls = new VBox(5);
        root.getChildren().add(controls);

        //Begin Background
        final ImageView background = new ImageView("file:///F:/Cards/Texas_Holdem_Poker_Table.jpg");
        background.setFitWidth(1850);
        background.setPreserveRatio(true);
        background.setSmooth(true);
        background.setCache(true);
        background.setLayoutX(13);
        background.setLayoutY(1);
        root.getChildren().add(background);
        //End Background

        //Showing both hands for now, for testing - Will need to make opposite player's hand face down for final
        //Start Hand One Data
        h1[0] = handOne[0].split("\\s+");
        h1[1] = handOne[1].split("\\s+");
        h1[2] = handOne[2].split("\\s+");
        h1[3] = handOne[3].split("\\s+");
        h1[4] = handOne[4].split("\\s+");
        //End Hand One Data

        //Start Hand Two Data
        h2[0] = handTwo[0].split("\\s+");
        h2[1] = handTwo[1].split("\\s+");
        h2[2] = handTwo[2].split("\\s+");
        h2[3] = handTwo[3].split("\\s+");
        h2[4] = handTwo[4].split("\\s+");
        //End Hand Two Data

        //Start Hand One and Hand Two Images
        for (int i = 0; i < handOneCards.length; i++)
        {
            //Hand One Start
            handOneCards[i].setImage(getCard(h1[i]));

            handOneCards[i].setLayoutX(400 + (i * 215));
            handOneCards[i].setLayoutY(590);

            root.getChildren().add(handOneCards[i]);
            //Hand One End
            //Hand Two Start
            handTwoCards[i].setImage(getCard(h2[i]));

            handTwoCards[i].setLayoutX(400 + (i * 215));
            handTwoCards[i].setLayoutY(82);

            root.getChildren().add(handTwoCards[i]);
            //Hand Two End
        }
        //End Hand One and Hand Two Images

        //Start MouseListener for Hand One for Trading Cards
        handOneCards[0].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandOne[0] ^= true;
        });
        handOneCards[1].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandOne[1] ^= true;
        });
        handOneCards[2].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandOne[2] ^= true;
        });
        handOneCards[3].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandOne[3] ^= true;
        });
        handOneCards[4].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandOne[4] ^= true;
        });
        //End MouseListener for Hand One for Trading Cards
        //Start MouseListener for Hand Two for Trading Cards
        handTwoCards[0].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandTwo[0] ^= true;
        });
        handTwoCards[1].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandTwo[1] ^= true;
        });
        handTwoCards[2].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandTwo[2] ^= true;
        });
        handTwoCards[3].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandTwo[3] ^= true;
        });
        handTwoCards[4].setOnMouseClicked((MouseEvent t) ->
        {
            clickedHandTwo[4] ^= true;
        });
        //End MouseListener of Hand Two for Trading Cards

        //Start Trading Out Cards
        Button handOneDone = new Button("Done");
        handOneDone.setLayoutX(700);
        handOneDone.setLayoutY(900);
        root.getChildren().add(handOneDone);

        Button handTwoDone = new Button("Done");
        handTwoDone.setLayoutX(700);
        handTwoDone.setLayoutY(25);
        root.getChildren().add(handTwoDone);

        handOneDone.setOnMouseClicked((MouseEvent m) ->
        {
            playerOneDone = true;
            if (playerTwoDone)
            {
                tradeCards(deck, handOne, handTwo);
                winner = faceOff(handOne, handTwo);
                //Winner is Player One Start
                if (winner == 1)
                {
                    Text t = new Text(800, 925, "Winner");
                    t.setWrappingWidth(200);
                    t.setTextAlignment(TextAlignment.JUSTIFY);
                    t.setFont(new Font(40));
                    t.setFill(Color.WHITE);
                    root.getChildren().add(t);
                }
                //Winner is Player One End
                //Winner is Player Two Start
                if (winner == 2)
                {
                    Text t = new Text(800, 50, "Winner");
                    t.setWrappingWidth(200);
                    t.setTextAlignment(TextAlignment.JUSTIFY);
                    t.setFont(new Font(40));
                    t.setFill(Color.WHITE);
                    root.getChildren().add(t);
                }
                //Winner is Player Two End
                //No Winner Start
                if (winner == 3)
                {
                    Text t = new Text(938, 476, "Draw");
                    t.setWrappingWidth(200);
                    t.setTextAlignment(TextAlignment.JUSTIFY);
                    t.setFont(new Font(40));
                    t.setFill(Color.WHITE);
                    root.getChildren().add(t);
                }
                //No Winner End
            }
        });

        handTwoDone.setOnMouseClicked((MouseEvent m) ->
        {
            playerTwoDone = true;
            if (playerOneDone)
            {
                tradeCards(deck, handOne, handTwo);
                winner = faceOff(handOne, handTwo);

                //Winner is Player One Start
                if (winner == 1)
                {
                    Text t = new Text(800, 925, "Winner");
                    t.setWrappingWidth(200);
                    t.setTextAlignment(TextAlignment.JUSTIFY);
                    t.setFont(new Font(40));
                    t.setFill(Color.WHITE);
                    root.getChildren().add(t);
                }
                //Winner is Player One End
                //Winner is Player Two Start
                if (winner == 2)
                {
                    Text t = new Text(800, 50, "Winner");
                    t.setWrappingWidth(200);
                    t.setTextAlignment(TextAlignment.JUSTIFY);
                    t.setFont(new Font(40));
                    t.setFill(Color.WHITE);
                    root.getChildren().add(t);
                }
                //Winner is Player Two End
                //No Winner Start
                if (winner == 3)
                {
                    Text t = new Text(938, 476, "Draw");
                    t.setWrappingWidth(200);
                    t.setTextAlignment(TextAlignment.JUSTIFY);
                    t.setFont(new Font(40));
                    t.setFill(Color.WHITE);
                    root.getChildren().add(t);
                }
                //No Winner End
            }
        });
        //End Trading Out Cards

        //Display Window Start
        primaryStage.setTitle("Poker Network");
        primaryStage.setScene(new Scene(root, 1876, 953));
        primaryStage.show();
        //Display Window End
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
