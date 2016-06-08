/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connectFour;

import javax.swing.JFrame;

/**
 *
 * @author scott
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame( "Connect Four" );
        ConnectFour connectFour = new ConnectFour();
    
        frame.add( connectFour );
        frame.setVisible(true);
        frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        
        connectFour.startNewGame();
    }
}
