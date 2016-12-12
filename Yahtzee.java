/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

        // The width of the application window
    public static final int APPLICATION_WIDTH = 600;

    // The height of the application window
    public static final int APPLICATION_HEIGHT = 350;

    // The number of dice in the game
    public static final int N_DICE = 5;

    // The maximum number of players
    public static final int MAX_PLAYERS = 4;

    // The total number of categories
    public static final int N_CATEGORIES = 17;

    // The number of categories in which the player can score
    public static final int N_SCORING_CATEGORIES = 13;

    // The constants that specify categories on the scoresheet
    public static final int ONES = 1;
    public static final int TWOS = 2;
    public static final int THREES = 3;
    public static final int FOURS = 4;
    public static final int FIVES = 5;
    public static final int SIXES = 6;
    public static final int UPPER_SCORE = 7;
    public static final int UPPER_BONUS = 8;
    public static final int THREE_OF_A_KIND = 9;
    public static final int FOUR_OF_A_KIND = 10;
    public static final int FULL_HOUSE = 11;
    public static final int SMALL_STRAIGHT = 12;
    public static final int LARGE_STRAIGHT = 13;
    public static final int YAHTZEE = 14;
    public static final int CHANCE = 15;
    public static final int LOWER_SCORE = 16;
    public static final int TOTAL = 17;

        // Private instance variables
    private int nPlayers = 3;
    private String[] playerNames;
    private YahtzeeDisplay display;
    private RandomGenerator rgen = new RandomGenerator();
    private int[] dices = new int[N_DICE];
    private int[][] usedCategory = new int[TOTAL][nPlayers - 1];
    private int[] diceValue = new int[6];
    private int[][] scores = new int[TOTAL][nPlayers - 1];
    private int roundCount = (nPlayers * 13);
    private int playerIndex;
    private int highestNumber;
    private int upperScore;
    private int lowerScore;
    private String player;


    
    public static void main(String[] args) {
        new Yahtzee().start(args);
    }
    
    public void run() {
        IODialog dialog = getDialog();
        nPlayers = dialog.readInt("Enter number of players");
        if(nPlayers == 1337){
            cheatGame();
            return;
        }
        playerNames = new String[nPlayers];
        for (int i = 1; i <= nPlayers; i++) {
            playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
        }
        display = new YahtzeeDisplay(getGCanvas(), playerNames);
        playGame(); 
    }

    //Cheatmode which allows for manual dice configuration
    private void cheatGame(){
        IODialog dialog = getDialog();
        dialog.println("Welcome mister President-Elect");
        playerNames = new String[]{"Trump"};
        display = new YahtzeeDisplay(getGCanvas(), playerNames);
        roundCount = 13;
        nPlayers = 1;
        initializeScores();
        usedCategory = scores;
        while(roundCount != 0){
            player = "Trump";
            playerIndex = 1;
            display.printMessage("Can't stump the Trump! Click 'Roll Dice' button to reveal the dice.");
                setDice();
            for(int i = 2; i > 0; --i){
                display.printMessage("You've made good choices, the best choices.");
                pickDice();
            }
            checkCategory();
            checkTotal();
            
            --roundCount;
        }
    }

    //Opens dialog box to enter dice
    private void setDice(){
        IODialog dialog = getDialog();
        for(int i = 0; i < N_DICE; i++){
            dices[i] = dialog.readInt("Enter dice "+ (i + 1) +"");
        }
    }

    //Regular play
    private void playGame() {
        initializeScores();
        usedCategory = scores;
        while(roundCount != 0){
            //The current player is determined. Currently only 2 players available.
            if(roundCount % 2 != 0){
                player = playerNames[0];
                playerIndex = 1;
            }else if(roundCount % 2 == 0 && nPlayers > 1) {
                player = playerNames[1];
                playerIndex = 2;
            }
            display.printMessage(""+ player +"'s turn! Click 'Roll Dice' button to roll the dice.");
            rollDice();
            for(int i = 2; i > 0; --i){
                display.printMessage("Select the dice you want to re-roll and click 'Roll Again'.");
                pickDice();
            }
            checkCategory();
            checkTotal();
            
            --roundCount;
        }
        calculateFinal();
    }

    // Sets scores array to 0
    private void initializeScores(){
        for(int i = 0; i < nPlayers; i++){
            for(int j = 0; j < TOTAL; j++){
                scores[j][i] = 0;
            }
        }
    }

    private void rollDice(){
        display.waitForPlayerToClickRoll(playerIndex);
        for(int i = 0; i < N_DICE; i++){
            dices[i] = rgen.nextInt(1,6);
        }
        display.displayDice(dices);
     
    }

    private void pickDice(){
        display.waitForPlayerToSelectDice();
        selectedDiceRoll();
    }

    // re-rolls the selected dice.
    private void selectedDiceRoll(){
        for(int i = 0; i < N_DICE; ++i) {
            if(display.isDieSelected(i))
                dices[i] = rgen.nextInt(1,6);
        }
        display.displayDice(dices);
    }

    //Checks if the selected category is legal and fills in score.
    private void checkCategory(){
        int category = display.waitForPlayerToSelectCategory();
        int result;
        int result2;
        switch(category)    {
            case 1: tryCategory(category);
                    break;

            case 2: tryCategory(category);
                    break;
                    
            case 3: tryCategory(category);
                    break;
                    
            case 4: tryCategory(category);
                    break;
                    
            case 5: tryCategory(category);
                    break;
 
            case 6: tryCategory(category);
                    break;

            case 9: if(tallyDice(9)){
                        break;
                    }
                    result = checkDice(3);
                    if(result > 0){
                        display.updateScorecard( 9, playerIndex, 3 * result);
                        scores[category - 1][playerIndex -1] = 3 * result;
                    }else   {
                            display.updateScorecard(9, playerIndex, 0);
                            scores[category - 1][playerIndex -1] = 0;
                        }
                    break;

            case 10:    if(tallyDice(10)){
                            break;
                        }
                        result = checkDice(4);
                        if(result > 0){
                            display.updateScorecard(10, playerIndex, 4 * result);
                            scores[category - 1][playerIndex -1] = 4 * result;
                        }else   {
                            display.updateScorecard(10, playerIndex, 0);
                            scores[category - 1][playerIndex -1] = 0;
                        }

                        break;

            case 11:    if(tallyDice(11)){
                            break;
                        }
                        result = checkDice(3);
                        result2 = checkDice(2);
                        if(result > 0 && result2 > 0){
                            display.updateScorecard(11, playerIndex, 25);
                            scores[category - 1][playerIndex -1] = 25;
                        }else   {
                            display.updateScorecard(11, playerIndex, 0);
                            scores[category - 1][playerIndex -1] = 0;
                        }
                        break;

            case 12:    if(tallyDice(12)){
                            break;
                        }
                        checkStraight(category);
                        break;

            case 13:    if(tallyDice(category)){
                            break;
                        }
                        checkStraight(category);
                        break;  

            case 14:    if(tallyDice(14)){
                            break;
                        }
                        result = checkDice(5);
                        if(result > 0){
                            display.updateScorecard(14, playerIndex, 50);
                            scores[category - 1][playerIndex -1] = 50;
                        }else   {
                            display.updateScorecard(14, playerIndex, 0);
                            scores[category - 1][playerIndex -1] = 0;
                        }
                        break;

            case 15:    chanceCategory();
                        break;
        }
    }

    private void tryCategory(int x){
        int category = x;
        //recursively calls checkCategory() to prompt user to pick a compatible
        // one.
        if(occurrenceCheck(category)){
            display.printMessage("Invalid category, choose again!");
            checkCategory();
        }else   {
            upperValue(category);
            usedCategory[category -1][playerIndex -1] = 1;
        }
    }

    //An array that counts the occurence of certain dice-values
    private boolean tallyDice(int x){
        int category = x;
        if(usedCategory[category - 1][playerIndex -1] > 0){
            display.printMessage("Invalid category, choose again!");
            tallyDice(category);
            return(true);
        }
        for(int i = 0; i < N_DICE; i++){
            switch(dices[i])    {

                case 1: ++diceValue[0];
                        break;

                case 2: ++diceValue[1];
                        break;

                case 3: ++diceValue[2];
                        break;

                case 4: ++diceValue[3];
                        break;

                case 5: ++diceValue[4];
                        break;

                case 6: ++diceValue[5];
                        break;
            }
        }
        usedCategory[category -1][playerIndex -1] = 1;
        return(false);
    }

    //Checks for Three-of-a Kind, Four of a kind and Yahtzee.
    private int checkDice(int x){
        int higherthan = x;
        for(int i = 0; i < N_DICE; i++){
            if(diceValue[i] > higherthan - 1){
                highestNumber = i + 1;
                diceValue[i] = 0;
                return(highestNumber);
            }
        }
        return(0);
    }
    //checks if straight is present.
    private void checkStraight(int x){
        int occurenceCounter = 0;
        int category = x;
        for(int i = 0; i < N_DICE; i++){
            if(diceValue[i] > 0){
                occurenceCounter++;
            }
        }
        if(occurenceCounter > 3 && category == 12){
            display.updateScorecard(category, playerIndex, 30);
        }else if(occurenceCounter > 4 && category == 13){
            display.updateScorecard(category, playerIndex, 40);
        }else   {
            display.updateScorecard(category, playerIndex, 0);   
        } 
    }
    
    private boolean occurrenceCheck(int x){
        int counter = 0;
        int category = x;
        for(int i = 0; i < N_DICE; ++i){
            if(dices[i] == category)
                counter++;
        }
        if(scores[category - 1][playerIndex -1] > 0){
            return(true);
        }else   {
            return(false);
        }
    }
    private void upperValue(int x){
        int category = x;
        for(int i = 0; i < N_DICE; ++i){
            if(dices[i] == category)
                scores[category - 1][playerIndex - 1] += category;
        }
        display.updateScorecard(category, playerIndex, scores[category - 1][playerIndex - 1]);
    }

    private void checkTotal(){
        lowerScore = 0;
        upperScore = 0;
        for(int i = ONES; i < UPPER_SCORE; i++){
            lowerScore += scores[i][playerIndex - 1];
        }
        for(int i = THREE_OF_A_KIND; i < LOWER_SCORE; i++){
            upperScore += scores[i][playerIndex - 1];
        }
        display.updateScorecard(TOTAL, playerIndex, upperScore + lowerScore);
    }

    private void chanceCategory(){
        int temp = dices[0];
        for(int i = 1; i < N_DICE; i++){
            temp += dices[i];
        }
        display.updateScorecard(15, playerIndex, temp);
        scores[14][playerIndex -1] = temp;
    }

    private void calculateFinal(){
        for(int i = 0; i < nPlayers; i++){
            for(int j = ONES; j < UPPER_SCORE; j++){
                scores[UPPER_SCORE -1][i] += scores[j][playerIndex - 1];
                display.updateScorecard(UPPER_SCORE -1, i, scores[UPPER_SCORE -1][i]);
            }
            for(int k = THREE_OF_A_KIND; k < LOWER_SCORE; k++){
                scores[LOWER_SCORE -1][i] += scores[k][playerIndex - 1];
                display.updateScorecard(LOWER_SCORE -1, i, scores[LOWER_SCORE -1][i]);
            }
            if(scores[UPPER_SCORE -1][i] > 64){
                scores[UPPER_BONUS -1][i] = 35;
                display.updateScorecard(UPPER_BONUS -1, i, 35);
            }
            scores[TOTAL - 1][i] = scores[UPPER_SCORE -1][i] + 
            scores[LOWER_SCORE -1][i] + scores[UPPER_BONUS -1][i];

        }
    }
}


