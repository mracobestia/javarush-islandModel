package com.javarush.island.model.animals;

import com.javarush.island.model.common.*;
import com.javarush.island.model.common.exceptions.AnimalReproducingException;
import com.javarush.island.model.common.exceptions.AnimalTravellingException;
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
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Animal extends BasicItem {

    private static final double LOWER_SATURATION_THRESHOLD = 0.001;

    private final int maxNumberOfSpeciesOnPosition;
    private final int maxTravelSpeed;
    private final double fullSaturation;
    private final int reproduceFrequency;
    private final UUID id;
    private final Map<Class, Integer> eatingProbabilitySettings;

    @Setter
    private double currentSaturation = 0.0;
    @Setter
    private int numberOfDaysBeforeReproduce = 0;
    @Setter
    private boolean isTravelThisDay = false;
    @Setter
    private int numberOfDaysWithoutEating = 0;

    protected Animal() {
        Settings settings = GameField.getInstance().getSettings();

        maxNumberOfSpeciesOnPosition = settings.getMaxNumbersOfSpeciesOnPositionForClass(getClass());
        maxTravelSpeed = settings.getMaxTravelSpeedForClass(getClass());
        fullSaturation = settings.getSaturationForClass(getClass());
        reproduceFrequency = settings.getAnimalReproduceFrequency();
        id = java.util.UUID.randomUUID();
        eatingProbabilitySettings = settings.getEatingProbabilitySettingsForClass(getClass());

    }

    public BasicItem eat() {

        setSaturation();

        List<BasicItem> itemsOnPosition = this.getPosition().getItemsOnPosition();
        List<BasicItem> edibleItemsOnPosition = itemsOnPosition.stream()
                .filter(el -> eatingProbabilitySettings.containsKey(el.getClass()) && eatingProbabilitySettings.get(el.getClass()) > 0)
                .toList();

        if (edibleItemsOnPosition.isEmpty()) {
            this.setNumberOfDaysWithoutEating(this.getNumberOfDaysWithoutEating()+1);
            return null;
        }

        int itemIndex = ThreadLocalRandom.current().nextInt(edibleItemsOnPosition.size());
        BasicItem itemToEat = edibleItemsOnPosition.get(itemIndex);
        int itemToEatEatingMaxProbability = eatingProbabilitySettings.get(itemToEat.getClass());

        int eatingProbability = ThreadLocalRandom.current().nextInt(101);
        if (eatingProbability <= itemToEatEatingMaxProbability) {

            if (itemToEat.getWeight() > (this.getFullSaturation() - this.getCurrentSaturation())) {
                this.setCurrentSaturation(this.getFullSaturation());
            } else {
                this.setCurrentSaturation(this.getFullSaturation() - this.getCurrentSaturation() + itemToEat.getWeight());
            }

            this.setNumberOfDaysWithoutEating(0);

            return itemToEat;
        } else {
            this.setNumberOfDaysWithoutEating(this.getNumberOfDaysWithoutEating()+1);
        }

        return null;

    }

    public void travel() {

        int travelSpeed = ThreadLocalRandom.current().nextInt(this.getMaxTravelSpeed()) + 1;

        FieldPosition currentPosition = this.getPosition();
        FieldPosition newPosition = travel(travelSpeed, currentPosition);

        ReentrantLock locker = new ReentrantLock();

        locker.lock();
        List<BasicItem> itemsOnNewPosition = newPosition.getItemsOnPosition();
        Map<Class, Long> itemsOnPositionByClass = itemsOnNewPosition
                .stream()
                .collect(Collectors.groupingBy(BasicItem::getClass, Collectors.counting()));

        boolean isThisClassOnPosition = itemsOnPositionByClass.containsKey(this.getClass());
        boolean couldAddMoreAnimals = false;
        if (isThisClassOnPosition) {
            couldAddMoreAnimals = itemsOnPositionByClass.get(this.getClass()) <= (long) this.getMaxNumberOfSpeciesOnPosition();
        }

        if (!isThisClassOnPosition || couldAddMoreAnimals) {
            currentPosition.clearItemOnPosition(this);

            this.setPosition(newPosition);
            this.setTravelThisDay(true);
            newPosition.addItemOnPosition(this);
        }

        try {
            locker.unlock();
        } catch (IllegalMonitorStateException ex ) {
           throw new AnimalTravellingException("Failed the animal travelling because of lock problems.");
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

        Animal child;
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

    private void setSaturation() {

        Settings settings = GameField.getInstance().getSettings();
        double saturation = this.getCurrentSaturation();

        if (saturation != 0) {
            double newSaturation = saturation - this.getFullSaturation() / settings.getMaxNumberOfDaysWithoutEating();
            if (newSaturation < LOWER_SATURATION_THRESHOLD)
                newSaturation = 0;
            this.setCurrentSaturation(newSaturation);
        }

    }

}
