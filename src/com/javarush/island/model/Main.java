package com.javarush.island.model;

import com.javarush.island.model.common.GameField;
import com.javarush.island.model.programinterface.GameInterface;

public class Main {

    public static void main(String[] args) {

        GameField gameField = GameField.getInstance();

        GameInterface gameInterface = new GameInterface();
        gameInterface.initializeGame(gameField);

    }

}


