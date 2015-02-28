package edu.jhu.iwilkes1.cs335.hw2.board;

/** Exception that indicates someone tried to make an invalid Konane move 
 *
 * @author Ben Mitchell
 */
public class InvalidMoveException extends Exception {

	private static final long serialVersionUID = 1L;

  public InvalidMoveException(String str) {
    super(str);
  }

}
