package com.javarush.island.model.programinterface;

import com.javarush.island.model.common.FieldPosition;
import com.javarush.island.model.common.GameField;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class MainGameThread {

    private static final String FILE_EXTENSION = ".txt";
    private static final String FILE_NAME = "day_";
    private static final String ERROR_TEXT = "Problems with writing statistic files in your directory: ";

    GameField gameField = GameField.getInstance();
    String outputCatalogPath;
    FieldPosition[][] fieldPositions;

    public MainGameThread(String outputCatalogPath) {
        this.outputCatalogPath = outputCatalogPath;
        fieldPositions = gameField.getPositions();
    }

    public void startGame() {

        PrintStatistics printer = new PrintStatistics(outputCatalogPath, fieldPositions);
        String separator = FileSystems.getDefault().getSeparator();

        try {
            printer.printInitializationStatistic();
        } catch (IOException e) {
            System.err.println(ERROR_TEXT + e.getMessage());
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < gameField.getNumberOfGameDays(); i++) {

            Phaser phaser = new Phaser(1);
            for (FieldPosition[] positions : fieldPositions) {
                for (FieldPosition position : positions) {
                    String threadName = "PhaseThread for position " + (position.getX()+1) + ":" + (position.getY()+1);
                    executorService.submit(new PhaseThread(phaser, threadName, position));
                }
            }

            // Herb is growing
            phaser.arriveAndAwaitAdvance();

            // Reset animals travelling flag
            phaser.arriveAndAwaitAdvance();

            try (FileWriter fileWriter = new FileWriter(outputCatalogPath + separator + FILE_NAME + (i + 1) + FILE_EXTENSION);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {

                // Animals travelling
                phaser.arriveAndAwaitAdvance();
                printer.printDayTravellingStatistic(printWriter);

                // Animals eating
                phaser.arriveAndAwaitAdvance();
                printer.printDayEatingStatistic(printWriter);

                // Animals reproducing
                phaser.arriveAndAwaitAdvance();
                printer.printDayReproducingStatistic(printWriter);

                // Animals are dying of hunger
                phaser.arriveAndDeregister();
                printer.printDayDyingOfHungerStatistic(printWriter);

            } catch (IOException e) {
                System.err.println(ERROR_TEXT + e.getMessage());
                executorService.shutdown();
                break;
            }

        }

        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }

    }

}
