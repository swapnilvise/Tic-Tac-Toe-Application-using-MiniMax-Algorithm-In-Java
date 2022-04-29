import javax.swing.*;

import com.sun.tools.javac.Main;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class MenaceBatchRun extends JFrame {

    // Test Case to check if Possible Play class is returning correct row and column
    @Test
    public void testPlay1() {

        PossiblePlay pp = new PossiblePlay(1,2,2);
        assertEquals(2,pp.getRow());
        assertEquals(2,pp.getCol());
        assertEquals(1,pp.getScore());
    }

    // Test Case to check that method Is Square Empty returns proper boolean value so that there is no overlap of a move
    // in the actual game
    @Test
    public void testPlay2() {
        TicTacToe TTT = new TicTacToe(3,3,3);
        boolean check = TTT.squareIsEmpty(1,1);
        assertEquals(true,check);
    }

    // Test case to check if wins method is returning false when the there is no win situation for symbol X
    @Test
    public void testPlay3() {
        TicTacToe TTT = new TicTacToe(3,3,3);
        boolean check = TTT.wins('X');
        assertEquals(false,check);
    }

    // Test Case to check the method of evaluating the board
    @Test
    public void testPlay4() {
        TicTacToe TTT = new TicTacToe(3,3,3);
        int boradValue = TTT.evalBoard();
        assertEquals(2,boradValue);
    }

}
