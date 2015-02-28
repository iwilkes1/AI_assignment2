package edu.jhu.iwilkes1.cs335.hw2.players;

import java.util.Scanner;
import java.util.ArrayList;

import edu.jhu.iwilkes1.cs335.hw2.board.Board;
import edu.jhu.iwilkes1.cs335.hw2.board.InvalidMoveException;
import edu.jhu.iwilkes1.cs335.hw2.board.Move;
import edu.jhu.iwilkes1.cs335.hw2.players.Player;

/** Human Player class for Konane game
 *
 * @author Ben Mitchell
 */
public class HumanPlayer extends Player {

  /** 
   * prompts a human player to enter a valid move; doesn't return
   * until it's got one thats legal.
   *
   * @param game the current game state; need this to check what moves are legal
   */
  public Move getMove(Board game) {

    /*XXX be sure not to actually modify "game" in here!
     *    in particular, do *not* "apply" your move; the driver
     *    loop in main() handles that. If you do it here, it will
     *    try to do it again, which (since it will no longer be legal),
     *    will cause an InvalidMoveException to get thrown.
     */

    Scanner stdin = new Scanner(System.in);
    Move ret = new Move(-1, -1, -1, -1);
    int r1, c1, r2, c2;
    boolean valid = false;

    ArrayList<Move> moveList = game.getLegalMoves();

    /* tell the player what her options are */
    System.out.println("Turn " + game.getTurn() + ", legal moves (" + moveList.size() + "): ");
    for(Move m : moveList) {
      System.out.println(m.toString());
    }

    /* keep asking for input until we get a legal move */
    while (!valid) {
      System.out.print("Enter a move (r1 c1 r2 c2): ");
      try {
        r1 = stdin.nextInt();
        c1 = stdin.nextInt();
        r2 = stdin.nextInt();
        c2 = stdin.nextInt();
        ret = new Move(r1, c1, r2, c2);

        try {
          /* testMove() is slower than legalMove(), but gives us helpful 
           * exception messages we can show the user if there's a problem */
          /* DO NOT USE executeMove() in getMove() */
          game.testMove(ret);  
          valid = true;  // if we don't get an exception, the move is legal
        } catch (InvalidMoveException e) {
          System.out.println("Move " + ret + " is illegal: \n\t" + e.getMessage());
        }
      } catch (Exception e) {
        System.out.println("bad input, expected: <int> <int> <int> <int>");
        System.out.println("\t(" + e + ")");
        String bad = stdin.nextLine();  // clear any remaining (bad) input
      }
    }


    return ret;
  }

}
