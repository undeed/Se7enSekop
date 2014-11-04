/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se7ensekop;

/**
 *
 * @author undeed
 */
public class Card {

    private int suit;
    private int rank;
    private int closedP;
    private static String[] suits = {"hearts", "spades", "diamonds", "clubs"};
    private static String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

    public static String rankAsString(int __rank) {
        return ranks[__rank];
    }

    Card(int suit, int rank) {
        this.rank = rank;
        this.suit = suit;
        this.closedP = 0;
    }

    public @Override
    String toString() {
        return ranks[rank] + " of " + suits[suit];
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }

    public void setRank(int i) {
        rank = i;
    }

    public int getClosedP() {
        return closedP;
    }

    public void setClosedP(int i) {
        closedP = i;
    }
}
