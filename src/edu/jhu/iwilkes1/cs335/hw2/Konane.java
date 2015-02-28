package edu.jhu.iwilkes1.cs335.hw2;

import java.util.Scanner;

import edu.jhu.iwilkes1.cs335.hw2.board.Board;
import edu.jhu.iwilkes1.cs335.hw2.board.Chip;
import edu.jhu.iwilkes1.cs335.hw2.board.Move;
import edu.jhu.iwilkes1.cs335.hw2.players.AlphaBetaPlayer;
import edu.jhu.iwilkes1.cs335.hw2.players.HumanPlayer;
import edu.jhu.iwilkes1.cs335.hw2.players.MinimaxPlayer;
import edu.jhu.iwilkes1.cs335.hw2.players.Player;

/**
 * Driver class for Konane program; see README for description and terms of use
 * @author Ben Mitchell
 */
public class Konane {

  public static void main(String[] args) {
    Board game;
    int boardSize;
    int maxSearchDepth;
    Player[] players = new Player[2];
    Scanner stdin = new Scanner(System.in);

    try {

      /* prompt user for game setup */
      System.out.println("Let's play Konane!");
      System.out.print("Enter board size: ");
      boardSize = stdin.nextInt();
      
      /* initialize game */
      game = new Board(boardSize);

      
      for (int i=0; i<2; i++) {
        maxSearchDepth = -1;
    	  System.out.print("Enter player type for ");
        if (i == 0)
          System.out.print("Black");
        else
          System.out.print("White");

        System.out.print(" (1=human, 2=minmax, 3=alphabeta): ");
        int choice = stdin.nextInt();
        switch (choice) {
          case 1:
            players[i] = new HumanPlayer();
            break;
          case 2:
        	  // prompt user for max search depth.
        	while (maxSearchDepth < 1) {
        		System.out.println("Enter the maximum search depth for minimax player: " + i);
        		maxSearchDepth = stdin.nextInt();
        	}
            players[i] = new MinimaxPlayer(i == 1, maxSearchDepth);
            break;
          case 3:
        	  // prompt user for max search depth.
          	while (maxSearchDepth < 1) {
          		System.out.println("Enter the maximum search depth for alpha beta player: " + i);
          		maxSearchDepth = stdin.nextInt();
          	}
            players[i] = new AlphaBetaPlayer(i == 0, maxSearchDepth);
            break;
          default:
            System.out.println("bad agent type given, please try again...");
            i--;
        }
      }

      System.out.println("\n===================");

      /* take turns until gameover */
      while ( game.gameWon() == Chip.NONE ) {  
        System.out.print("Turn " + game.getTurn() + ", ");
        if (game.getTurn()%2 == 0) {
          //TODO System.out.println("black to play:");
        } else {
          // TODO System.out.println("white to play:");
        }

        // TODO System.out.println(game);

        Move m = players[game.getTurn()%players.length].getMove(game);
        game.executeMove(m);
      }

     //TODO System.out.println("Game over!  Final board state:\n" + game);
/*
      if (game.gameWon() == Chip.BLACK) {
        System.out.println("Game won by Black after " + game.getTurn() + " turns");
      } else {
        System.out.println("Game won by White after " + game.getTurn() + " turns");
      }
*/

    } catch (Exception e) {
      System.err.println("Caught an exception: \n\t" + e.toString());
      e.printStackTrace();
    } finally {
    	stdin.close();
    }

  }

}
