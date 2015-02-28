package edu.jhu.iwilkes1.cs335.hw2.board;

import java.util.ArrayList;

import edu.jhu.iwilkes1.cs335.hw2.board.InvalidMoveException;
import edu.jhu.iwilkes1.cs335.hw2.board.Tile;

/**
 * This class represents a gameboard for the game of Konane
 * @author Ben Mitchell, adapted by Ian Wilkes
 */
public class Board {
	// bounds on legal board size
	private static final int MIN_WIDTH = 4;
	private static final int MAX_WIDTH = 8;

	// values for board evaluation
	public static final double MAX_BOARD_VALUE = 1.0;
	public static final double MIN_BOARD_VALUE = -1.0;

	/**
	 * actual board tiles; row-major mapping to 2D array
	 */
	private Tile[] data;

	/**
	 * width (and also height) of the board
	 */
	private int width;

	/**
	 * tracks who's turn it is
	 */
	private boolean blackToPlay;

	/**
	 * tracks how many turns have elapsed
	 */
	private int turn;

	/** 
	 * list of valid moves; needs to be updated every turn, but the getter does that
	 */
	ArrayList<Move> moveList;

	/** 
	 * tracks whether the moveList is up-to-date
	 */
	private boolean moveListStale;

	/**
	 * Creates a new game board.
	 *
	 * @param size the size of the board (will be used for both X and Y size)
	 */
	public Board (int size) throws IllegalArgumentException {
		// range check
		if (size < MIN_WIDTH || size > MAX_WIDTH || (size % 2 > 0) ) {
			throw new IllegalArgumentException(
					"Attempt to construct an illegal board! " + "requested size of \"" +
							size + "\" must satisfy " + MIN_WIDTH + " <= size <= " + MAX_WIDTH + 
					" and be an even number."); 
		}

		this.width = size;

		// allocate data
		data = new Tile[width*width];
		blackToPlay = true;
		moveList = new ArrayList<Move>();
		moveListStale = true;

		// initialize tiles
		for (int i=0; i<width; i++) {
			for (int j=0; j<width; j++) {
				if ( (i + j) % 2 == 0 ) {  // alternating color tileing
					this.putTile(i, j, new Tile(Chip.BLACK));
				} else {
					this.putTile(i, j, new Tile(Chip.WHITE));
				}
			}
		}
	}

	/**
	 * Copy constructor for the board. Takes an old board and creates a new board.
	 * @param oldBoard
	 */
	public Board(Board oldBoard) {
		this.blackToPlay = oldBoard.blackToPlay;
		this.turn = oldBoard.turn;
		this.width = oldBoard.width;
		this.moveList = new ArrayList<Move>();
		this.moveListStale = true;
		this.data = new Tile[this.width * this.width];

		for (int i = 0; i < this.width *this.width; i++) {
			this.data[i] = new Tile(oldBoard.data[i].getChip());  
		}
	}

	/** 
	 * getter method for the board size
	 */
	public int getSize() {
		return width;
	}

	/**
	 * getter for turn counter
	 */
	public int getTurn() {
		return turn;
	}


	/**
	 * getter method for an indexed tile
	 *
	 * @param row the row index of the tile to retrieve
	 * @param col the column index of the tile to retrieve
	 */
	public Tile getTile(int row, int col) throws IndexOutOfBoundsException {
		// bounds check
		if ( row < 0 || row >= width || 
				col < 0 || col >= width ) 
		{
			throw new IndexOutOfBoundsException("row and col (\"" + row + "\", \"" + 
					col + "\") must satisfy: 0 <= index < " + width);
		}
		return data[ (row*width) + col ];
	}

	/**
	 * wrapper for getTile using a Point as input
	 *
	 * @param p the Point to get the tile for
	 */
	public Tile getTile(Point p) throws IndexOutOfBoundsException {
		return this.getTile(p.row(), p.col());
	}

	/**
	 * setter method for an indexed tile; non-public
	 *
	 * @param row the row index of the tile to retrieve
	 * @param col the column index of the tile to retrieve
	 */
	private void putTile(int row, int col, Tile t) throws IndexOutOfBoundsException {
		// bounds check
		if ( row < 0 || row >= width || 
				col < 0 || col >= width ) 
		{
			throw new IndexOutOfBoundsException("row and col (\"" + row + "\", \"" + 
					col + "\") must satisfy: 0 <= index < " + width);
		}
		data[ (row*width) + col ] = t;
	}

