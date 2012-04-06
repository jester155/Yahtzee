/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;
import java.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {
		dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		initializer();
		playGame();
	}

	private void playGame() {
		if(!playerNames[0].equalsIgnoreCase("testing")){
			while(!gameOver()){
				for(player=1; player <= nPlayers; player++){
					if(!playerNames[0].equalsIgnoreCase("admin")){
						rollDice();
						reRolls();
					}
					admin();
					display.printMessage(playerNames[player-1] + ", please select a category.");
					chooseCategory();
					determineScore();
				}
				round ++;
			}
			tallyUp();
			determineWinner();
		} else {
			scoreboardTester();

		}
	}

	private void rollDice(){
		display.printMessage(playerNames[player-1] + ", it is your turn. Please roll dice.");
		display.waitForPlayerToClickRoll(player);
		for(int i = 0; i < N_DICE; i++){
			dice[i]  = rgen.nextInt(1,6); 
		}
		display.displayDice(dice);
	}

	private void reRolls(){
		for(int i = 0; i < 2; i++){
			display.printMessage(playerNames[player-1] + 
					", please select dice to reroll or just hit reroll again to roll none.");
			display.waitForPlayerToSelectDice();

			// Check for selected Die and rolls
			for(int j = 0; j < N_DICE; j++){
				if(display.isDieSelected(j)) dice[j] = rgen.nextInt(1,6);
			}
			display.displayDice(dice);
		}
	}

	private void chooseCategory(){
		category = display.waitForPlayerToSelectCategory();
		if(playerScore[player-1][category-1] != 0){
			display.printMessage(playerNames[player-1] + ", you already used that category. Please, reslect.");
			chooseCategory();
		}
	}

	private void determineScore(){
		int score = 0;
		int counter = 0;
		Arrays.sort(dice);
		int comparer = dice[0];
		switch(category){
		case 1:
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] == 1) score +=1;
			}
			break;
		case 2:
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] == 2) score +=2;
			}
			break;
		case 3:
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] == 3) score +=3;
			}
			break;
		case 4:
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] == 4) score +=4;
			}
			break;
		case 5:
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] == 5) score +=5;
			}
			break;
		case 6:
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] == 6) score +=6;
			}
			break;
		case 9:
			// Three of a kind
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] != comparer && counter != 3){
					comparer = dice[i];
				} else if(dice[i] == comparer){
					counter ++;
				}
				score += dice[i];
			}
			if (counter <3) score = 0;
			break;
		case 10:
			// Four of  a kind
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] != comparer && counter != 4){
					comparer = dice[i];
				} else if(dice[i] == comparer){
					counter ++;
				}
				score += dice[i];
			}
			if (counter <4) score = 0;
			break;
		case 11:
			// Full house
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] != comparer && dice[i] == dice[i+1]){
					comparer = dice[i];
					counter ++;
				} else if(dice[i] == comparer){
					counter ++;
				}
			}
			// Checks if there a pair and three of a kind
			score = ((counter == 5 && dice[3] == dice[4]) || 
					(counter == 5 && dice [2] == dice[3] && dice [2] == dice [4] )) ? 25 : 0; 
			break;
		case 12:
			// Small Straight
			for(int i = 0; i < N_DICE; i++){
				if(comparer == dice[i]){
					comparer ++;
					counter ++;
				} else if(comparer != dice[i] && i == 1 && dice[i-1] != dice[i]) {
					comparer = dice[i];
					counter ++;
				}
			}
			// Checks if there are 4 or more die die in succession
			if(counter >= 4) score = 30; 
			break;
		case 13:
			// Large Straight
			for(int i = 0; i < N_DICE; i++){
				if(comparer == dice[i]){
					comparer ++;
					counter ++;
				}
			}
			// Checks if there are 5 die succession
			if(counter >= 5) score = 40; 
			break;
		case 14:
			// Yahtzee
			for(int i = 0; i < N_DICE; i++){
				if(dice[i] != comparer){
					comparer = dice[i];
				} else if(dice[i] == comparer){
					counter ++;
				}
			}
			if (counter == 5) score = 50;
			break;
		case 15:
			// Chance
			for(int i = 0; i < N_DICE; i++){
				score += dice[i];
			}
			break;
		}
		display.updateScorecard(category, player, score);
		playerScore[player-1][category-1] = score; 
	}

	private void tallyUp(){
		for(int i = 0; i < nPlayers; i++){

			// Upper Score Tallying
			for(int j = 0; j < 6; j++){
				playerScore[i][UPPER_SCORE-1] += playerScore[i][j];
			}
			display.updateScorecard(UPPER_SCORE, i+1, playerScore[i][UPPER_SCORE-1]);

			// Awarding Bonus Score
			if(playerScore[i][UPPER_SCORE-1] >= 63){
				playerScore[i][UPPER_BONUS-1] = 35;
			} else {
				playerScore[i][UPPER_BONUS-1] = 0;
			}
			display.updateScorecard(UPPER_BONUS, i+1, playerScore[i][7]);

			// Lower Score Tallying
			for(int j = 8; j < 15; j++){
				playerScore[i][LOWER_SCORE-1] += playerScore[i][j];
			}
			display.updateScorecard(LOWER_SCORE, i+1, playerScore[i][LOWER_SCORE-1]);

			// Total Score
			playerScore[i][TOTAL-1] = playerScore[i][LOWER_SCORE-1] + playerScore[i][UPPER_SCORE-1] + playerScore[i][UPPER_BONUS-1];
			display.updateScorecard(TOTAL, i+1, playerScore[i][TOTAL-1]);
		}
	}

	private void determineWinner(){
		if (nPlayers > 1){
			int higherScore = 0;
			int winner = 0;
			totalScore = new int[nPlayers];
			for(int i = 0; i < nPlayers; i++){
				totalScore[i] = playerScore[i][TOTAL-1];
			}
			Arrays.sort(totalScore);
			// Tie
			if(totalScore[nPlayers-1] == totalScore[nPlayers-2] 
					|| totalScore[nPlayers-1] == totalScore[nPlayers-3]){
				winners = new ArrayList<Integer>();
				for(int i = 0; i < nPlayers; i++){
					if(playerScore[i][TOTAL-1] == totalScore[nPlayers-1]) winners.add(i);
				}
			}
			// No Tie
			for(int i = 0; i < nPlayers; i++){
				if(playerScore[i][TOTAL-1] == totalScore[nPlayers-1]) winner = i;
			}
			
			// Display Winner
			if(winners.size() == 2){
				display.printMessage("It's a tie between " + playerNames[winners.get(0)] 
						+ " and " + playerNames[winners.get(1)] + ".");
			} else if(winners.size() == 3){
				display.printMessage("It's a tie between " + playerNames[winners.get(0)] 
						+ " and " + playerNames[winners.get(1)] + " and " 
						+ playerNames[winners.get(2)] + ".");
			} else if(winners.size() == 4){
				display.printMessage("It's a 4-way tie. What are the chances?");
			} else {
				display.printMessage(playerNames[winner] + ", you win with a score of " + higherScore);
			}
			
		} else {
			display.printMessage(playerNames[0] + ", you got a score of " + playerScore[0][TOTAL-1] + ".");
		}

	}

	private void admin() {
		if(playerNames[0].equalsIgnoreCase("admin") ){
			for(int i = 0; i < N_DICE; i ++){
				dice[i] = dialog.readInt("Set die number " + (i+1)  + ":");
			}
			display.displayDice(dice);
		}
	}

	private void scoreboardTester(){
		// Scoreboard tester
		for (int i = 0; i<nPlayers; i++ ){
			for(int j = 0; j < 6; j++){
				playerScore[i][j] = dialog.readInt("Set score for player " + (i+1));
			}
			for(int j = 8; j < 15; j++){
				playerScore[i][j] = dialog.readInt("Set score for player " + (i+1));
			}
			playerScore[i][UPPER_BONUS-1] = 35;
			for(int k=0; k <17; k++){
				display.updateScorecard(k+1, i+1, playerScore[i][k]);
			}
		}
		tallyUp();
		determineWinner();
	}

	private boolean gameOver(){
		return round > 13;
	}

	private void initializer(){
		dice = new int[N_DICE];
		int scoresheet = 17; 
		playerScore = new int[nPlayers][scoresheet];
	}

	/* Private instance variables */
	private int nPlayers, round = 1, player, category;
	private String[] playerNames;
	private int[][] playerScore;
	private int[] dice, totalScore;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private IODialog dialog;
	private ArrayList<Integer> winners;
}
