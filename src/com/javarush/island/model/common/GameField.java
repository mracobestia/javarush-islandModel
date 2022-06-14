package com.javarush.island.model.common;

import com.javarush.island.model.common.exceptions.ObjectInitializationException;
import com.javarush.island.model.settings.Settings;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class GameField {

    private static GameField gameField;
    private int width;
    private int height;
    private FieldPosition[][] positions;
    private Settings settings = new Settings();
    @Setter
    private int numberOfGameDays;

    private GameField(int width, int height) {
        this.height = height;
        this.width = width;
        this.positions = new FieldPosition[this.height][this.width];
    }

    private GameField() {
        this.height = settings.getDefaultGameFieldHeight();
        this.width = settings.getDefaultGameFieldWidth();
        this.positions = new FieldPosition[this.height][this.width];
    }

    public static GameField getInstance() {
        if (gameField==null) {
            gameField = new GameField();
        }

        return gameField;
    }

    public static GameField getInstance(int width, int height) {
        if (gameField==null || gameField.width != width || gameField.height != height) {
            gameField = new GameField(width, height);
        }

        return gameField;
    }

    public void initialize() {
        initializeFieldPositions();
        initializeAnimalsOnPositions();
    }

    public FieldPosition getPosition(int x, int y) {
        return positions[x][y];
    }

    private void initializeFieldPositions() {
        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < positions[i].length; j++) {
                positions[i][j] = new FieldPosition(i, j);
            }
        }
    }

    // Клетки будут заполнены с некоторым шагом от 1 до 2х
    // В каждой клетке будет 2 вида животных, рандомно выбранных из списка классов
    // Для каждого вида количество животных будет выбранно рандомно с учетом максимально допустимого в клетке
    private void initializeAnimalsOnPositions() {

        int step = ThreadLocalRandom.current().nextInt(2) + 1;
        List<Class> possibleClasses = settings.getPossibleAnimalClasses();

        int i = 0;
        int j = 0;
        while (i < this.height && j < this.width) {
            // First species
            int species1 = ThreadLocalRandom.current().nextInt(possibleClasses.size());
            Class aClass = possibleClasses.get(species1);

            // Count of animals of first species
            int maxCount = settings.getMaxNumbersOfSpeciesOnPositionForClass(aClass);
            int countOfSpecies1 = ThreadLocalRandom.current().nextInt(maxCount);

            for (int k = 0; k < countOfSpecies1; k++) {
                Object newInstance = null;
                try {
                    newInstance = aClass.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new ObjectInitializationException("Failed to initialize field with objects.");
                }
                this.positions[i][j].addItemOnPosition((BasicItem) newInstance);
                ((BasicItem) newInstance).setPosition(this.positions[i][j]);
            }

            // Second species
            int species2 = ThreadLocalRandom.current().nextInt(possibleClasses.size()) ;
            if (species2 == species1 && species2 == possibleClasses.size() - 1) {
                species2--;
            } else if (species2 == species1 && species2 != possibleClasses.size() - 1) {
                species2++;
            }
            aClass = possibleClasses.get(species2);

            // Count of animals of second species
            maxCount = settings.getMaxNumbersOfSpeciesOnPositionForClass(aClass);
            int countOfSpecies2 = ThreadLocalRandom.current().nextInt(maxCount);

            for (int k = 0; k < countOfSpecies2; k++) {
                Object newInstance = null;
                try {
                    newInstance = aClass.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new ObjectInitializationException("Failed to initialize field with objects.");
                }
                this.positions[i][j].addItemOnPosition((BasicItem) newInstance);
                ((BasicItem) newInstance).setPosition(this.positions[i][j]);
            }

            j += step;
            if (j >= this.width) {
                j = j - this.width;
                i++;
            }
        }

    }
}
