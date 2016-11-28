/*
 * GUI Client for LytlePoker
 */
package Poker;

import java.util.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * Runs the client as an application. Looks for a server on localhost at port
 * 9090. Connects to receive message then exits. Adapted from code at:
 * http://cs.lmu.edu/~ray/notes/javanetexamples/
 *
 * @author bwwagner
 * @author jrlytle
 */
public class LPokerClient extends Application {

    protected String Server;
    
    static String[] handOne;
    static String[] handTwo;
    
    protected static ImageView[] handOneCards = fillHands();
    protected static ImageView[] handTwoCards = fillHands();

    protected String[][] h1 = new String[5][3];
    protected String[][] h2 = new String[5][3];
    
    boolean[] clickedHandOne = new boolean[5];
    boolean[] clickedHandTwo = new boolean[5];

    public void start(Stage primaryStage) {

        //int draw;
        
        for (int i = 0; i < handOne.length; i++)
        {
            handOneCards[i] = new ImageView(new Image(handOne[i]));  // TODO: Get Card from Server
            handTwoCards[i] = new ImageView(new Image(handTwo[i]));
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

        //Display Window Start
        primaryStage.setTitle("Poker Network");
        primaryStage.setScene(new Scene(root, 1876, 953));
        primaryStage.show();
        //Display Window End

        //Server Address Entry
        final TextInputDialog ipAddr = new TextInputDialog("localhost");

        //Display IP Address Dialog
        ipAddr.setTitle("Connect to Poker Server");
        ipAddr.setHeaderText("Server IP Address");
        ipAddr.setContentText("Please enter the server's IP Address:");
        //End Display IP Address Dialog

        //TODO use this result to choose server IP
        //Get the response value.
        Optional<String> result = ipAddr.showAndWait();
        if (result.isPresent()) {
            System.out.println("Your choice: " + result.get());
        }
        //end Server Address Entry

        //Start Hand One and Hand Two Images
        for (int i = 0; i < handOneCards.length; i++) {
            handOneCards[i].relocate(400 + (i * 215), 590);
            root.getChildren().add(handOneCards[i]);
            
            handTwoCards[i].relocate(400 + (i * 215), 82);
            root.getChildren().add(handTwoCards[i]);
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
        
        
    }//end start

    public static void main(String[] args) throws IOException {
        boolean retry = true;
        String serverAddress = "127.0.0.1";
        while (retry) {
            Socket server = new Socket(serverAddress, 9090);
            try {
                BufferedReader input
                        = new BufferedReader(new InputStreamReader(server.getInputStream()));
                String answer = input.readLine();
                answer = answer.replaceAll("\\[|\\]", "");
                handOne = answer.split(", ");
                
                answer = input.readLine();
                answer = answer.replaceAll("\\[|\\]", "");
                handTwo = answer.split(", ");
                
                launch(args);
            } finally {
                server.close();
            }
        }
    }//main    

    public static ImageView[] fillHands() {
        ImageView[] hand = new ImageView[5];
        for (int i = 0; i < hand.length; i++) {
            hand[i] = new ImageView("file:./src/Resources/Images/Cards/Blank.png");
        }
        return hand;
    }

    public Image getCard(String[] card) {
        String fileName = "file:./src/Resources/Images/Cards/";
        for (int i = 0; i < card.length; i++) {
            fileName += card[i];
            if (i < card.length - 1) {
                fileName += "_";
            }
        }
        fileName += ".png";
        return new Image(fileName);
    }

}//LPokerClient
