/*
 * Betting class and methods for ALytlePokerGame
 */
package Poker;

/**
 *
 * @author bwwagner
 */
public class Betting {
    private int jackpot = 0;
    private int ante = 0;
    private int myPot = 0;
    
    //Blind betting mode
    private int largeBlind;
    private int smallBlind;
    
    public Betting(int newPot, int newAnte){
        //Constructor used for Ante Betting Mode
        jackpot = newPot;
        ante = newAnte;
    }  // end Ante Constructor
    
    public Betting(int newPot, int newLargeBl, int newSmallBl){
        //Constructor used for Blind Betting Mode
        jackpot = newPot;
        largeBlind = newLargeBl;
        smallBlind = newSmallBl;
    } // end Blind Constructor
    
    public int getLarge(){
        return this.largeBlind;
    }
    
    public int getSmall(){
        return this.smallBlind;
    }
    
    public int getAnte(){
        return this.ante;
    }
    
    public int getBank(){
        return myPot;
    }
    
    public void setAnte(int amount){
        this.ante = amount;
    } // end setAnte
    
    public void winner(){
        this.myPot += this.jackpot;
        this.jackpot = 0;
    }
    
    public void bet(int amount){
        if (myPot > 0 && amount > 0 && amount <= myPot) {
            jackpot += amount;
            myPot -= amount;
        }
        else return;
    }// end bet
    
} // end class Betting
