/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connectFour;

import java.awt.Color;

/**
 *
 * @author scott
 */
public class BoardModel {
    
    private Color[][] boardModel = new Color[6][7];
    public int[] nextOpenSpot = new int[7];
    
    public BoardModel() { initializeBoard(); }
    
    public final void initializeBoard()
    {
        for( int row = 0; row < boardModel.length; row++ )
        {
            for( int col = 0; col < boardModel[row].length; col++ )
            {
                boardModel[row][col] = ConnectFour.noPlayer;
            }
        }
        
        for( int i = 0; i < nextOpenSpot.length; i++ )
        {
            nextOpenSpot[i] = 0;
        }
    }
    
    // assumes move is legal
    public void makeMove( int move, Color player )
    { 
        boardModel[ nextOpenSpot[move] ][move] = player;
        nextOpenSpot[move]++;
    }
   
    public void unmove( int move )
    {
        nextOpenSpot[move]--;
        boardModel[ nextOpenSpot[move] ][move] = ConnectFour.noPlayer;        
    }
    
    public boolean isBoardFull()
    {
        for( int i = 0; i < nextOpenSpot.length; i++ )
        {
            if( nextOpenSpot[i] < 6 ) { return false; }
        }
        
        return true;
    }
    
    public int[] getLegalMoves()
    {
        // find the number of legal moves
        // and create an array of that size
        int legalMovesCount = 0;
        for( int i = 0; i < nextOpenSpot.length; i++ )
        {
            if( nextOpenSpot[i] < 6 ) { legalMovesCount++; }
        }
        
        // create an array of legalMoves
        int[] legalMoves = new int[legalMovesCount];
        
        int legalMoveIndex = 0;
        for( int i = 0; i < nextOpenSpot.length; i++ )
        {
            if( nextOpenSpot[i] < 6 )
            {
                legalMoves[legalMoveIndex] = i;
                legalMoveIndex++;
            }
        }
        
        return legalMoves;
    }
    
    public boolean isGameOver()
    {
        if( isBoardFull() ) { return true; }
        if( findWinner() != ConnectFour.noPlayer ) { return true; }
        
        return false;
    }
    
    public Color getPlayer( int row, int col ) { return boardModel[row][col]; }
    
    // given four consecutive board positions, returns a value
    // the higher the value, the better a, b, c, d are to the computer
    // the lower the value, the better a, b, c, d are to the human
    private int findValue( Color a, Color b, Color c, Color d )
    {
        int computerCount = 0;
        int humanCount = 0;
        int playerCount;
        
        Color player;
        
        if( a == ConnectFour.computerPlayer ) { computerCount++; }
        if( a == ConnectFour.humanPlayer ) { humanCount++; }
        
        if( b == ConnectFour.computerPlayer ) { computerCount++; }
        if( b == ConnectFour.humanPlayer ) { humanCount++; }
        
        if( c == ConnectFour.computerPlayer ) { computerCount++; }
        if( c == ConnectFour.humanPlayer ) { humanCount++; }
        
        if( d == ConnectFour.computerPlayer ) { computerCount++; }
        if( d == ConnectFour.humanPlayer ) { humanCount++; }
        
        if( computerCount != 0 && humanCount != 0 ) { playerCount = 0; player = ConnectFour.noPlayer; }
        else if( computerCount != 0 && humanCount == 0 ) { playerCount = computerCount; player = ConnectFour.computerPlayer; }
        else if( computerCount == 0 && humanCount != 0 ) { playerCount = humanCount; player = ConnectFour.humanPlayer; }
        else { playerCount = 0; player = ConnectFour.noPlayer; }
        
        int value = 0;
        
        if( playerCount == 0 ) { value = 0; }
        else if( playerCount == 1 ) { value = 1; }
        else if( playerCount == 2 ) { value = 10; }
        else if( playerCount == 3 ) { value = 50; }
        else if( playerCount == 4 ) { value = 125; }
        
        if( player == ConnectFour.computerPlayer ) { return value; }
        else if( player == ConnectFour.humanPlayer ) { return -value; }
        else if( player == ConnectFour.noPlayer ) { return 0; }
        
        return 0;
    }
    
    // gives a value to the board
    // the higher the value, the better the board is for the computer
    // the lower the value, the better the board is for the human
    public int boardValue()
    {
        int value = 0;
        
        // check cols
        for( int col = 0; col < 7; col++ )
        {
            for( int row = 0; row < 3; row++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row+1][col];
                Color c = boardModel[row+2][col];
                Color d = boardModel[row+3][col];
                
                value += findValue(a,b,c,d);                
            }
        }
        
        // check rows
        for( int row = 0; row < 6; row++ )
        {
            for( int col = 0; col < 4; col++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row][col+1];
                Color c = boardModel[row][col+2];
                Color d = boardModel[row][col+3];
                
                value += findValue(a,b,c,d);
            }
        }
        
        // check main diagonals
        for( int row = 0; row < 3; row++ )
        {
            for( int col = 0; col < 4; col++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row+1][col+1];
                Color c = boardModel[row+2][col+2];
                Color d = boardModel[row+3][col+3];
                
                value += findValue(a,b,c,d);
            }
        }
        
        // check reverse diagonals
        for( int row = 0; row < 3; row++ )
        {
            for( int col = 3; col < 7; col++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row+1][col-1];
                Color c = boardModel[row+2][col-2];
                Color d = boardModel[row+3][col-3];
                
                value += findValue(a,b,c,d);
            }
        }
        
        return value;
        
    } // end method boardValue
    
    // assumes there is only one winner
    public Color findWinner()
    {
        
        // check cols
        for( int col = 0; col < 7; col++ )
        {
            for( int row = 0; row < 3; row++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row+1][col];
                Color c = boardModel[row+2][col];
                Color d = boardModel[row+3][col];
            
                if( a == b && a == c && a == d && a != ConnectFour.noPlayer ) { return a; }             
            }
        }
        
        // check rows
        for( int row = 0; row < 6; row++ )
        {
            for( int col = 0; col < 4; col++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row][col+1];
                Color c = boardModel[row][col+2];
                Color d = boardModel[row][col+3];
                
                if( a == b && a == c && a == d && a != ConnectFour.noPlayer ) { return a; } 
            }
        }
        
        // check main diagonals
        for( int row = 0; row < 3; row++ )
        {
            for( int col = 0; col < 4; col++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row+1][col+1];
                Color c = boardModel[row+2][col+2];
                Color d = boardModel[row+3][col+3];
                
                if( a == b && a == c && a == d && a != ConnectFour.noPlayer ) { return a; } 
            }
        }
        
        // check reverse diagonals
        for( int row = 0; row < 3; row++ )
        {
            for( int col = 3; col < 7; col++ )
            {
                Color a = boardModel[row][col];
                Color b = boardModel[row+1][col-1];
                Color c = boardModel[row+2][col-2];
                Color d = boardModel[row+3][col-3];
                
                if( a == b && a == c && a == d && a != ConnectFour.noPlayer ) { return a; } 
            }
        }
        
        return ConnectFour.noPlayer;
        
    } // end method findWinner
    
    
} // end class BoardModel