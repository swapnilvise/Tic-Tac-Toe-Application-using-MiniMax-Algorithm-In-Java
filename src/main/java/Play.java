import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class Play extends JFrame {
    static final long UID = 0;

    // Characters
    private final char MENACE = 'o';
    private final char HUMAN = 'x';
    private final char BLOCKED = 'b';

    // Timers
    private final int MIN_DELAY = 200;          // Delay before Menace shows move
    private final int MAX_MENACE_MOVES = 10000;   // Number of move tries before Menace gives up

    // GUI
    private JButton[][] displayGame;            // Game Board
    private TicTacToe TTT;                      // Tic Tac Toe Class

    private int maxLevelTree;                       // Max level of tree
    private Dictionary configurations;
    private int numblockedPosition;                  // Number of positions to block

    private int blockedPosition [];                  // Blocked Positions

    private ClickHandler handler;

    private int numCalls = 0;
    private boolean thinking = false;

    public Play (int size, int toWin, int depth, int num, int[] blocked) {

        Container c = getContentPane();
        c.setLayout(new GridLayout(size, size));
        displayGame = new JButton[size][size];
        Icon emptySquare = new ImageIcon("empty.gif");
        handler = new ClickHandler(size);

        /* Board is represented as a grid of clickable buttons */
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                displayGame[i][j] = new JButton("", emptySquare);
                displayGame[i][j].setEnabled(true);
                add(displayGame[i][j]);
                displayGame[i][j].addActionListener(handler);
            }

        // board_size = size;
        maxLevelTree = depth;
        numblockedPosition = num;
        blockedPosition = blocked;
        TTT = new TicTacToe(size, toWin, depth); /* User code needed to play */

    }


    public static void main(String[] args) {

        int size = 0;
        int toWin = 0;
        int depth = 0;

        // Check if correct params provided
        if (args.length < 3) {
            System.out.println("Usage: java Play <board-size> <to win>  <depth> <blocked positions ...>");
            System.exit(0);
        }

        // Attempt to assign initial game values
        try {
            /* Size of the game board */
            size = Integer.parseInt(args[0]);
            toWin = Integer.parseInt(args[1]);
            depth = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid program argument");
            System.exit(0);
        }

        // Block all BLOCKED positions
        int numOfBlocked = args.length - 3;

        int[] blocked = new int[numOfBlocked];

        for (int i = 0; i < numOfBlocked; ++i) {
            blocked[i] = Integer.parseInt(args[3 + i]) - 1;
            if ((blocked[i] < 0) || (blocked[i] >= size * size)) {
                System.out.println("Invalid board position " + blocked[i]);
                System.exit(0);
            }
        }

        // Create a GameBoard and Start the Game
        JFrame gameWindow = new Play(size, toWin, depth, numOfBlocked, blocked);

        // Set screen size
        gameWindow.setSize(size * 90, size * 100);
        gameWindow.setVisible(true);

        // Listen for closing events
        gameWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });

        // Randomly choose who goes first
        if (Math.random() > 0.5)
            ((Play)gameWindow).handler.displaymenacePlay();
    }

    /*
     * Panel to represent the game board. It contians methods for detecting the
     * position selected by the human player.
     */

    private class ClickHandler implements ActionListener {
        private int board_size;
        private boolean endGame = false;

        /* Constructor. Save board size in instance variable */
        public ClickHandler(int size) {
            board_size = size;
        }

        /*
         * When the user has selected a play, this method is invoked to process
         * the selected play
         */
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof JButton) { /*
             * Some position of the
             * board was selected
             */
                int row = -1, col = -1;

                if (endGame)
                    System.exit(0);
                /* Find out which position was selected by the player */
                for (int i = 0; i < board_size; i++) {
                    for (int j = 0; j < board_size; j++)
                        if (event.getSource() == displayGame[i][j]) {
                            row = i;
                            col = j;
                            break;
                        }
                    if (row != -1)
                        break;
                }

                if (TTT.squareIsEmpty(row, col)) {
                    /* Valid play, mark it on the board */
                    displayGame[row][col].setIcon(new ImageIcon("human.gif"));
                    displayGame[row][col].paint(displayGame[row][col].getGraphics());
                    Date date = new Date();

                    TTT.storePlay(row, col, HUMAN);
                    if (TTT.wins(HUMAN))
                        endGame("Human wins, Date : "+date.toString());
                    else {
                        if (TTT.isDraw())
                            endGame("Game is a draw, Date : "+date.toString());
                        else
                            displaymenacePlay();
                    }
                } else
                    System.out.println("Invalid play");

            }
        }

        private void displaymenacePlay() {
            PossiblePlay pos;

            Date d = new Date();
            long start = d.getTime();
            numCalls = 0;
            thinking = false;

            pos = menacePlay(MENACE, -1, 4, 0);

            if (thinking) System.out.println("");
            long end = d.getTime();
            try {
                if (end-start < MIN_DELAY)
                    Thread.sleep(MIN_DELAY-end+start);
            }
            catch (Exception e) {
                System.out.println("Something is wrong with timer");
            }

            TTT.storePlay(pos.getRow(), pos.getCol(), MENACE);
            displayGame[pos.getRow()][pos.getCol()].setIcon(new ImageIcon("computer.gif"));
            Date date = new Date();
            if (TTT.wins(MENACE))
                endGame("Menace wins, Date : "+date.toString());
            else if (TTT.isDraw())
                endGame("Game is a draw, Date : "+date.toString());
        }

        /* Explore the game tree and choose the best move for the computer */
        private PossiblePlay menacePlay(char symbol, int highest_score, int lowest_score, int level) {

            char opponent; // Opponent's symbol
            PossiblePlay reply; // Opponent's best reply

            int bestRow = -1;
            int bestColumn = -1; // Position of best play

            int value;
            int lookupVal;

            if (level == 0) /* Create new hash table */
                configurations = TTT.createDictionary();

            if (symbol == MENACE) {
                opponent = HUMAN;
                value = -1;
            } else {
                opponent = MENACE;
                value = 4;
            }

            if (++numCalls == MAX_MENACE_MOVES) {
                System.out.print("Please wait ..");
                thinking = true;
            }
            else if ((numCalls % MAX_MENACE_MOVES) == 0) System.out.print(".");


            // Scan entries of the game board in random order
            int row, column;
            row = (int)(Math.random() * board_size);

            for (int r = 0; r < board_size; r++) {
                column = (int)(Math.random() * board_size);
                for (int c = 0; c < board_size; c++) {
                    if (TTT.squareIsEmpty(row, column)) { // Empty position
                        TTT.storePlay(row, column, symbol); // Store next play
                        if (TTT.wins(symbol) || TTT.isDraw() || (level >= maxLevelTree))
                            // Game ending situation or max number of levels
                            // reached
                            reply = new PossiblePlay(TTT.evalBoard(), row, column);
                        else {
                            lookupVal = TTT.repeatedConfig(configurations);
                            if (lookupVal != -1)
                                reply = new PossiblePlay(lookupVal, row, column);
                            else {
                                reply = menacePlay(opponent, highest_score, lowest_score, level + 1);
                                if (TTT.repeatedConfig(configurations) == -1)
                                    TTT.insertConfig(configurations, reply.getScore(), 0);
                            }
                        }
                        TTT.storePlay(row, column, ' ');

                        if ((symbol == MENACE && reply.getScore() > value)
                                || (symbol == HUMAN && reply.getScore() < value)) {
                            bestRow = row;
                            bestColumn = column;
                            value = reply.getScore();

                            /* Alpha/beta cut */
                            if (symbol == MENACE && value > highest_score)
                                highest_score = value;
                            else if (symbol == HUMAN && value < lowest_score)
                                lowest_score = value;

                            if (highest_score >= lowest_score)
                                return new PossiblePlay(value, bestRow, bestColumn);
                        }

                    }
                    column = (column + 1) % board_size;
                }
                row = (row + 1) % board_size;
            }
            return new PossiblePlay(value, bestRow, bestColumn);
        }

        /* Prompt the user for a key to terminate the game */
        private void endGame(String mssg) {
            System.out.println(mssg);
            Logger logger = Logger.getLogger(Play.class.getName());
//            try {
//                LogManager.getLogManager().readConfiguration(new FileInputStream("mylogging.properties"));
//            } catch (SecurityException | IOException e1){
//                e1.printStackTrace();
//            }
            logger.setLevel(Level.FINE);
            logger.addHandler(new ConsoleHandler());
            try {
                Handler fileHandler = new FileHandler("C:/Users/swapn/Documents/Spring 2022/Program Structures and Algorithms/Final Project local/Final Projet-Menace Tic tac Toe/logger/logger.log",2000,50);
//                fileHandler.setFormatter(new MyFormatter());
//                fileHandler.setFilter(new MyFilter());
                logger.addHandler(fileHandler);
                logger.log(Level.INFO,mssg);
                logger.log(Level.CONFIG, "Config Data");
            }catch (SecurityException | IOException e) {
                e.printStackTrace();}
            System.out.println("");
            System.out.println("Click on board to terminate game");
            endGame = true;
        }

    }
}
