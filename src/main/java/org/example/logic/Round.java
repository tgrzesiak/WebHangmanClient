package org.example.logic;

import java.io.IOException;
import java.util.Scanner;

public class Round {
    private String category;
    private String word;
    private String hiddenWord;
    private int totalLettersCounter;
    private int hiddenLettersCounter;

    public String getHiddenWord() {
        return this.hiddenWord;
    }

    public int getHiddenLettersCounter() { return this.hiddenLettersCounter; }

    public Round(String word) {
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
    }

    public void play() {
        System.out.println("Enter letter you think the word consists of:");
        Scanner letterScanner = new Scanner(System.in);
        //TODO jak zignorować dane z InputStreamu, które były tam napisane wcześniej

        while (hiddenLettersCounter > 0 ){
            System.out.println(hiddenWord);
            char letter;
            letter = letterScanner.next().toUpperCase().toCharArray()[0];
            updateHiddenWord(letter);
        }
        if (hiddenLettersCounter == 0) {
            System.out.println("Congratulations! You guessed right!");
            //TODO co wysłać do serwera?
        } else {
            System.out.println("Nope! maybe next time ;)");
            //TODO co wysłać do serwera?
            //TODO zrobić żeby czekał na początek kolejnej GRY, a nie rundy
        }
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
            System.out.println("hello there");
            try {
                NetworkManager.getSocketOS().write(totalLettersCounter - hiddenLettersCounter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
