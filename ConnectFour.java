/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connectFour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author scott
 */
public class ConnectFour extends JPanel
{
    public static final int INFINITY = 999999;
    public static Color humanPlayer = Color.GREEN;
    public static Color computerPlayer = Color.RED;
    public static Color noPlayer = Color.WHITE;
    
    private BoardModel boardModel = new BoardModel();
    private JButton[][] board = new JButton[6][7];
    private JPanel[] boardRows = new JPanel[6];
    private JPanel boardPanel = new JPanel( new GridLayout(6,1) );
    private JPanel controlPanel = new JPanel( new GridLayout(3,1,0,5) );
    private JPanel messagePanel = new JPanel( new GridLayout(2,1) );
    
    private JButton startNewGameButton = new JButton();
    private JCheckBox humanMovesFirst = new JCheckBox();
    private JLabel title = new JLabel();
    private JLabel message = new JLabel();
    
    String[] difficultyLevels = { "easy", "medium", "hard" };
    private JComboBox difficultyLevelComboBox = new JComboBox( difficultyLevels );
    
    private volatile boolean terminateExecution = false;
    private Thread calculateComputerMoveExecutionThread;
    
    public ConnectFour()
    {
        // create board
        for( int row = 0; row < board.length; row++ )
        {
            for( int col = 0; col < board[row].length; col++ )
            {
                board[row][col] = new JButton();
                board[row][col].setPreferredSize( new Dimension(80,80) );
                board[row][col].setEnabled(false);
            }
        }
        
        // create panels to hold board rows
        for( int i = 0; i < boardRows.length; i++ )
        {
            boardRows[i] = new JPanel();
            
            for( int j = 0; j < board[i].length; j++ )
            {
                boardRows[i].add( board[i][j] );
            }
        }
        
        // add boardRows to boardPanel 
        for( int i = boardRows.length - 1; i > -1; i-- )
        {
            boardPanel.add( boardRows[i] );
        }
        
        // add button listeners
        for( int row = 0; row < board.length; row++ )
        {
            for( int col = 0; col < board[row].length; col++ )
            {
                final int passableCol = col;
                board[row][col].addActionListener( new ActionListener() {
                    @Override
                    public void actionPerformed( ActionEvent event )
                    {
                        // only buttons which represent legal moves will be enabled
                        // so we may assume that we are handling a legal move
          
                        // make human move
                        boardModel.makeMove( passableCol, ConnectFour.humanPlayer );
                        displayBoardModel(); // show human move
                        disableBoardForHumanInput();
                        
                        if( boardModel.isGameOver() ) { gameOver(); }
                        else
                        {  
                            // make computer's response move

                            message.setText( "I'm thinking..." );

                            calculateComputerMoveExecutionThread = new Thread() {
                                @Override
                                public void run() { makeComputerMove(); }
                            };

                            calculateComputerMoveExecutionThread.start();
                        }
                       
                    } // end method actionPerformed
                });
            }
        }
           
        
        // initialize controlPanel components
        
        startNewGameButton.setText( "start new game" );
        startNewGameButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent event )
            {
                if( calculateComputerMoveExecutionThread != null && calculateComputerMoveExecutionThread.isAlive() )
                {
                    terminateExecution = true;
                    while( calculateComputerMoveExecutionThread.isAlive() ) {} // wait for execution to stop
                }
                
                startNewGame();  
            }
        });
             
        humanMovesFirst.setText( "human moves first" );
        humanMovesFirst.setSelected(true);
        difficultyLevelComboBox.setEditable(false); 
        difficultyLevelComboBox.setSelectedIndex(1);
        
        
        controlPanel.add( startNewGameButton );
        controlPanel.add( humanMovesFirst );
        controlPanel.add( difficultyLevelComboBox );
        
        
        // initialize messagePanel components
        title.setText( "Welcome to Connect Four!" );
        title.setFont( new Font("DejaVu Sans", Font.BOLD, 18) );
        
        message.setFont( new Font("DejaVu Sans", Font.PLAIN, 14) );
        
        messagePanel.add( title );
        messagePanel.add( message );
        
        
        // add components to GamePanel and set preferred size
        JPanel a = new JPanel( new GridLayout(1,2,55,0) );
        a.add( messagePanel );
        a.add( controlPanel );
        
        add( a );
        add( boardPanel );
        
        setPreferredSize( new Dimension(660,660) ); 
        
    } // end construtor ConnectFour
    
    
    
    // assumes all board buttons are disabled before called
    public void enableBoardForHumanInput()
    { 
        // enable board buttons corresponding to legal moves
        for( int col = 0; col < board[0].length; col++ )
        {
            try { board[ boardModel.nextOpenSpot[col] ][col].setEnabled(true); }
            catch( ArrayIndexOutOfBoundsException e )
            { /* this happened because col is not a legal move */ }     
        }
    }
    
    // disables all board buttons
    public void disableBoardForHumanInput()
    {   
        for( int row = 0; row < board.length; row++ )
        {
            for( int col = 0; col < board[row].length; col++ )
            {
                board[row][col].setEnabled(false);
            }
        }
    }
    
    public void displayBoardModel()
    {
        for( int row = 0; row < board.length; row++ )
        {
            for( int col = 0; col < board[row].length; col++ )
            {
                board[row][col].setBackground( boardModel.getPlayer(row, col) );
                board[row][col].setOpaque(true);

            }
        }  
    }
    
    
    private void gameOver()
    {
        disableBoardForHumanInput();
        
        Color winner = boardModel.findWinner();
        
        if( winner == computerPlayer ) { message.setText( "I win!" ); }
        else if( winner == humanPlayer ) { message.setText( "You win!" ); }
        else { message.setText( "It's a draw!" ); }      
    }
    
    
    
    public void startNewGame()
    {   
        terminateExecution = false;
        boardModel.initializeBoard();
        disableBoardForHumanInput();
        
        displayBoardModel();
        
        if( humanMovesFirst.isSelected() )
        { 
            enableBoardForHumanInput();
            message.setText( "Your turn." );
        }
        else
        {
            message.setText( "I'm thinking..." );
            
            calculateComputerMoveExecutionThread = new Thread()
            {
                @Override
                public void run() { makeComputerMove(); }
            };
            
            calculateComputerMoveExecutionThread.start();
        }
        
    } // end method startNewGame  
     
    
    private void makeComputerMove()
    {
        int move = -1;
        int depth = 1;
        if( difficultyLevelComboBox.getSelectedIndex() == 0 ) { depth = 4; }
        else if( difficultyLevelComboBox.getSelectedIndex() == 1 ) { depth = 8;  }
        else { depth = 10; }
        
      
        int[] legalMoves = boardModel.getLegalMoves();
        
        // if there is a winning move, find it
        if( move == -1 )
        {
            for( int i = 0; i < legalMoves.length; i++ )
            {
                boardModel.makeMove( legalMoves[i], computerPlayer );
                if( boardModel.findWinner() == computerPlayer ) { move = legalMoves[i]; }
                boardModel.unmove( legalMoves[i] );
            }
        }
        
        // if there wasn't a winning move, try to find a blocking move
        if( move == -1 )
        {
            for( int i = 0; i < legalMoves.length; i++ )
            {
                boardModel.makeMove( legalMoves[i], humanPlayer );
                if( boardModel.findWinner() == humanPlayer ) { move = legalMoves[i]; }
                boardModel.unmove( legalMoves[i] );
            }
        }
        
        // if no move has been found yet, calculate the best move
        if( move == -1 )
        {
            ValueMove a;        
       
            // if terminateExecution is set to true, alphaBeta might end prematurely
            // and a might be set to null
            a = alphaBeta( computerPlayer,  -INFINITY, INFINITY, depth ); 
            
            if( a != null ) { move = a.getMove(); }
        }
        
        if( !terminateExecution )
        {
            final int passableMove = move;
            java.awt.EventQueue.invokeLater( new Runnable()
            { 
                @Override
                public void run()
                {
                    boardModel.makeMove( passableMove, computerPlayer );
                    displayBoardModel(); // show computer move

                    if( boardModel.isGameOver() ) { gameOver(); }
                    else
                    {
                        message.setText( "Your turn." );
                        enableBoardForHumanInput();
                    }
                }
            });
        }
        
    } // end method makeComputerMove
    
    
    // computer is max player, human is min player
    // alpha holds the max value the computer has as an option
    // beta holds the min value the human has as an option 
    private ValueMove alphaBeta( Color player, int alpha, int beta, int depth )
    {
        if( terminateExecution ) { return null; }
        
        if( boardModel.isGameOver() || depth == 1 )
        {
            return new ValueMove( boardModel.boardValue(), 0 );
        }
        
        int alphaMove = 0;
        int betaMove = 0;
        
        int[] legalMoves = boardModel.getLegalMoves();
        
        // if max player
        if( player == computerPlayer )
        {
            alpha = -INFINITY;
            for( int i = 0; i < legalMoves.length; i++ )
            {
                boardModel.makeMove( legalMoves[i], computerPlayer );
                ValueMove a = alphaBeta( humanPlayer, alpha, beta, depth - 1 );
                if( terminateExecution ) { return null; }
                boardModel.unmove( legalMoves[i] );
                
                if( a.getValue() > alpha )
                {
                    alpha = a.getValue();
                    alphaMove = legalMoves[i];
                }
                
                if( alpha >= beta )
                {
                    return new ValueMove( alpha, alphaMove );
                }   
            }
            
            return new ValueMove( alpha, alphaMove );
        }
        
        // if min player
        if( player == humanPlayer )
        {
            beta = INFINITY;
            for( int i = 0; i < legalMoves.length; i++)
            {
                boardModel.makeMove( legalMoves[i], humanPlayer );
                ValueMove a = alphaBeta( computerPlayer, alpha, beta, depth - 1 );
                if( terminateExecution ) { return null; }
                boardModel.unmove( legalMoves[i] );
                
                if( a.getValue() < beta )
                {
                    beta = a.getValue();
                    betaMove = legalMoves[i];
                }
                
                if( alpha >= beta )
                {
                    return new ValueMove( beta, betaMove );
                }   
            }
            
            return new ValueMove( beta, betaMove );
        }
        
        return null;
        
    } // end method alphaBeta
    
    public final class ValueMove
     {
         public final int value;
         public final int move;
         
         public ValueMove( int value, int move )
         {
             this.value = value;
             this.move = move;
         }
         
         public int getValue() { return value; }
         public int getMove() { return move; }
    } // end ValueMove class
    
} // end class GamePanel

