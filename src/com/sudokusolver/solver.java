package com.sudokusolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Ryan Sadeghi-Javid
 * January 19, 2022
 * Sudoku solving application. Enter the current digits of your sudoku board, and it will fill out the missing digits.
 * The software uses the Backtracking algorithm, which is built off recursive fundamentals.
 * The GUI is build on Java's swing.
 */

class solver implements ActionListener {

    // Application variable initialization.
    final static int SIZE = 9;
    static JFrame frame;
    static JTextField[] textField = new JTextField[81]; // 9*9=81, therefore 81 text-fields.
    static JPanel primePanel, gridPanel,buttonPanel; // Application panels
    static JButton solveButton, resetButton; // Application buttons
    static int[][] sudoku = new int[SIZE][SIZE]; // Matrix representation of board.
    static double time = 0.00;
    static boolean flag = false; // boolean to mark if the board is solved.

    public solver() // Main GUI class, setup on application's launch.
    {
        frame = new JFrame("Sudoku Solving Java Application");

        primePanel = new JPanel();
        primePanel.setLayout(new BoxLayout(primePanel,BoxLayout.Y_AXIS));

        gridPanel = new JPanel(new GridLayout(SIZE,SIZE));
        // Fills the GUI with empty text fields
        for(int i = 0 ; i < SIZE ; i++ ) {
            for(int j = 0 ; j < SIZE ; j++ )
            {
                textField[i*SIZE + j] = new JTextField("");
                gridPanel.add(textField[i*SIZE + j]);
            }
        }
        // Application buttons setup.
        buttonPanel = new JPanel();
        solveButton = new JButton("Solve Sudoku");
        solveButton.addActionListener(this);
        buttonPanel.add(solveButton);
        resetButton = new JButton("Reset Sudoku");
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);

        // Adds the buttons to the primary panel, in order to attach to the main frame.
        primePanel.add(gridPanel);
        primePanel.add(buttonPanel);

