package com.javarush.island.model.programinterface;

import com.javarush.island.model.common.FieldPosition;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.common.exceptions.AnimalReproducingException;
import com.javarush.island.model.common.exceptions.ObjectInitializationException;
import com.javarush.island.model.plants.Herb;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class GameInterface {

    private static final String START_MESSAGE = """
            Hello, friend! Let's begin the game.
            To start, please enter the number of game days more than 0:""";
    private static final String SAVE_STATISTICS_MESSAGE = "And now, please specify the path to the catalog to save the results of the game:";
    private static final String EXIT_MODE = "exit";
    private static final String FILE_EXTENSION = ".txt";
    private static final String FILE_NAME = "day_";

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

        startGame(gameField, outputCatalogPath);

    }

    public void startGame(GameField gameField, String outputCatalogPath) {

        FieldPosition[][] positions = gameField.getPositions();
        PrintStatistics printer = new PrintStatistics(outputCatalogPath, positions);

        String separator = FileSystems.getDefault().getSeparator();

        printer.printInitializationStatistic();
        for (int i = 0; i < gameField.getNumberOfGameDays(); i++) {

            Herb.grow();

            try (FileWriter fileWriter = new FileWriter(outputCatalogPath + separator + FILE_NAME + (i + 1) + FILE_EXTENSION);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {

                printer.printDayTravellingStatistic(printWriter);
                printer.printDayEatingStatistic(printWriter);
                printer.printDayReproducingStatistic(printWriter);

            } catch (AnimalReproducingException e) {
                System.err.println(e.getErrorMessage());
            } catch (IOException e) {
                System.err.println("Problems with writing statistic files in your directory.");
            }
        }
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
            System.out.println("It is not a directory. Please, select the directory.");
            isValid = false;
        }

        return isValid;

    }
}
