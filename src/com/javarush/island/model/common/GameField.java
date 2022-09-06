package com.javarush.island.model.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.javarush.island.model.common.exceptions.ObjectInitializationException;
import com.javarush.island.model.settings.Settings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class GameField {

    private static final String ERROR_TEXT = "Failed to initialize field with objects.";

    private static GameField gameField;
    private int width;
    private int height;
    private FieldPosition[][] positions;
    private Settings settings;
    @Setter
    private int numberOfGameDays;

    private GameField() {

    }

    public static GameField getInstance() {
        if (gameField==null) {
            gameField = new GameField();
        }

        return gameField;
    }

    public void initialize() {
        initializeBySettings();
        initializeFieldPositions();
        initializeAnimalsOnPositions();
    }

    public FieldPosition getPosition(int x, int y) {
        return positions[x][y];
    }

    private void initializeBySettings() {

        ClassLoader classLoader = getClass().getClassLoader();;
        File file = new File(classLoader.getResource("settings.yaml").getFile());

        ObjectMapper objectMapper = new YAMLMapper();

        try {
            settings = objectMapper.readValue(file, Settings.class);
        } catch (Exception e) {
            throw new ObjectInitializationException(ERROR_TEXT);
        }

        this.height = settings.getDefaultGameFieldHeight();
        this.width = settings.getDefaultGameFieldWidth();
        this.positions = new FieldPosition[this.height][this.width];

    }

    private void initializeFieldPositions() {
        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < positions[i].length; j++) {
                positions[i][j] = new FieldPosition(i, j);
            }
        }
    }

    /**
     * Клетки будут заполнены с некоторым шагом от 1 до 2х
     * В каждой клетке будет 2 вида животных, рандомно выбранных из списка классов
     * Для каждого вида количество животных будет выбранно рандомно с учетом максимально допустимого в клетке
     */
    private void initializeAnimalsOnPositions() {

        int stepForFieldFillingByAnimals = ThreadLocalRandom.current().nextInt(2) + 1;
        List<Class> possibleAnimalsClassesToFillField = settings.getPossibleAnimalClasses();

        int heightCounter = 0;
        int widthCounter = 0;
        while (heightCounter < this.height && widthCounter < this.width) {
            // First species
            int firstSpeciesOfAnimalToFill = ThreadLocalRandom.current().nextInt(possibleAnimalsClassesToFillField.size());
            Class firstClassOfAnimalToFill = possibleAnimalsClassesToFillField.get(firstSpeciesOfAnimalToFill);

            // Count of animals of first species
            int maxCountClassOfAnimal = settings.getMaxNumbersOfSpeciesOnPositionForClass(firstClassOfAnimalToFill);
            int countFirstClassOfAnimalToFill = ThreadLocalRandom.current().nextInt(maxCountClassOfAnimal);

            for (int k = 0; k < countFirstClassOfAnimalToFill; k++) {
                Object newAnimalInstance;
                try {
                    newAnimalInstance = firstClassOfAnimalToFill.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new ObjectInitializationException(ERROR_TEXT);
                }
                this.positions[heightCounter][widthCounter].addItemOnPosition((BasicItem) newAnimalInstance);
                ((BasicItem) newAnimalInstance).setPosition(this.positions[heightCounter][widthCounter]);
            }

            // Second species
            int secondSpeciesOfAnimalToFill = ThreadLocalRandom.current().nextInt(possibleAnimalsClassesToFillField.size()) ;
            if (secondSpeciesOfAnimalToFill == firstSpeciesOfAnimalToFill && secondSpeciesOfAnimalToFill == possibleAnimalsClassesToFillField.size() - 1) {
                secondSpeciesOfAnimalToFill--;
            } else if (secondSpeciesOfAnimalToFill == firstSpeciesOfAnimalToFill && secondSpeciesOfAnimalToFill != possibleAnimalsClassesToFillField.size() - 1) {
                secondSpeciesOfAnimalToFill++;
            }
            firstClassOfAnimalToFill = possibleAnimalsClassesToFillField.get(secondSpeciesOfAnimalToFill);

            // Count of animals of second species
            maxCountClassOfAnimal = settings.getMaxNumbersOfSpeciesOnPositionForClass(firstClassOfAnimalToFill);
            int countSecondClassOfAnimalToFill = ThreadLocalRandom.current().nextInt(maxCountClassOfAnimal);

            for (int k = 0; k < countSecondClassOfAnimalToFill; k++) {
                Object newAnimalInstance;
                try {
                    newAnimalInstance = firstClassOfAnimalToFill.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new ObjectInitializationException(ERROR_TEXT);
                }
                this.positions[heightCounter][widthCounter].addItemOnPosition((BasicItem) newAnimalInstance);
                ((BasicItem) newAnimalInstance).setPosition(this.positions[heightCounter][widthCounter]);
            }

            widthCounter += stepForFieldFillingByAnimals;
            if (widthCounter >= this.width) {
                widthCounter = widthCounter - this.width;
                heightCounter++;
            }
        }

    }
}
