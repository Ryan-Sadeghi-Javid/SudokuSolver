package com.sudokusolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Ryan Sadeghi-Javid
 * January 19, 2022
 * Sudoku solving application. Enter the current digits of your sudoku board, and it will fill out the missing digits
 */

class solver implements ActionListener {

    // Application variable initialization.
    final static int SIZE = 9;
    static JFrame frame;
    static JTextField[] textField = new JTextField[81]; // 9*9=81, therefore 81 text-fields.
    JPanel primePanel, gridPanel,buttonPanel;
    static JButton solveButton, resetButton;
    static int[][] sudoku = new int[SIZE][SIZE];
    static double time = 0.00;
    static boolean flag = false;

    public solver() // GUI setup on launch.
    {
        frame = new JFrame("Sudoku Solving Java Application");

        primePanel = new JPanel();
        primePanel.setLayout(new BoxLayout(primePanel,BoxLayout.Y_AXIS));

        gridPanel = new JPanel(new GridLayout(SIZE,SIZE));

        for(int i = 0 ; i < SIZE ; i++ )
        {
            for(int j = 0 ; j < SIZE ; j++ )
            {
                textField[i*SIZE + j] = new JTextField("");
                gridPanel.add(textField[i*SIZE + j]);
            }
        }

        buttonPanel = new JPanel();

        solveButton = new JButton("Solve Sudoku");
        solveButton.addActionListener(this);

        buttonPanel.add(solveButton);

        resetButton = new JButton("Reset Sudoku");
        resetButton.addActionListener(this);

        buttonPanel.add(resetButton);

        primePanel.add(gridPanel);
        primePanel.add(buttonPanel);

        frame.add(primePanel);

        frame.pack();
        frame.setVisible(true);
        frame.setSize(500,410);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) // Preforms the respective function when certain button is pressed.
    {
        if( e.getSource() == solveButton) // Solves the board.
        {
            solve();
            if(flag)
                solveButton.setText("Solved! Time : "+time+" Seconds."); // Outputs the solve time to the solve buttons text field.
            else
                solveButton.setText("Solve Sudoku");
        }

        if( e.getSource() == resetButton )
        {
            reset();
        }
    }


    private static void reset() // Resets sudoku board to empty state.
    {
        for(int i = 0 ; i < SIZE*SIZE ; i++ )
        {
            textField[i].setText(""); // Sets the text fields to an empty string.
        }

        solveButton.setText("Solve Sudoku");

        JOptionPane.showMessageDialog(frame, "Sudoku Successfully Reset.");
    }

    private static void solve() //
    {
        long timeMill = System.currentTimeMillis(); // Stores the run time as a long in case of memory leaks (since it is being stored in milliseconds).

        makeSudoku();

        if(validate())
        {
            if(solveSudoku())
            {
                flag = true;
            }
            else
            {
                JOptionPane.showMessageDialog(frame,"Invalid sudoku. Please try Again. Time : "+time+" Sec.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(frame,"Invalid sudoku. Please try Again");
        }

        time = (System.currentTimeMillis() - timeMill)/1000.000 ;
    }

    private static void makeSudoku()
    {
        for(int i=0 ; i< SIZE ; i++ )
        {
            for(int j=0 ; j< SIZE ; j++ )
            {
                if( ( (textField[i*SIZE + j]).getText() ).equals("") )
                    sudoku[i][j] = 0 ;
                else
                    sudoku[i][j] = (int)( (textField[i*SIZE + j]).getText().charAt(0) ) - 48;
            }
        }
    }


    private static boolean solveSudoku()
    {
        int row = 0 , col = 0;
        boolean f = false;
        //find unassigned location
        for( row = 0 ; row < SIZE ; row++ )
        {
            for( col = 0 ; col < SIZE ; col++ )
            {
                if( sudoku[row][col] == 0 )
                {
                    f = true;
                    break;
                }
            }
            if(f)
                break;
        }

        if(!f) { // If no unassigned values
            return true;
        }
        for(int n = 1 ; n <= SIZE ; n++ )
        {
            if(isSafe(row,col,n))
            {
                // Make assignment
                sudoku[row][col] = n ;
                // print output
                (textField[(row)*SIZE + col]).setText(Integer.toString(n));

                // return if success
                if(solveSudoku())
                    return true;
                // if fail , undo and try again
                sudoku[row][col] = 0 ;
            }
        }

        //trigger backtracking
        return false ;
    }


    private static boolean validate()
    {
        for(int i=0 ; i<SIZE ; i++ )
        {
            for(int j=0 ; j<SIZE ; j++ )
            {
                if( sudoku[i][j] < 0 && sudoku[i][j] > SIZE )
                    return false ;

                if( sudoku[i][j] != 0 && (usedInRow(i,j,sudoku[i][j]) || usedInCol(i,j,sudoku[i][j]) || usedInBox(i,j, sudoku[i][j]) ) )
                {
                    return false ;
                }
            }
        }

        return true ;
    }


    private static boolean isSafe(int r , int c , int n)
    {
        return ( !usedInRow(r,c,n) && !usedInCol(r,c,n) && !usedInBox(r,c,n) ) ;
    }


    private static boolean usedInRow(int r , int c, int n)
    {
        for(int col=0 ; col<SIZE ; col++ )
        {
            if( col != c && sudoku[r][col] == n )
            {
                return true;
            }
        }


        return false;
    }


    private static boolean usedInCol(int r,int c , int n)
    {
        for(int row=0 ; row < SIZE ; row++ )
        {
            if( row != r && sudoku[row][c] == n )
            {
                return true;
            }
        }

        return false;
    }


    private static boolean usedInBox(int r , int c , int n)
    {
        int r_st = r-r%((int)Math.sqrt(SIZE)) ;
        int c_st = c-c%((int)Math.sqrt(SIZE)) ;

        for(int i=0 ; i< (int)Math.sqrt(SIZE) ; i++ )
        {
            for(int j=0 ; j< (int)Math.sqrt(SIZE) ; j++ )
            {
                if( r_st+i != r && c_st+j != c && sudoku[r_st+i][c_st+j] == n )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        new solver();
    }
}