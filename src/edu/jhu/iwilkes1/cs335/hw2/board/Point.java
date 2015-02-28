package edu.jhu.iwilkes1.cs335.hw2.board;

/** Class to represent a board location in Konane
 *
 * @author Ben Mitchell
 */
public class Point {

  private int row;
  private int col;

  /** constructor with row, col as input
   *
   * @param row,col row and column the Point refers to
   */
  public Point(int r, int c) {
    row = r;
    col = c;
  }

  /**
   * getter for row
   */
  public int row() {
    return row;
  }

  /**
   * getter for col
   */
  public int col() {
    return col;
  }

  /**
   * get a printable form of this point
   */
  public String toString() {
    return "(" + row + ", " + col + ")";
  }


  /** equality test for two points
   *
   * @param o the object (hopefully a Point) to compare
   */
  public boolean equals(Object o) {
    Point cmp = (Point) o; // try to cast, let the JRE sort it out...

    return (this.row == cmp.row && this.col == cmp.col);
  }


}
