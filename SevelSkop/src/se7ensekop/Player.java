/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se7ensekop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 *
 * @author undeed
 */
public class Player {

    private ArrayList<Card> hand;
    private ArrayList<Card> playable;
    private ArrayList<Card> closeable;
    private ArrayList<Card> closed;
    private String name;

    public Player(Game g, String name) {
        this.name = name;
        hand = new ArrayList<>();
        playable = new ArrayList<>();
        closeable = new ArrayList<>();
        closed = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            hand.add(g.drawFromDeck());
        }
//        displayAll();
        sortHand();
    }

    public void sortHand() {
        /*
         * sort the card at hand descending
         */
        ArrayList<Card> temp = (ArrayList<Card>) hand.clone();
        hand.clear();
        for (Card card : temp) {
            int i = 0;
            int j = 0;
            while (i < hand.size()) {
                j = i;
                if (card.getSuit() == hand.get(i).getSuit()) {
                    while (j < hand.size() && card.getRank() < hand.get(j).getRank() && card.getSuit() == hand.get(j).getSuit()) {
                        j++;
                    }
                    break;
                } else if (card.getSuit() < hand.get(i).getSuit()) {
                    break;
                }
                i++;
            }
            if (i == hand.size()) {
                hand.add(card);
            } else {
                hand.add(j, card);
            }
        }
    }

    void displayAll() {
        System.out.println("==================");
        System.out.println("player : " + name);
        for (Card card : hand) {
            System.out.println(card);
        }
    }

    void displayCloseable() {
        System.out.println("----closeable-----");
        for (Card card : closeable) {
            System.out.println(card + " " + card.getClosedP());
        }
    }

    void displayPlayable() {
        System.out.println("----playable-----");
        for (Card card : playable) {
            System.out.println(card);
        }
    }

    void displayClosed() {
        System.out.println("----closed " + name + "-----");
        for (Card card : closed) {
            System.out.println(card);
        }
    }

    public int getNumCard() {
        /*
         * return the number of card that still at hand
         */
        return hand.size() + playable.size() + closeable.size();
    }

    public void getPlayable(Game g) {
        /*
         * check the playable card at hand
         */

        //reset hand card
        for (Card card : playable) {
            hand.add(card);
        }
        playable.clear();

        for (Card card : closeable) {
            hand.add(card);
        }
        closeable.clear();
        changeAce(false);


        if (!g.getStarted()) {
            // if the game not started yet, playable only for 7 spade
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                if (card.getSuit() == 1 && card.getRank() == 6) {
                    playable.add(hand.remove(i));
                    break;
                }
            }
        } else {
            //if the game has started, check the playable hand according to the table
            for (int i = 0; i < hand.size(); i++) {
                //check each card with the corresponding suit
                Card card = hand.get(i);
                ArrayList<Card> temp = g.getPlayed()[card.getSuit()];
                if (temp.isEmpty()) {
                    //if the suit at table is empty, card 7 is playable
                    if (card.getRank() == 6) {
                        playable.add(hand.remove(i));
                        i--;
                    }
                } else {
                    //if the suit at table was already exist, check whether it's playable
                    if (card.getRank() == 0) {
                        // if the card is an ACE then check the possibility
                        // 1. if no suit has been closed and either 2 or king is present
                        // 2. if suit is closed at bottom and 2 is present
                        // 3. if suit is closed at above and king is present
                        if (((g.getPosition() == 0) && (temp.get(0).getRank() == 1 || temp.get(temp.size() - 1).getRank() == 12))
                                || ((g.getPosition() == -1) && (temp.get(0).getRank() == 1))
                                || ((g.getPosition() == 1) && (temp.get(temp.size() - 1).getRank() == 12))) {
                            playable.add(hand.remove(i));

                            // if the Ace will be closed at above, set rank = 13
                            if ((g.getPosition() == 1) || (g.getPosition() == 0 && temp.get(0).getRank() != 1)) {
                                card.setRank(13);
                            }
                            i--;
                        }
//                        if ((g.getPosition() == 0) && (temp.get(0).getRank() == 1 || temp.get(temp.size() - 1).getRank() == 12)) {
//                            playable.add(hand.remove(i));
//                        } else if ((g.getPosition() == -1) && (temp.get(0).getRank() == 1)) {
//                            playable.add(hand.remove(i));
//                        } else if ((g.getPosition() == 1) && (temp.get(temp.size() - 1).getRank() == 12)) {
//                            playable.add(hand.remove(i));
//                        }
                    } else {
                        // if the card is not an Ace, check whether its 1 rank below or above from cards at the table
                        if ((card.getRank() == temp.get(0).getRank() - 1) || (card.getRank() == temp.get(temp.size() - 1).getRank() + 1)) {
                            playable.add(hand.remove(i));
                            i--;
                        }
//                        else if (card.getRank() == 0 && temp.get(temp.size() - 1).getRank() == 12) {
//                        playable.add(hand.remove(i));
//                    }
                    }
                }
            }
        }

        if (playable.isEmpty()) {
            // if no card is playable, check what card is closeable
            if (g.getPosition() == 1 || g.getPosition() == 0) {
                // if game closed at above or not yet closed, set Ace as highest rank
                // so the probability of Aced being closed is minimum
                changeAce(true);
            }
            sortHand();
//            closeable.add(hand.remove(0));
            int prevSuit = -1;
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                if (g.getClosed(card.getSuit())) {
                    // if the suit was already closed, all card the same suit is closeable
                    closeable.add(hand.remove(i));
                    i--;
                } else {
                    // if the suit is still on play
                    if (card.getSuit() != prevSuit && card.getRank() > 6) {
                        // check if the card is the highest rank of the suit
                        // and is above 7
                        prevSuit = card.getSuit();
                        closeable.add(hand.remove(i));
                        i--;
                    } else if (i == hand.size() - 1 || card.getSuit() != hand.get(i + 1).getSuit()) {
                        // check if the card is the lowest of the suit
                        closeable.add(hand.remove(i));
                        i--;
                    }

                    // set priority to close the card
                    if (card.getRank() < 10 && card.getRank() > 6) {
                        card.setClosedP(1);
                    } else if (card.getRank() >= 10) {
                        card.setClosedP(3);
                    } else {
                        card.setClosedP(2);
                    }
                }
            }
            //return Ace as rank 0
            changeAce(false);
        }
    }

    public void changeAce(boolean up) {
        /*
         * true : change rank Ace = 13
         * false : change rank Ace = 0
         */
        if (up) {
            for (Card card : hand) {
                if (card.getRank() == 0) {
                    card.setRank(13);
                }
            }
        } else {
            for (Card card : hand) {
                if (card.getRank() == 13) {
                    card.setRank(0);
                }
            }
        }
    }

    public void sortCloseable() {
        /*
         * sort closeable card by priority
         */
        Collections.sort(closeable, new Comparator<Card>() {
            @Override
            public int compare(Card card1, Card card2) {
                if (card1.getClosedP() > card2.getClosedP()) {
                    return 1;
                } else if (card1.getClosedP() < card2.getClosedP()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

    }

    public void play(Game g) {
        System.out.println("player = " + name);

        getPlayable(g);
        displayPlayable();
        sortCloseable();
        displayCloseable();

        Card played;
        Random generator = new Random();
        if (playable.size() > 0) {
            g.setStart();
            // random select played card
            if (playable.size() == 1) {
                played = playable.remove(0);
            } else {
                played = playable.remove(generator.nextInt(playable.size() - 1));
            }

            // ask if 2 and king both present but position not yet determined
            // default = closed at below

            if (played.getRank() == 0) {
                g.setPosition(-1);
                System.out.println("bawah");
            } else if (played.getRank() == 13) {
                g.setPosition(1);
                System.out.println("atas");
            }

            System.out.println(name + " played " + played);
            g.addToPlayed(played.getSuit(), played);
        } else if (g.getStarted()) {
            System.out.println(name + " closed " + closeable.get(0));
            closed.add(closeable.remove(0));
        }
        System.out.println("============================");
    }

    public int countLose(Game g) {
        int tot = 0;
        for (Card card : closed) {
            if (card.getRank() == 0 || card.getRank() == 13) {
                if (g.getPosition() == 1) {
                    card.setRank(13);
                } else if (g.getPosition() == -1) {
                    card.setRank(0);
                } else if (g.getPosition() == 0) {
                    card.setRank(6);
                }
            }
            tot += card.getRank() + 1;
        }
        return tot;
    }
}
