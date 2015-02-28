package edu.jhu.iwilkes1.cs335.hw2.players;

import java.util.Map.Entry;

import edu.jhu.iwilkes1.cs335.hw2.board.Board;
import edu.jhu.iwilkes1.cs335.hw2.board.InvalidMoveException;
import edu.jhu.iwilkes1.cs335.hw2.board.Move;
import edu.jhu.iwilkes1.cs335.hw2.players.Player;

/** alpha-beta pruning Player class for Konane game
 *
 * @author Ian Wilkes
 */
public class AlphaBetaPlayer extends Player {

	private int maxSearchDepth;
	private boolean isPlayerBlack;

	public AlphaBetaPlayer(boolean isPlayerBlack, int maxSearchDepth) {
		this.isPlayerBlack = isPlayerBlack;
		this.maxSearchDepth = maxSearchDepth;
	}


	public Move getMove(Board game) {
		long startTime;
		long endTime;
		long timeDifference;

		startTime = System.nanoTime();
		// populates the search tree
		AlphaBetaNode currentRoot = new AlphaBetaNode(game, null, this.maxSearchDepth, this.isPlayerBlack);
		try {
			currentRoot.calculateParameters();
			// This method should never cause invalid moves, as the only ones tried are valid.
		} catch (InvalidMoveException e) {
			e.printStackTrace();
		}
		Move moveToTest;
		AlphaBetaNode nodeToTest;

		Move bestMoveSoFar = null;
		Double bestValueSoFar = null;
		// searches through the possible moves to find out which one is best after the
		// search tree has been populated.
		for (Entry<Move, AlphaBetaNode> entry : currentRoot.getSuccessors().entrySet()) {
			moveToTest = entry.getKey();
			nodeToTest = entry.getValue();

			if (bestValueSoFar == null || bestValueSoFar < nodeToTest.getAlpha()) {
				bestMoveSoFar = moveToTest;
				bestValueSoFar = nodeToTest.getAlpha();
			}
		}

		endTime = System.nanoTime();
		timeDifference = endTime - startTime;
		System.out.println(currentRoot.getNodesExplored());// + " nodes explored on turn " + game.getTurn());
		System.out.println((timeDifference /1000000000) + " seconds, " + ((timeDifference /1000000 )% 1000) + " ms elapsed on turn " + game.getTurn());
		System.out.println("Deepest depth reached: " + (maxSearchDepth - currentRoot.getLowestRemaining()));
		currentRoot.resetNodesExplored();
		return bestMoveSoFar;
	} 


}
