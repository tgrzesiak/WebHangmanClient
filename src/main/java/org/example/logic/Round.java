package org.example.logic;

import java.util.Scanner;

public class Round {
    private String word;
    private String hiddenWord;
    private int hiddenLettersCounter;

    public String getHiddenWord() {
        return this.hiddenWord;
    }

    public int getHiddenLettersCounter() { return this.hiddenLettersCounter; }

    public Round(String word) {
        this.word = word;
        hiddenWord = "";
        for (int i=0; i<word.length()-1; i++) hiddenWord += "_ ";
        hiddenWord += '_';
        hiddenLettersCounter = word.length();
    }

    public void play() {
        System.out.println("Enter letter you think the word consists of:");
        Scanner letterScanner = new Scanner(System.in);
        //TODO jak zignorować dane z InputStreamu, które były tam napisane wcześniej

        while (hiddenLettersCounter > 0 ){//&& NetworkManager.isRoundInProgress()) {
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
        for (int i=0; i<word.length(); i++) {
            if (word.charAt(i) == letter && hiddenWord.charAt(2*i) == '_') {
                hiddenWord = hiddenWord.substring(0, 2*i) + letter + hiddenWord.substring(2*i+1);
                hiddenLettersCounter--;
            }
        }
    }
}
