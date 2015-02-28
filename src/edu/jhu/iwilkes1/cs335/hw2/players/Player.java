package edu.jhu.iwilkes1.cs335.hw2.players;

import edu.jhu.iwilkes1.cs335.hw2.board.Board;
import edu.jhu.iwilkes1.cs335.hw2.board.Move;

/** abstract Player class for Konane game
 *
 * @author Ben Mitchell
 */
public abstract class Player {


  /**
   * Function to ask the player to provide a move; will be called by driver
   *
   * @param game the current state of play (for getting/checking legal moves)
   */
  public abstract Move getMove(Board game);
}
