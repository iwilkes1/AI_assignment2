package edu.jhu.iwilkes1.cs335.hw2.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.jhu.iwilkes1.cs335.hw2.board.Board;
import edu.jhu.iwilkes1.cs335.hw2.board.Chip;
import edu.jhu.iwilkes1.cs335.hw2.board.InvalidMoveException;
import edu.jhu.iwilkes1.cs335.hw2.board.Move;

/**
 * Class to use in a recursive implementation of an alpha beta pruning version of
 * the minimax algorithm.
 * @author Ian Wilkes
 *
 */
public class AlphaBetaNode {

	private static int nodesExplored = 0;

	private Board board;
	private ArrayList<AlphaBetaNode> successors;

	private int remainingDepth;

	private AlphaBetaNode parent;
	private boolean isPlayerBlack;
	private boolean isMaxNode;
	private Double alpha;
	private Double beta;

	private int lowestRemaining = 0;
	private static final double MIN_ALPHA_VALUE = -2.0;
	private static final double MAX_BETA_VALUE = 2.0;

	/**
	 * Constructor for a minimax node.  Initializes all the data needed for the node.
	 * @param board the state this node represents.
	 * @param parent the node conctaining the state from which this state was generated
	 * @param remainingDepth the number of levels of recursion which can be executed by this node.
	 * @param isPlayerBlack boolean used in combination with turn number to determine if this is a
	 * max node or a min node.
	 */
	public AlphaBetaNode(Board board, AlphaBetaNode parent, int remainingDepth, boolean isPlayerBlack) {
		this.board = board; 
		this.parent = parent;
		this.remainingDepth = remainingDepth;
		this.isPlayerBlack = isPlayerBlack;
		this.alpha = MIN_ALPHA_VALUE;
		this.beta = MAX_BETA_VALUE;

		this.isMaxNode = (this.isPlayerBlack && this.board.getTurn() % 2 == 0)
				|| (!this.isPlayerBlack && this.board.getTurn() % 2 == 1);

		if (this.parent == null) {
			this.successors = new ArrayList<AlphaBetaNode>();
		} else {
			this.successors = null;
		}

		if (remainingDepth < lowestRemaining) {
			lowestRemaining = remainingDepth;
		}
	}

	/**
	 * Method to recursively calculate the alpha and beta parameters of the search tree.
	 * For any given node, it will continue recursing until one of two things happens.
	 * The first is reaching a node which represents a leaf state in the tree, or the end of the
	 * game.  In this case, both alpha and beta are set to the value of that end state.
	 * 
	 * The second possibility is reaching the maximum search depth, at which point the 
	 * heuristic board evaluation function is called to determine the alpha and beta
	 * value of this node.  
	 * 
	 * While recursing, the algorithm checks to see if the children of this node are
	 * more promising than the values previously discovered, and also if this branch
	 * of the search tree can be pruned, due to the parent having already found a better
	 * alternative.  This is true for both max and min nodes.
	 * @throws InvalidMoveException
	 */
	public void calculateParameters() throws InvalidMoveException {
		Board currentBoard;
		AlphaBetaNode nextNode;
		Double nextBeta;
		Double nextAlpha;
		Double maxSoFar = null;
		Double minSoFar = null;
		Chip endState = this.board.gameWon();
		// I do not consider looking at the current game to be one node expansion.
		if (this.parent != null) {
			nodesExplored++;
		}
		// Game is over, so set the values of the node based on winning or losing.
		if (endState != Chip.NONE) {
			if (isPlayerBlack && endState == Chip.BLACK || !isPlayerBlack && endState == Chip.WHITE) {
				alpha = Board.MAX_BOARD_VALUE;
				beta = Board.MAX_BOARD_VALUE;
			} else {
				alpha = Board.MIN_BOARD_VALUE;
				beta = Board.MIN_BOARD_VALUE;
			}
			// maximum recursion depth has been reached, so use the heuristic to 
			// evaluate the current board.
		} else if (remainingDepth == 0) {
			alpha = board.getValue(isPlayerBlack);
			beta = alpha;
		} else {
			// check each of the possible moves which could be made from this location.
			for (Move m: this.board.getLegalMoves()) {
				currentBoard = new Board(this.board);
				currentBoard.executeMove(m);

				// create new nodes, then the next call is the recursive calculation.
				nextNode = new AlphaBetaNode(currentBoard, this, remainingDepth - 1, this.isPlayerBlack);
				nextNode.calculateParameters();
				
				nextBeta = nextNode.getBeta();
				if (maxSoFar == null || nextBeta > maxSoFar) {
					maxSoFar = nextBeta;
				}

				nextAlpha = nextNode.getAlpha();
				if (minSoFar == null || nextAlpha < minSoFar) {
					minSoFar = nextAlpha;
				}

				// this is a min node, need to see if the minimum can be lowered.
				if (!this.isMaxNode && this.beta > nextAlpha) {
					this.beta = nextAlpha;
					// this node is a minimization node, and its largest value that it will produce is less than
					// a value which can be reached from the 
					// parent node, so we can prune this branch. 
					if (this.parent != null && this.parent.getAlpha() >= Board.MIN_BOARD_VALUE 
							&& this.parent.getAlpha() > this.beta) {
						break;
					}

					// this is the equivalent for a maximization node.
				} else if (this.isMaxNode && this.alpha < nextBeta) {
					this.alpha = nextBeta;
					if (this.parent != null && this.parent.getBeta() <= Board.MAX_BOARD_VALUE && this.parent.getBeta() < this.alpha) {
						break;
					}
				}
				// include a collection of all the moves and resulting boards if
				// this is the root node, will be used to determine the optimal move.
				if (this.successors != null) {
					this.successors.add(nextNode);
				}
			}
			// update the other value of beta and alpha after iterating over all children.
			if (this.isMaxNode) {
				this.beta = maxSoFar;
			} else {
				this.alpha = minSoFar;
			}
		}
	}

	/**
	 * Getter function for the alpha value of this node.
	 * @return the alpha value
	 */
	public double getAlpha() {
		return this.alpha;
	}

	/**
	 * Getter function for the beta value of this node.
	 * @return the beta value.
	 */
	public double getBeta() {
		return this.beta;
	}

	/**
	 * Setter method to update the alpha value of a node.
	 * @param alpha the new value of alpha
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * Setter method to update the beta value of a node.
	 * @param beta the new value of beta
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	/**
	 * Getter method to return the successors of a given alpha beta node.
	 * This will return null in all cases where the node it is called on is not the root node.
	 * @return a map of moves to further nodes which are the successors generated by said moves.
	 */
	public ArrayList<AlphaBetaNode> getSuccessors() {
		return this.successors;
	}

	/**
	 * getter method to get the number of nodes
	 * explored in the search process.
	 * @return the number of nodes explored.
	 */
	public int getNodesExplored() {
		return AlphaBetaNode.nodesExplored;
	}

	/**
	 * Method to reset the number of explored nodes to zero
	 * at the end of a turn.
	 */
	public void resetNodesExplored() {
		AlphaBetaNode.nodesExplored = 0;
		this.lowestRemaining  = 0;
	}

	/**
	 * getter method for figuring out the lowest depth the algorithm reached.
	 * @return the value of additional recursions allowed by the lowest possible
	 * node.
	 */
	public int getLowestRemaining() {
		return this.lowestRemaining;
	}


}
