package org.example.logic;

import java.io.IOException;
import java.util.Scanner;

public class Round {
    private int roundNumber;
    private String category;
    private String word;
    private String hiddenWord;
    private int totalLettersCounter;
    private int hiddenLettersCounter;
    private Integer[] otherScores;
    private int lives;
    private int buttonDelay;

    public int getButtonDelay() {return this.buttonDelay; }

    public int getRoundNumber() { return this.roundNumber; }

    public String getCategory() { return this.category; }

    public int getLives() { return this.lives; }

    public void setLives(int lives) { this.lives = lives; }

    public String getHiddenWord() {
        return this.hiddenWord;
    }

    public int getHiddenLettersCounter() { return this.hiddenLettersCounter; }

    public Integer[] getOtherScores() { return this.otherScores; }

    public void setOtherScores(Integer[] otherScores) { this.otherScores = otherScores; }

    public Round(String word, int roundNumber, int playersCounter) {
        this.roundNumber = roundNumber;
        String[] words = word.split(":");
        this.category = words[0];
        this.word = words[1].replaceAll("_", " ");
        this.hiddenWord = "";
        for (int i=0; i<this.word.length(); i++) {
            if (this.word.charAt(i) == ' ') this.hiddenWord += " ";
            else this.hiddenWord += "_";
        }
        this.totalLettersCounter = this.word.replaceAll(" ", "").length();
        this.hiddenLettersCounter = this.totalLettersCounter;
        this.otherScores = new Integer[playersCounter];
        for (Integer in : this.otherScores) in = 0;
        this.lives = 5;
        if(roundNumber < 4) this.buttonDelay = 10000;
        else this.buttonDelay = 5000;
    }

    public void updateHiddenWord(char letter) {
        int prevHiddenLettersCounter = this.hiddenLettersCounter;
        for (int i=0; i<this.word.length(); i++) {
            if (this.word.charAt(i) == letter && this.hiddenWord.charAt(i) == '_') {
                this.hiddenWord = this.hiddenWord.substring(0, i) + letter + this.hiddenWord.substring(i+1);
                this.hiddenLettersCounter--;
            }
        }
        if (prevHiddenLettersCounter > this.hiddenLettersCounter) {
            try {
                NetworkManager.getSocketOS().write(Integer.toString(totalLettersCounter - hiddenLettersCounter).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
