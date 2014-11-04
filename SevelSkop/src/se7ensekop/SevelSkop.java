/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se7ensekop;

import java.util.ArrayList;

/**
 *
 * @author undeed
 */
public class SevelSkop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Game g = new Game();
        Player Player1 = new Player(g,"p1");
        Player Player2 = new Player(g,"p2");
        Player Player3 = new Player(g,"p3");
        Player Player4 = new Player(g,"p4");
        g.Play(Player1, Player2, Player3, Player4);
        
    }
}
