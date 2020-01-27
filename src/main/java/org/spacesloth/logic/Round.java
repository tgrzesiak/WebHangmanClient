package org.spacesloth.logic;

import java.io.IOException;

public class Round {
    private int roundNumber;
    private String category;
    private String word;
    private String hiddenWord;
    private int totalLettersCounter;
    private int hiddenLettersCounter;
    private int lives;
    private int buttonDelay;
    private String[] otherNamesScores;
    private int[] othersDescs;

    public String getWord() { return this.word; }

    public String[] getOtherNamesScores() {
        return otherNamesScores;
    }

    public void setOtherNamesScores(String[] otherNamesScores) {
        this.otherNamesScores = otherNamesScores;
    }

    public int getButtonDelay() {return this.buttonDelay; }

    public int getRoundNumber() { return this.roundNumber; }

    public String getCategory() { return this.category; }

    public int getLives() { return this.lives; }

    public void setLives(int lives) { this.lives = lives; }

    public String getHiddenWord() {
        return this.hiddenWord;
    }

    public int getHiddenLettersCounter() { return this.hiddenLettersCounter; }



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
        this.otherNamesScores = new String[playersCounter-1];
        for (int i=0; i<playersCounter-1; i++) this.otherNamesScores[i] = "Gracz" + (i + 1) + ": 0";
        this.othersDescs = new int[playersCounter-1];
        for (int i=0; i<playersCounter-1; i++) this.othersDescs[i] = 0;
        this.lives = 8;
        if(roundNumber < 4) this.buttonDelay = 20000;
        else this.buttonDelay = 10000;
    }

    public int updateHiddenWord(char letter) {
        int prevHiddenLettersCounter = this.hiddenLettersCounter;
        for (int i=0; i<this.word.length(); i++) {
            if (this.word.charAt(i) == letter && this.hiddenWord.charAt(i) == '_') {
                this.hiddenWord = this.hiddenWord.substring(0, i) + letter + this.hiddenWord.substring(i+1);
                this.hiddenLettersCounter--;
            }
        }
        if (prevHiddenLettersCounter > this.hiddenLettersCounter) {
            //TODO czasem nie wysyła updatu ilości odgadniętych liter
            NetworkManager.sendInt(this.totalLettersCounter- this.hiddenLettersCounter);
        }
        return prevHiddenLettersCounter - this.hiddenLettersCounter;
    }

    public int matchIndex(int _desc) {
        for (int i = 0; i < this.othersDescs.length; i++) {
            if (this.othersDescs[i] == _desc) {
                return i;
            }
            if (this.othersDescs[i] == 0) {
                this.othersDescs[i] = _desc;
                return i;
            }
        }
        return this.othersDescs.length-1;
    }
}