	/**
	 * setter method for an indexed tile; non-public
	 *
	 * @param row the row index of the tile to retrieve
	 * @param col the column index of the tile to retrieve
	 */
	private void changeTile(int row, int col, Chip c) throws IndexOutOfBoundsException {
		// bounds check
		if ( row < 0 || row >= width || 
				col < 0 || col >= width ) 
		{
			throw new IndexOutOfBoundsException("row and col (\"" + row + "\", \"" + 
					col + "\") must satisfy: 0 <= index < " + width);
		}
		data[ (row*width) + col ].setChip(c);
	}


	/**
	 * return a string representing the board; each tile is
	 * 'b', 'w', or '.' depending on occupancy
	 */
	public String toString() {
		String ret = "";

		for (int i=0; i<(width*2)+3; i++, ret+="-") 
			;
		ret += "\n";

		for (int i=0; i<width; i++) {
			ret += "| ";
			for (int j=0; j<width; j++) {
				ret += this.getTile(i, j).toString() + " ";
			}
			ret += "|\n";
		}

		for (int i=0; i<(width*2)+3; i++, ret+="-") 
			;

		return ret;
	}

	/**
	 * test whether the game is over, and return the color of the winning player
	 * (will return NONE if the game is not over)
	 */
	public Chip gameWon() {

		/* game is over when there are no legal moves for current player */
		if (getLegalMoves().size() == 0) {
			if (blackToPlay) { /* if black is out of moves, white wins */ 
				return Chip.WHITE;
			} else {
				return Chip.BLACK;
			}
		}

		/* otherwise, the game isn't over yet */
		return Chip.NONE;
	}

	/**
	 * returns a list of all the legal moves for the current player given the
	 * current state of the board
	 */
	public ArrayList<Move> getLegalMoves() {
		Chip friend, enemy;

		/* if we've already calculated moves this turn, return cached results */
		if (!moveListStale) {
			return moveList;
		}

		/* otherwise, clear the moveList and re-calculate for the current turn */
		moveList.clear();
		moveListStale = false;

		/* special case for first 2 turns */

		if (turn == 0) {  // first turn for black
			Point p;
			/* corner pieces */
			p = new Point(0, 0);
			moveList.add(new Move(p, p));
			p = new Point((width/2)-1, (width/2)-1);
			moveList.add(new Move(p, p));
			p = new Point(width/2, width/2);
			moveList.add(new Move(p, p));
			p = new Point(width-1, width-1);
			moveList.add(new Move(p, p));

			return moveList;
		} else if (turn == 1) {  // first turn for white
			/* scan board */
			for (int row=0; row<width; row++) {
				for (int col=0; col<width; col++) {
					/* look for empty square */
					if (this.getTile(row, col).getChip() == Chip.NONE) {
						/* tiles adjacent to an empty square are valid second moves */
						for (int rowChange=-1; rowChange<=1; rowChange++) {
							for (int colChange=-1; colChange<=1; colChange++) {
								if ((row+rowChange >= 0 && row+rowChange < width) && 
										(col+colChange >= 0 && col+colChange < width) && 
										(rowChange == 0 || colChange == 0) && 
										(rowChange != colChange)) 
								{  
									Point p = new Point(row+rowChange, col+colChange);
									moveList.add(new Move(p, p));
								}
							}
						}
					}
				}
			}

			return moveList;
		}

		/* general case */

		/* figure out which color belongs to the current player */
		if (blackToPlay) {
			friend = Chip.BLACK;
			enemy = Chip.WHITE;
		} else {
			friend = Chip.WHITE;
			enemy = Chip.BLACK;
		}

		/* loop over board */
		for (int row=0; row<width; row++) {
			for (int col=0; col<width; col++) {
				/* for each square, check if the piece is ours; if not, we can't touch it */
				if (this.getTile(row, col).getChip() == friend) {
					Point start, end;
					start = new Point(row, col);

					/* see if we can make jumps in each direction */
					for (int rowChange=-1; rowChange<=1; rowChange++) {
						for (int colChange=-1; colChange<=1; colChange++) {
							if ( (rowChange == 0 || colChange == 0) &&
									(rowChange != 0 || colChange != 0))
							{  // can only move in one direction at a time

								for (int r=row+rowChange, c=col+colChange, i=1; r<width; r+=rowChange, c+=colChange, i++) {

									if (r < 0 || r >= width || c < 0 || c >= width) {
										break;  // we're out of bounds
									}
									if (i%2 == 0) {  // ...a square we want to land in 
										if (getTile(r, c).getChip() == Chip.NONE) {
											end = new Point(r, c);  // if it's empty, the move is good
											moveList.add(new Move(start, end));
										} else {
											break;  // if it's not empty, we can't jump into it
										}
									} else {  // ...a square we want to jump over
										if (getTile(r, c).getChip() != enemy) {
											break;  // nothing to jump means we can't move this direction
										}
									}
								}

							}
						}
					}

				}
			}
		}

		return moveList;
	}



