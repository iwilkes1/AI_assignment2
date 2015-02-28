package edu.jhu.iwilkes1.cs335.hw2.players;

import edu.jhu.iwilkes1.cs335.hw2.board.Board;
import edu.jhu.iwilkes1.cs335.hw2.board.Chip;
import edu.jhu.iwilkes1.cs335.hw2.board.InvalidMoveException;
import edu.jhu.iwilkes1.cs335.hw2.board.Move;
import edu.jhu.iwilkes1.cs335.hw2.players.Player;

/** minimax Player class for Konane game
 *
 * @author Ian Wilkes
 */
public class MinimaxPlayer extends Player {

	private int maxDepth;
	private boolean isWhite;
	private static final int MAX_NUM_PLAYERS = 2;
	private static int nodesExplored = 0;
	private static int lowestRemaning;
	
	/**
	 * Generic constructor to generate minimax players using their color and maximum
	 * search depth.
	 * @param isWhite true if the player is white, false otherwise.
	 * @param maxDepth the maximum number of moves this agent is allowed to look ahead.
	 */
	public MinimaxPlayer(boolean isWhite, int maxDepth) {
		this.isWhite = isWhite;
		this.maxDepth = maxDepth;
	}

	
	public Move getMove(Board game) {
		nodesExplored = 0;
		lowestRemaning = maxDepth;
		long startTime = System.nanoTime();
		Move bestMove = null;
		double bestScore = -2;
		double currentScore = -2;
		for (Move m: game.getLegalMoves()) {
			try {
				currentScore = getMoveScore(m, new Board(game), maxDepth);
				if (currentScore > bestScore || bestMove == null) {
					bestScore = currentScore;
					bestMove = m;
				}
				// only legal moves are tried, so no exceptions should be caught.
			} catch (InvalidMoveException e) {
				e.printStackTrace();
			}

		}
		long endTime = System.nanoTime();
		long timeDifference = endTime - startTime;
		System.out.println(nodesExplored + " nodes explored on turn " + game.getTurn());
		System.out.println((timeDifference /1000000000) + " seconds, " + ((timeDifference /1000000 )% 1000) + " ms elapsed on turn " + game.getTurn());
		System.out.println("lowest depth reached: " + (maxDepth - lowestRemaning));
		System.out.println();
		nodesExplored = 0;
		return bestMove;

	}

	/**
	 * This method recursively calculates the score of a given move on a specified board.
	 * It recursively searches for leaf nodes which will provide the values of the current
	 * board, or if it runs out of levels of recursion to use, it will use the board
	 * evaluation heuristic to calculate the values of each of the bottom level boards.
	 *   
	 * @param toTry the move we are about to try
	 * @param oldBoard the board we are going to try it on
	 * @param remainingDepth the number of further moves to explore
	 * @return the minimax value of the move specified for the board specified.
	 * @throws InvalidMoveException 
	 */
	private double getMoveScore(Move toTry, Board oldBoard, int remainingDepth) throws InvalidMoveException {
		nodesExplored++;
		if (remainingDepth < lowestRemaning) {
			lowestRemaning = remainingDepth;
		}
		if (oldBoard.gameWon() == Chip.NONE) {  
			// not allowed to traverse the tree further.
			if (remainingDepth == 1) {
				return oldBoard.getValue(!isWhite); 
				// keep exploring
			} else {
				double best_so_far = -2;
				double worst_so_far = 2;
				double result;
				// apply the move to the given board, then recurse on the next set of moves.
				oldBoard.executeMove(toTry);
				for (Move m: oldBoard.getLegalMoves()) {
					result = getMoveScore(m, new Board(oldBoard), remainingDepth-1);
					if (result > best_so_far) {
						best_so_far = result;
					} 
					if (result < worst_so_far) {
						worst_so_far = result;
					}
				}
				// return values based on whether this is a max node or min node.
				if (((oldBoard.getTurn() % MAX_NUM_PLAYERS == 0) && !this.isWhite) || 
						((oldBoard.getTurn() % MAX_NUM_PLAYERS == 1) && this.isWhite )) {
					return best_so_far;
				} else {
					return worst_so_far;
				}
			}
		}
		// Case where the game is over and black won.
		else if (oldBoard.gameWon() == Chip.BLACK) {
			if (this.isWhite) {
				return -1;
			} else {
				return 1;
			}
		}
		// game over with a white win.
		else {
			if (this.isWhite) {
				return 1;
			} else {
				return -1;
			}
		}

	}
}


