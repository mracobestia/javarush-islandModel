package com.javarush.island.model.programinterface;

import com.javarush.island.model.common.GameField;
import com.javarush.island.model.common.exceptions.ObjectInitializationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class GameInterface {

    private static final String START_MESSAGE = """
            Hello, friend! Let's begin the game.
            To start, please enter the number of game days more than 0:""";
    private static final String SAVE_STATISTICS_MESSAGE = "And now, please specify the path to the catalog to save the results of the game:";
    private static final String EXIT_MODE = "exit";

    public void initializeGame(GameField gameField) {

        System.out.println(START_MESSAGE);

        Scanner dayScanner = new Scanner(System.in);
        int numberOfGameDays = 0;
        while (numberOfGameDays <= 0) {
            numberOfGameDays = dayScanner.nextInt();
        }

        gameField.setNumberOfGameDays(numberOfGameDays);

        System.out.println(SAVE_STATISTICS_MESSAGE);
        Scanner catalogPathScanner = new Scanner(System.in);
        String outputCatalogPath = "";
        do {
            outputCatalogPath = catalogPathScanner.nextLine();
            if (isExit(outputCatalogPath)) {
                System.exit(1);
            }
        } while (!isFilePathValid(outputCatalogPath));

        try {
            gameField.initialize();
        } catch (ObjectInitializationException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        MainGameThread mainGameThread = new MainGameThread(outputCatalogPath);
        mainGameThread.startGame();

    }

    private boolean isExit(String userLine) {
        return userLine.equals(EXIT_MODE);
    }

    private boolean isFilePathValid(String filePath) {

        if (filePath.length() == 0) {
            return false;
        }

        boolean isValid = true;
        Path path = Paths.get(filePath);

        if (!Files.isDirectory(path)) {
            System.err.println("It is not a directory. Please, select the directory.");
            isValid = false;
        }

        return isValid;

    }
}

