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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


/**
 * Runs the client as an application. Looks for a server on localhost at port
 * 9090. Connects to receive message then exits. 
 * Adapted from code at: http://cs.lmu.edu/~ray/notes/javanetexamples/
 *
 * @author bwwagner
 */
public class LPokerClient extends Application{

    public void start(Stage primaryStage)
    {
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
        
    }//end start
    
    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        Socket s = new Socket(serverAddress, 9090);
        BufferedReader input
                = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String answer = input.readLine();
        
        launch(args);

        //JOptionPane.showMessageDialog(null, answer);
        //System.exit(0);
    }//main    

}//LPokerClient