	/**
	 * test move to see if it's legal; throws an exception if it's invalid
	 *
	 * @param move the move to try to apply
	 * @see InvalidMoveException
	 */
	public void testMove(Move move) throws InvalidMoveException {
		tryMove(move, false);
	}

	/**
	 * test if a move is legal, return true or false (doesn't throw exceptions)
	 *
	 * @param move the move to test
	 */
	public boolean legalMove(Move move) {
		this.getLegalMoves();  // make sure move list isn't stale...
		return moveList.contains(move);  // ...then see if our move is in it
		/*
    try {
      tryMove(move, false);  // throws an exception if the move is illegal...
      return true;  // ...so if we get here, it's legal
    } catch (InvalidMoveException e) {
      System.out.println("Exception caught in legalMove(): " + e);
      return false;
    }
		 */
	}


	/**
	 * try to execute the move; throws an exception if an invalid move is tried
	 *
	 * @param move the move to try to apply
	 * @see InvalidMoveException
	 */
	public void executeMove(Move move) throws InvalidMoveException {
		tryMove(move, true);
	}

	/**
	 * private function to test moves for legality and optionally apply them
	 *
	 * @param move the move to test
	 * @param apply whether or not to apply the move if it's valid
	 * @see InvalidMoveException
	 */
	private void tryMove(Move move, boolean apply) throws InvalidMoveException {
		Chip friend, enemy;
		int row, col;
		int rowChange, colChange;

		/* special case for first 2 turns */
		if (turn == 0) {
			/* pieces that can be remvoved on first turn*/
			Point p1 = new Point(0, 0);
			Point p2 = new Point(width-1, width-1);
			Point p3 = new Point(width/2, width/2);
			Point p4 = new Point(width/2-1, width/2-1);

			if (! move.pointFrom().equals(move.pointTo())) {
				throw new InvalidMoveException("Error: first two moves must have matching from/to (remove a chip)");
			} else if ((move.pointFrom().equals(p1)) ||
					(move.pointFrom().equals(p2)) ||
					(move.pointFrom().equals(p3)) ||
					(move.pointFrom().equals(p4)))
			{
				if (apply) {
					changeTile(move.pointFrom().row(), move.pointFrom().col(), Chip.NONE);
					this.endTurn();
				}
				return;
			} else {
				throw new InvalidMoveException("Error: first move must take chip from corners or center");
			}

		} else if (turn == 1) {
			if (! move.pointFrom().equals(move.pointTo())) {
				throw new InvalidMoveException("Error: first two moves must have matching from/to (remove a chip)");
			} else {
				for (rowChange=-1; rowChange<=1; rowChange++) {
					row = move.pointFrom().row() + rowChange;
					for (colChange=-1; colChange<=1; colChange++) {
						col = move.pointFrom().col() + colChange;
						if ((row >= 0 && row < width && col >=0 && col < width) &&  // must be inside board
								(rowChange == 0 || colChange == 0) &&  // can only move in one direction at a time...
								(rowChange != 0 || colChange != 0))  // ...but need to move in *some* direction
						{
							if (this.getTile(row, col).getChip() == Chip.NONE) {
								/* if there's an adjacent empty square, the move is legal */
								if (apply) {
									changeTile(move.pointFrom().row(), move.pointFrom().col(), Chip.NONE);
									this.endTurn();
								}
								return;
							}
						}
					}
				}

				/* if we didn't find an empty square adjacent, the move is illegal */
				throw new InvalidMoveException("Error: second move must take chip adjacent to empty square");
			}

		}

		/* general case (all turns after the first two) */

		/* figure out which color belongs to the current player */
		if (blackToPlay) {
			friend = Chip.BLACK;
			enemy = Chip.WHITE;
		} else {
			friend = Chip.WHITE;
			enemy = Chip.BLACK;
		}

		/* check if chip to move is ours */
		if (getTile(move.pointFrom()).getChip() != friend) {
			throw new InvalidMoveException("Error: trying to move chip not owned by current player(" + move.pointFrom().row() + ", " + move.pointFrom().col() + ")");
		}

		/* check if place to put it is empty */
		if (getTile(move.pointTo()).getChip() != Chip.NONE) {
			throw new InvalidMoveException("Error: trying to move chip into an occupied square(" + move.pointTo().row() + ", " + move.pointTo().col() + ")");
		}

		/* check direction of move */
		rowChange = 0;
		colChange = 0;
		if (move.pointFrom().row() == move.pointTo().row() &&  // horizontal move
				move.pointFrom().col() > move.pointTo().col())  // to the left
		{
			colChange = -1;
		} else if (move.pointFrom().row() == move.pointTo().row() &&  // horizontal
				move.pointFrom().col() < move.pointTo().col())  // to the right
		{
			colChange = 1;
		} else if (move.pointFrom().col() == move.pointTo().col() &&  // vertical
				move.pointFrom().row() > move.pointTo().row())  // up
		{
			rowChange = -1;
		} else if (move.pointFrom().col() == move.pointTo().col() &&  //vertical
				move.pointFrom().row() < move.pointTo().row())  // down
		{
			rowChange = 1;
		} else {
			throw new InvalidMoveException("Error: trying to move chip in a non-cardinal direction");
		}

		row = move.pointFrom().row();
		col = move.pointFrom().col();
		/* check each square between start and end, see if they contain the right things */
		for (int i=1, r=row+rowChange, c=col+colChange;  // init indicies
				r!=move.pointTo().row() || c!=move.pointTo().col();  // go until we get to the "to" point 
				i++, r+=rowChange, c+=colChange)  // update indicies
		{
			/* check whether this tile has the right thing in it */
			if (i%2 == 0 && getTile(r, c).getChip() != Chip.NONE) {
				throw new InvalidMoveException("Error: trying to jump chip into an occupied square (" + r + ", " + c + ")");
			} else if (i%2 != 0 && getTile(r, c).getChip() == friend) {
				throw new InvalidMoveException("Error: trying to jump chip over a friendly piece (" + r + ", " + c + ")");
			} else if (i%2 != 0 && getTile(r, c).getChip() == Chip.NONE) {
				throw new InvalidMoveException("Error: trying to jump chip over an empty square (" + r + ", " + c + ")");
			}
		}

		/* if we get here, move is legal; if we need to actually execute the move, do so */
		if (apply) {
			/* move our piece from the old location to the new one */
			changeTile(move.pointFrom().row(), move.pointFrom().col(), Chip.NONE);
			changeTile(move.pointTo().row(), move.pointTo().col(), friend);

			/* remove captured pieces */
			for (int i=1, r=row+rowChange, c=col+colChange;  // init indicies
					r!=move.pointTo().row() || c!=move.pointTo().col();  // go until we get to the "to" point 
					i++, r+=rowChange, c+=colChange)  // update indicies
			{
				if (i%2 != 0) {
					changeTile(r, c, Chip.NONE);
				}
			}

			/* do end-of-turn stuff */
			this.endTurn();
		}

	}

