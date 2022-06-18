package com.javarush.island.model.programinterface;

import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.FieldPosition;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrintStatistics {

    private String outputCatalogPath;
    private FieldPosition[][] positions;
    private static final String PRINT_DELIMITER = "//////////////////////////////////////////////////////////////////";
    private static final String INITIALIZE_FILE_NAME = "initialize.txt";
    private static final String INITIALIZE_TITLE = "After initialization:";
    private static final String TRAVELLING_TITLE = "After travelling:";
    private static final String EATING_TITLE = "After eating:";
    private static final String REPRODUCING_TITLE = "After reproducing:";
    private static final String DYING_TITLE = "After dying of hunger:";
    private static final String POSITION_NAME = "Position ";
    private static final String CLASS_NAME = "Class";

    public PrintStatistics(String outputCatalogPath, FieldPosition[][] positions) {
        this.outputCatalogPath = outputCatalogPath;
        this.positions = positions;
    }

    public void printInitializationStatistic() {

        String separator = FileSystems.getDefault().getSeparator();

        try (FileWriter fileWriter = new FileWriter(outputCatalogPath + separator + INITIALIZE_FILE_NAME);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            printWriter.println(INITIALIZE_TITLE);
            statisticsOutput(positions, printWriter);

        } catch (IOException e) {
            System.err.println("Problems with writing statistic files in your directory.");
        }

    }

    public void printDayTravellingStatistic(PrintWriter printWriter) {

        printWriter.println(TRAVELLING_TITLE);
        statisticsOutput(positions, printWriter);
        printWriter.println(PRINT_DELIMITER);

    }

    public void printDayEatingStatistic(PrintWriter printWriter) {

        printWriter.println(EATING_TITLE);
        statisticsOutput(positions, printWriter);
        printWriter.println(PRINT_DELIMITER);

    }

    public void printDayReproducingStatistic(PrintWriter printWriter) {

        printWriter.println(REPRODUCING_TITLE);
        statisticsOutput(positions, printWriter);
        printWriter.println(PRINT_DELIMITER);

    }

    public void printDayDyingOfHungerStatistic(PrintWriter printWriter) {

        printWriter.println(DYING_TITLE);
        statisticsOutput(positions, printWriter);

    }

    private void statisticsOutput(FieldPosition[][] positions, PrintWriter printWriter) {

        int totalNumberOfAnimals = 0;
        Map<Class, Long> totalNumberOfAnimalsByClasses = new HashMap<>();

        for (FieldPosition[] pos : positions) {
            for (FieldPosition fieldPosition : pos) {

               /* String str = POSITION_NAME + (fieldPosition.getX() + 1) + ":" + (fieldPosition.getY() + 1);
                printWriter.println(str);*/

                List<BasicItem> itemsOnPosition = fieldPosition.getItemsOnPosition();
                totalNumberOfAnimals += itemsOnPosition.size();

                Map<Class, Long> itemsOnPositionByClass = itemsOnPosition
                        .stream()
                        .collect(Collectors.groupingBy(BasicItem::getClass, Collectors.counting()));


                itemsOnPositionByClass.forEach((aClass, p) -> {
                    if (totalNumberOfAnimalsByClasses.containsKey(aClass)) {
                        totalNumberOfAnimalsByClasses.put(aClass, totalNumberOfAnimalsByClasses.get(aClass) + p);
                    } else {
                        totalNumberOfAnimalsByClasses.put(aClass, p);
                    }
                });

                /*itemsOnPositionByClass.forEach((aClass, p) -> printWriter.printf(CLASS_NAME + " %s: %s\n", aClass.getSimpleName(), p));
                printWriter.println();*/

            }
        }

        printWriter.println("Total number of animals by classes: ");
        totalNumberOfAnimalsByClasses.forEach((aClass, p) -> printWriter.printf("Class " + " %s: %s\n", aClass.getSimpleName(), p));
        printWriter.println();
        printWriter.println("Total number of animals: " + totalNumberOfAnimals);

    }

}
