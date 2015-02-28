package edu.jhu.iwilkes1.cs335.hw2.board;

import edu.jhu.iwilkes1.cs335.hw2.board.Chip;

/**
 * A class to represent a tile on a Konane board
 *
 * @author Ben Mitchell
 */
public class Tile {

  /**
   * The state of the tile
   */
  private Chip state;

  /**
   * Create a new tile
   *
   * @param chip what the occupancy of the new tile should be 
   */
  public Tile(Chip chip) {
    state = chip;
  }

  /* getter method for the state
   */
  public Chip getChip() {
    return state;
  }

  /**
   * setter method for the state
   *
   * @param chip what the new occupancy of the tile should be
   */
  public void setChip(Chip chip) {
    state = chip;
  }

  /**
   * Whether the tile is empty or occupied
   */
  public boolean isEmpty() {
    return state == Chip.NONE;
  }

  /**
   * Return a string (really just a character) representing the
   * state of the tile.
   */
  public String toString() {
    switch (this.state) {
      case BLACK:
        return new String("b");
      case WHITE:
        return "w";
      default:
        return ".";
    }
  }

}
