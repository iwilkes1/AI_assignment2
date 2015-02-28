package edu.jhu.iwilkes1.cs335.hw2.board;

import edu.jhu.iwilkes1.cs335.hw2.board.Point;

/** Class to represent a move in the game of Konane
 *
 * @author Ben Mitchell
 */
public class Move {
  private Point from;  // initial coordinates
  private Point to;  // final coordinates

  /**
   * construct a Move
   *
   * @param start, initial point
   * @param end, final point
   */
  public Move(Point start, Point end) {
    from = start;
    to = end;
  }

  /**
   * construct a Move
   *
   * @param r1 row, initial
   * @param c1 column, initial
   * @param r2 row, final
   * @param c2 column, final
   */
  public Move(int r1, int c1, int r2, int c2) {
    from = new Point(r1, c1);
    to = new Point(r2, c2);
  }

  /**
   * getter for initial point
   */
  public Point pointFrom() {
    return from;
  }

  /**
   * getter for final point
   */
  public Point pointTo() {
    return to;
  }

  /**
   * get a printable form of this move
   */
  public String toString() {
    return from + " -> " + to;
  }

  /** equality test for two moves
   *
   * @param o the object (hopefully a Move) to compare
   */
  public boolean equals(Object o) {
    Move cmp = (Move) o; // try to cast, let the JRE sort it out...

    return (this.from.equals(cmp.from) && this.to.equals(cmp.to));
  }

}
