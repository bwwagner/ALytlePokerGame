/*
 * GUI Client for LytlePoker
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
import javafx.scene.control.TextInputDialog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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

    protected ImageView[] handOneCards = fillHands();
    protected ImageView[] handTwoCards = fillHands();

    protected String[][] h1 = new String[5][3];
    protected String[][] h2 = new String[5][3];

    //p Socket server = null;
    public void start(Stage primaryStage) {
        String[] handOne = new String[5];  // player's hand
        String[] handTwo = new String[5];  // opponent's hand
        Betting player1bets = new Betting(0, 0);
        Betting player2bets = new Betting(0, 0);

        String inMessage = this.Server;

        System.out.println(inMessage);
        //int draw;
        for (int i = 0; i < handOne.length; i++) {
            handOne[i] = "";  // TODO: Get Card from Server
            handTwo[i] = "";
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
        final TextInputDialog ipAddr = new TextInputDialog("192.168.1.2");

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

        String serverAddress = ipAddr.toString(); //need to fix this conversion

        try {
            Socket server = new Socket("localhost", 9090);
            BufferedReader input
                    = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String answer = input.readLine();
            System.out.print(answer);
        } catch (IOException e) {
            System.out.println("Connection Failed.");
        }

        //Start Hand One and Hand Two Images
        for (int i = 0;
                i < handOneCards.length;
                i++) {
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

    }//end start

    public static void main(String[] args) throws IOException {
        launch(args);

    }//main    

    public ImageView[] fillHands() {
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
