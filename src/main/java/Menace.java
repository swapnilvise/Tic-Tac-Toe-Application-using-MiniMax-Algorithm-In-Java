import java.util.Hashtable;
import java.util.Scanner;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Random;
import org.bson.Document;

public class Menace {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Hashtable<String,String> ht = new Hashtable<>();


//        try {
            MongoClient client = MongoClients.create("mongodb+srv://menaceuser:Psa6205Menace@cluster0.gn66j.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
            MongoDatabase db = client.getDatabase("menaceDB");
            MongoCollection coll = db.getCollection("menace");
            System.out.println("Connection to MongoDB Atlas successfully established");
//        } catch (Exception e){
//            System.out.println(e);
//        }

        // Get the player's name
        System.out.print("Hello Player 1, what is your name ?");
//        String p1 = input.nextLine();
        String p1 = "Jarvis";

        System.out.print("Hello Player 2, what is your name ?");
//        String p2 = input.nextLine();
        String p2 = "Alexa";

        //3*3 Tic Tac Toe board
        // - denotes Empty space
        // X is player 1
        // O is player 2
        char[][] board = new char[3][3];

        // Fill in the board with dashes
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }

        //Variable to keep track if it is their turn or it is the Menace's turn
        boolean isPlayer1 = true;

        // Keep track if game has ended
        boolean gameEnded = false;

        while (!gameEnded){
            //Draw the board
            drawBoard(board);

            //Variable to keep track of what symbol we are using to play
            char symbol = ' ';

            if (isPlayer1) {
                symbol = 'X';
            } else {
                symbol = 'O';
            }

            // Print out the player's turn
            if (isPlayer1) {
                System.out.println(p1 + " it's your turn(X).");
            }

            // row and col variable declaration
            int row = 0;
            int col = 0;

            while (true) {
                Random random = new Random();
                // Get row and column from user
                System.out.println("Enter a row (0, 1 or 2) : ");
//                row = input.nextInt();
                row = random.nextInt(3);
                System.out.println(row);
                System.out.println("Enter a column (0, 1 or 2) : ");
//                col = input.nextInt();
                col = random.nextInt(3);
                System.out.println(col);

                // Check if row and column are valid
                if (row < 0 || col < 0 || row > 2 || col > 2) {
                    System.out.println("Entered Row and Column are out of bounds!");
                } else if (board[row][col] != '-') {
                    System.out.println("That move has already been made.");
                } else {
                    // Row and column are valid
                    String combination = "";
                    for (int i = 0; i<3 ; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (i==0 && j==0){
                                combination = String.valueOf(board[i][j]);
                                System.out.println(combination);
                            } else {
                                combination = combination + String.valueOf(board[i][j]);
                                System.out.println(combination);
                            }
                        }
                    }
                    ht.put(combination,"");
                    System.out.println(ht);
//                    Document sampleComb = new Document().append()
//                    coll.insertOne()
                    break;
                }
            }

            // Setting the position of the row and columns as per the player's move
            board[row][col] = symbol;
//            drawBoard(board);

            if (hasWon(board) == 'X') {
                // Player 1 has won
                System.out.println("Congratulations " + p1 + " has won the game! ");
                gameEnded = true;
            } else if (hasWon(board) == 'O') {
                // Menace won
                System.out.println("Menace won the game! ");
                gameEnded = true;
            } else {
                // Nobody has won
                if (hasTied(board)) {
                    //Tied
                    System.out.println("It's a tie");
                    gameEnded = true;
                } else {
                    // Continue the game and tiggles the turn
                    isPlayer1 = !isPlayer1;
                }
            }

        }
        // Print out the final board
        drawBoard(board);
    }

    // Printing out the tic tac toe board
    public static void drawBoard(char[][] board) {
        for (int i = 0; i<3 ; i++){
            for (int j =0;j<3;j++){
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    // This function will return which symbol has won the game, which will help us detrmine the winning player
    public static char hasWon(char[][] board) {
        //Checking for row combination
        for(int i=0; i<3;i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != '-') {
                return board[i][0];
            }
        }
        //Checking for column combination
        for (int j=0; j<3; j++) {
            if (board[0][j] == board[1][j] && board[1][j] == board[2][j] && board[0][j] != '-') {
                return board[0][j];
            }
        }

        //Checking for diagonal combination
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[1][1] != '-'){
            return board[1][1];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]){
            return board[1][1];
        }

        // Nobody has won
        return '-';
    }

    // Check if the board is full
    public static boolean hasTied(char[][] board){
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (board[i][j] == '-'){
                    return false;
                }
            }
        }
        return true;
    }

}
