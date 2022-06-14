package com.javarush.island.model.animals;

import com.javarush.island.model.common.*;
import com.javarush.island.model.common.exceptions.AnimalReproducingException;
import com.javarush.island.model.settings.Settings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode()
@ToString
public abstract class Animal extends BasicItem {

    private final double weight;
    private final int maxNumberOfSpeciesOnPosition;
    private final int maxTravelSpeed;
    private final double fullSaturation;
    private final int reproduceFrequency;
    private final UUID id;

    @Setter
    private double currentSaturation = 0.0;
    @Setter
    private int numberOfDaysBeforeReproduce = 0;

    protected Animal() {
        Settings settings = GameField.getInstance().getSettings();

        weight = settings.getWeightForClass(getClass());
        maxNumberOfSpeciesOnPosition = settings.getMaxNumbersOfSpeciesOnPositionForClass(getClass());
        maxTravelSpeed = settings.getMaxTravelSpeedForClass(getClass());
        fullSaturation = settings.getSaturationForClass(getClass());
        reproduceFrequency = settings.getAnimalReproduceFrequency();
        id = java.util.UUID.randomUUID();
    }

    public abstract void eat();

    public void travel() {

        GameField gameField = GameField.getInstance();

        int travelSpeed = ThreadLocalRandom.current().nextInt(this.getMaxTravelSpeed()) + 1;

        TravelDirections travelDirection = selectTravelDirection();
        FieldPosition currentPosition = this.getPosition();

        FieldPosition newPosition = null;

        switch (travelDirection) {
            case UP -> {
                int newY = currentPosition.getY() - travelSpeed;
                if (newY < 0) {
                    newPosition = travel(-newY, gameField.getPosition(currentPosition.getX(), 0));
                } else {
                    newPosition = gameField.getPosition(currentPosition.getX(), newY);
                }
            }
            case DOWN -> {
                int newY = currentPosition.getY() + travelSpeed;
                if (newY >= gameField.getWidth()) {
                    newPosition = travel(newY - gameField.getWidth() + 1,
                            gameField.getPosition(currentPosition.getX(), gameField.getWidth()-1));
                } else {
                    newPosition = new FieldPosition(currentPosition.getX(), newY);
                }
            }
            case RIGHT -> {
                int newX = currentPosition.getX() + travelSpeed;
                if (newX >= gameField.getHeight()) {
                    newPosition = travel(newX - gameField.getHeight() + 1,
                            gameField.getPosition(gameField.getHeight()-1, currentPosition.getY()));
                } else {
                    newPosition = gameField.getPosition(newX, currentPosition.getY());
                }
            }
            default -> {
                int newX = currentPosition.getX() - travelSpeed;
                if (newX < 0) {
                    newPosition = travel(-newX, gameField.getPosition(0, currentPosition.getY()));
                } else {
                    newPosition = gameField.getPosition(newX, currentPosition.getY());
                }
            }
        }

        List<BasicItem> itemsOnPosition = currentPosition.getItemsOnPosition();
        Map<Class, Long> itemsOnPositionByClass = itemsOnPosition
                .stream()
                .collect(Collectors.groupingBy(BasicItem::getClass, Collectors.counting()));

        if (itemsOnPositionByClass.containsKey(this.getClass())
                && itemsOnPositionByClass.get(this.getClass()) <= this.getMaxNumberOfSpeciesOnPosition()) {
            currentPosition.clearItemOnPosition(this);

            this.setPosition(newPosition);
            newPosition.addItemOnPosition(this);
        }

    }

    private FieldPosition travel(int travelSpeed, FieldPosition currentPosition) {

        GameField gameField = GameField.getInstance();

        TravelDirections travelDirection = selectTravelDirection();

        switch (travelDirection) {
            case UP -> {
                int newY = currentPosition.getY() - travelSpeed;
                if (newY < 0) {
                    return travel(-newY, gameField.getPosition(currentPosition.getX(), 0));
                } else {
                    return gameField.getPosition(currentPosition.getX(), newY);
                }
            }
            case DOWN -> {
                int newY = currentPosition.getY() + travelSpeed;
                if (newY >= gameField.getWidth()) {
                    return travel(newY - gameField.getWidth() + 1,
                            gameField.getPosition(currentPosition.getX(), gameField.getWidth()-1));
                } else {
                    return gameField.getPosition(currentPosition.getX(), newY);
                }
            }
            case RIGHT -> {
                int newX = currentPosition.getX() + travelSpeed;
                if (newX >= gameField.getHeight()) {
                    return travel(newX - gameField.getHeight() + 1,
                            gameField.getPosition(gameField.getHeight()-1, currentPosition.getY()));
                } else {
                    return gameField.getPosition(newX, currentPosition.getY());
                }
            }
            default -> {
                int newX = currentPosition.getX() - travelSpeed;
                if (newX < 0) {
                    return travel(-newX, gameField.getPosition(0, currentPosition.getY()));
                } else {
                    return gameField.getPosition(newX, currentPosition.getY());
                }
            }

        }
    }

    public void reproduce() {

        if (numberOfDaysBeforeReproduce != 0) {
            this.setNumberOfDaysBeforeReproduce(this.getNumberOfDaysBeforeReproduce()-1);
            return;
        }

        FieldPosition currentPosition = this.getPosition();

        List<BasicItem> itemsOnPosition = currentPosition.getItemsOnPosition();
        List<Animal> itemsOnPositionForReproduce = itemsOnPosition.stream()
                .filter(el -> el.getClass().equals(this.getClass()))
                .map(Animal.class::cast)
                .filter(el -> el.getNumberOfDaysBeforeReproduce() == 0 && !el.getId().equals(this.getId()))
                .toList();

        if (itemsOnPositionForReproduce.isEmpty())
            return;

        int itemIndex = ThreadLocalRandom.current().nextInt(itemsOnPositionForReproduce.size());
        Animal itemToReproduce = itemsOnPositionForReproduce.get(itemIndex);

        itemToReproduce.setNumberOfDaysBeforeReproduce(itemToReproduce.getReproduceFrequency());
        this.setNumberOfDaysBeforeReproduce(this.getReproduceFrequency());

        Map<Class, Long> itemsOnPositionByClass = itemsOnPosition
                .stream()
                .collect(Collectors.groupingBy(BasicItem::getClass, Collectors.counting()));

        if (itemsOnPositionByClass.get(this.getClass()) == this.getMaxNumberOfSpeciesOnPosition()) {
            return;
        }

        Animal child = null;
        try {
            child = this.getClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new AnimalReproducingException("Failed to create new animal in reproducing.");
        }
        child.setPosition(this.getPosition());
        this.getPosition().addItemOnPosition(child);
        child.setNumberOfDaysBeforeReproduce(itemToReproduce.getReproduceFrequency());

    }

    public static boolean isAnimal(Class aClass) {

        Class superClass = aClass.getSuperclass();
        if (superClass == null)
            return false;
        else if (superClass.equals(Animal.class))
            return true;

        return isAnimal(superClass);

    }

    private TravelDirections selectTravelDirection() {
        int bound = TravelDirections.values().length;
        int travelDirectionIndex = ThreadLocalRandom.current().nextInt(bound);
        return TravelDirections.values()[travelDirectionIndex];
    }
}
