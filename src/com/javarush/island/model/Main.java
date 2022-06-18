package com.javarush.island.model;

import com.javarush.island.model.common.GameField;
import com.javarush.island.model.programinterface.GameInterface;

public class Main {

    public static void main(String[] args) {

        GameField gameField = GameField.getInstance();

        GameInterface gameInterface = new GameInterface();
        //GameInterface_one_thread gameInterface = new GameInterface_one_thread();
        gameInterface.initializeGame(gameField);

    }

}


