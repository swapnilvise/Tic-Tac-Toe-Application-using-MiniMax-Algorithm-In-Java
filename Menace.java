import java.util.Scanner;

public class Menace {

    public static void main(String[] args){
        Scanner input = new Scanner(System.in);

        // Get the player's name
        System.out.print("Hello Player 1, what is your name ?");
        String p1 = input.nextLine();

        System.out.print("Hello Player 2, what is your name ?");
        String p2 = input.nextLine();

        //3*3 Tic Tac Toe board
        // - denotes Empty space
        // X is player 1
        // O is player 2
        char[][] board = new char[3][3];

        // Fill in the board with dashes
        for (int i = 0; i<3 ; i++){
            for (int j =0;j<3;j++){
                board[i][j] = '-';
            }
        }

        //Draw the board
        drawBoard(board);

        //Variable to keep track if it is their turn or it is the Menace's turn
        boolean isPlayer1 = true;

        //Variable to keep track of what symbol we are using to play
        char symbol = ' ';
        if (symbol != 'X' || symbol !='O') {
            System.out.println("Only X and O are accepted in the Menace game, please choose one of these! :-)");
        }

        while (true) {
            // Get row and column from user
            System.out.print("Enter a row (0, 1 or 2) : ");
            int row = input.nextInt();
            System.out.print("Enter a column (0, 1 or 2) : ");
            int col = input.nextInt();

            // Check if row and column are valid
            if (row<0 || col<0 || row>2||col >2){
                System.out.print("Entered Row and Column are out of bounds!");
            } else if (board[row][col] != '-'){
                System.out.print("That move has already been made.");
            } else {
                // Row and column are valid
                break;
            }
        }
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
}
