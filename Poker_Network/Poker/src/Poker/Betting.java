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
    
    //Blind betting mode
    private int largeBlind;
    private int smallBlind;
    
    public Betting(int newPot, int newAnte){
        jackpot = newPot;
        ante = newAnte;
    }  // end Constructor
    
    public int getLarge(){
        return this.largeBlind;
    }
    
    public int getSmall(){
        return this.smallBlind;
    }
    
    public int getAnte(){
        return this.ante;
    }
    
    public void setAnte(int amount){
        this.ante = amount;
    } // end setAnte
    
    public void bet(int amount){
        if (amount > 0) jackpot += amount;
        else return;
    }// end bet
    
} // end class Betting