	/** private function to do end-of-turn status update
	 */
	private void endTurn() {
		/* swap who's turn it is */
		blackToPlay = !blackToPlay;
		/* mark movelist as stale */
		moveListStale = true;
		/* and increment the turn count */
		turn++;
	}

	/**
	 * Board evaluation function. 
	 * 
	 * Checks the number of pieces this player is threatening compared to number threatened by
	 * the opponent.
	 * 
	 * @param isPlayerBlack boolean used to determine the player whose value we want to calculate
	 * @return the board value.  this value should be between 1 and -1, exclusive.
	 */
	public Double getValue(boolean isPlayerBlack) {
		int opponentThreatened = 0;
		int playersThreatened = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				// check all squares in the interior section of the board which are your color
				if (i + j % 2 == 0 && isPlayerBlack || i + j % 2 == 1 && !isPlayerBlack) {
					if (i > 0 && i < width - 1 && j > 0 && j < width - 1) {
						// if horizontally there is a piece on one side it is threatened
						if (getTile(i - 1, j).getChip() != getTile(i+1, j).getChip()) {
							playersThreatened++;
						}
						// same argument vertically
						if (getTile(i, j-1).getChip() != getTile(i, j+1).getChip()) {
							playersThreatened++;
						}
					}
					// checking the opponent's tiles
				} else {
					if (i > 0 && i < width - 1 && j > 0 && j < width - 1) {
						if (getTile(i - 1, j).getChip() != getTile(i+1, j).getChip()) {
							opponentThreatened++;
						}
						if (getTile(i, j-1).getChip() != getTile(i, j+1).getChip()) {
							opponentThreatened++;
						}
					}
				}
			}
		}

		return opponentThreatened - playersThreatened/(1.0 * width * width);
	}


}