        // Adjusting settings and adding main components on the main frame.
        frame.add(primePanel);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(500,410);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent buttonClick) // Preforms the respective function when specific button is pressed.
    {
        if(buttonClick.getSource() == solveButton) // Solves the board.
        {
            solve();
            if(flag)// When the flag condition is triggered, outputs the completion time in place of the solve button.
                solveButton.setText("Solved! Time : "+time+" Seconds.");
            else
                solveButton.setText("Solve Sudoku");
        }
        if(buttonClick.getSource() == resetButton)
        {
            reset(); // Resets the board.
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

    private static void solve() // Waits for the board to be solved, or inform the user that it can not be solved.
    {
        long timeMill = System.currentTimeMillis(); // Stores the run time as a long in case of memory leaks (since it is being stored in milliseconds, value can be very large).

        makeSudoku(); // Moves values put into the GUI, to the applications main matrix.

        // Check if user-given Sudoku board is valid to solve.
        if(validate())
        {
            if(solveSudoku())
            {
                flag = true; // When board is solved, the flag condition will trigger the action listener to stop the process.
            }
            else
            {
                JOptionPane.showMessageDialog(frame,"Invalid sudoku. Please try Again. Time : "+time+" Sec.");
            }
        }
        else // If the board is invalid, it will prompt the user that it can not be solved.
        {
            JOptionPane.showMessageDialog(frame,"Invalid sudoku. Please try Again");
        }

        time = (System.currentTimeMillis() - timeMill)/1000.000; // Calculates the run time.
    }

    private static void makeSudoku() // Takes the values from the GUI sudoku board and stores it into a 9x9 matrix.
    {
        // Loops through all the GUI's values with a nested loop.
        for(int i=0; i < SIZE; i++)
        {
            for(int j=0; j < SIZE; j++)
            {
                if(((textField[i*SIZE + j]).getText()).equals(""))
                    sudoku[i][j] = 0 ;
                else
                    sudoku[i][j] = (int)((textField[i*SIZE + j]).getText().charAt(0)) - 48; // Converts the characters into an integers to store into the integer matrix, and preform functions on the matrix.
            }
        }
    }


    private static boolean solveSudoku() // Main backtracking, recursive function used to solve the board.
    {
        int row, col = 0;
        boolean blank = false;
        // Find unassigned location.
        for(row = 0; row < SIZE; row++)
        {
            for(col = 0; col < SIZE; col++)
            {
                if(sudoku[row][col] == 0)
                {
                    blank = true;
                    break;
                }
            }
            if(blank)
                break;
        }

        if(!blank) { // If no unassigned values.
            return true;
        }
        for(int n = 1; n <= SIZE; n++)
        {
            if(isSafe(row,col,n))
            {
                // Make assignment.
                sudoku[row][col] = n ;
                // Print output
                (textField[(row)*SIZE + col]).setText(Integer.toString(n));
                // Return if success.
                if(solveSudoku())
                    return true;
                // If it fails, undo and try again (backtrack).
                sudoku[row][col] = 0 ;
            }
        }

        // Trigger backtracking.
        return false;
    }


    private static boolean validate() // Checks if board can be solved.
    {
        for(int i=0; i<SIZE ;i++)
        {
            for(int j=0 ;j<SIZE; j++)
            {
                if(sudoku[i][j] < 0 && sudoku[i][j] > SIZE)
                    // Checking if value goes out of bounds.
                    return false; // Board can not be solved, stops process, informs readers with a promp through action listener.
                if(sudoku[i][j] != 0 && (usedInRow(i,j,sudoku[i][j]) || usedInCol(i,j,sudoku[i][j]) || usedInBox(i,j, sudoku[i][j])))
                {
                    // If value is 0, OR, the value is used in the row, column, or box the function will return 0.
                    return false; // Board can not be solved, stops process, informs readers with a promp through action listener.
                }
            }
        }
        return true; // Board can be solved, continues process of solving.
    }

    private static boolean isSafe(int r , int c , int n)
    {
        // Checks if a certain value is not used in a row, column or box, in that respective order.
        // Key function to backtracking, as it check if a values spot is safe to place.
        return (!usedInRow(r,c,n) && !usedInCol(r,c,n) && !usedInBox(r,c,n));
    }


    private static boolean usedInRow(int r, int c, int n) // Checks for a specific value in a certain row of the board.
    {
        for(int col=0; col<SIZE; col++ )
        {
            if(col != c && sudoku[r][col] == n)
            {
                return true; // Value found in row.
            }
        }
        return false; // Not found in row.

    }

    private static boolean usedInCol(int r,int c , int n) // Checks for a specific value in a certain column of the board.
    {
        for(int row=0 ; row < SIZE ; row++ )
        {
            if(row != r && sudoku[row][c] == n)
            {
                return true; // Value found in column.
            }
        }
        return false; // Not found in column.
    }

    private static boolean usedInBox(int r , int c , int n) // Checks for a specific value in a certain box (3x3 traditional sudoku) of the board.
    {
        // Accounting for matrix out of bounds.
        int rStart = r-r%((int)Math.sqrt(SIZE));
        int cStart = c-c%((int)Math.sqrt(SIZE));
        // Loops the square root of the board's size.
        for(int i=0; i < Math.sqrt(SIZE); i++ )
        {
            for(int j=0; j < Math.sqrt(SIZE); j++ )
            {
                if(rStart+i != r && cStart+j != c && sudoku[rStart+i][cStart+j] == n)
                {
                    return true; // Value found in 3x3 box.
                }
            }
        }
        return false; // Not found in 3x3 box.
    }

    public static void main(String[] args) throws FileNotFoundException {
        File instructions = new File("src/instructions.txt");
        Scanner myReader = new Scanner(instructions);
        JOptionPane.showMessageDialog(frame,myReader.nextLine()); // Reads text file once during the applications initial launch.
        new solver();
    }
}