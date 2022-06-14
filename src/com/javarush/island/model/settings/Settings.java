package com.javarush.island.model.settings;


import lombok.Getter;
import com.javarush.island.model.animals.herbivores.*;
import com.javarush.island.model.animals.predators.*;
import com.javarush.island.model.plants.Herb;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Settings {

    @Getter
    private final int defaultGameFieldWidth = 5;
    @Getter
    private final int defaultGameFieldHeight = 10;
    @Getter
    private final int animalReproduceFrequency = 2;
    @Getter
    private final List<Class> possibleAnimalClasses = new CopyOnWriteArrayList<>();
    @Getter
    private final int maxNumberOfDaysWithoutEating = 2;

    private final Map<Class, Integer> maxNumbersOfSpeciesOnPosition = new ConcurrentHashMap<>();
    private final Map<Class, Integer> maxTravelSpeed = new ConcurrentHashMap<>();
    private final Map<Class, Double> weights = new ConcurrentHashMap<>();
    private final Map<Class, Double> saturation = new ConcurrentHashMap<>();
    private final Map<Class, Map<Class, Integer>> eatingProbabilitySettings = new ConcurrentHashMap<>();

    public Settings() {
        initializeAnimalPossibleClasses();
        initializeMaxNumbersOfSpeciesOnPosition();
        initializeMaxTravelSpeed();
        initializeWeights();
        initializeSaturation();
        initializeEatingProbabilitySettings();
    }

    public int getMaxTravelSpeedForClass(Class aClass) {
        return maxTravelSpeed.get(aClass);
    }

    public int getMaxNumbersOfSpeciesOnPositionForClass(Class aClass) {
        return maxNumbersOfSpeciesOnPosition.get(aClass);
    }

    public double getWeightForClass(Class aClass) {
        return weights.get(aClass);
    }

    public double getSaturationForClass(Class aClass) {
        return saturation.get(aClass);
    }

    public Map<Class, Integer> getEatingProbabilitySettingsForClass(Class aClass) {
        return eatingProbabilitySettings.get(aClass);
    }


    private void initializeMaxNumbersOfSpeciesOnPosition() {

        maxNumbersOfSpeciesOnPosition.put(Wolf.class, 30);
        maxNumbersOfSpeciesOnPosition.put(Boa.class, 30);
        maxNumbersOfSpeciesOnPosition.put(Fox.class, 30);
        maxNumbersOfSpeciesOnPosition.put(Bear.class, 5);
        maxNumbersOfSpeciesOnPosition.put(Eagle.class, 20);

        maxNumbersOfSpeciesOnPosition.put(Horse.class, 20);
        maxNumbersOfSpeciesOnPosition.put(Deer.class, 20);
        maxNumbersOfSpeciesOnPosition.put(Rabbit.class, 150);
        maxNumbersOfSpeciesOnPosition.put(Mouse.class, 500);
        maxNumbersOfSpeciesOnPosition.put(Goat.class, 140);
        maxNumbersOfSpeciesOnPosition.put(Sheep.class, 140);
        maxNumbersOfSpeciesOnPosition.put(Boar.class, 50);
        maxNumbersOfSpeciesOnPosition.put(Buffalo.class, 10);
        maxNumbersOfSpeciesOnPosition.put(Duck.class, 200);
        maxNumbersOfSpeciesOnPosition.put(Caterpillar.class, 1000);

        maxNumbersOfSpeciesOnPosition.put(Herb.class, 200);

    }

    private void initializeAnimalPossibleClasses() {

        possibleAnimalClasses.add(Wolf.class);
        possibleAnimalClasses.add(Boa.class);
        possibleAnimalClasses.add(Fox.class);
        possibleAnimalClasses.add(Bear.class);
        possibleAnimalClasses.add(Eagle.class);

        possibleAnimalClasses.add(Horse.class);
        possibleAnimalClasses.add(Deer.class);
        possibleAnimalClasses.add(Rabbit.class);
        possibleAnimalClasses.add(Mouse.class);
        possibleAnimalClasses.add(Goat.class);
        possibleAnimalClasses.add(Sheep.class);
        possibleAnimalClasses.add(Boar.class);
        possibleAnimalClasses.add(Buffalo.class);
        possibleAnimalClasses.add(Duck.class);
        possibleAnimalClasses.add(Caterpillar.class);

    }

    private void initializeMaxTravelSpeed() {

        maxTravelSpeed.put(Wolf.class, 3);
        maxTravelSpeed.put(Boa.class, 1);
        maxTravelSpeed.put(Fox.class, 2);
        maxTravelSpeed.put(Bear.class, 2);
        maxTravelSpeed.put(Eagle.class, 3);

        maxTravelSpeed.put(Horse.class, 4);
        maxTravelSpeed.put(Deer.class, 4);
        maxTravelSpeed.put(Rabbit.class, 2);
        maxTravelSpeed.put(Mouse.class, 1);
        maxTravelSpeed.put(Goat.class, 3);
        maxTravelSpeed.put(Sheep.class, 3);
        maxTravelSpeed.put(Boar.class, 2);
        maxTravelSpeed.put(Buffalo.class, 3);
        maxTravelSpeed.put(Duck.class, 4);
        maxTravelSpeed.put(Caterpillar.class, 0);

    }

    private void initializeWeights() {

        weights.put(Wolf.class, 50.0);
        weights.put(Boa.class, 15.0);
        weights.put(Fox.class, 8.0);
        weights.put(Bear.class, 500.0);
        weights.put(Eagle.class, 6.0);

        weights.put(Horse.class, 400.0);
        weights.put(Deer.class, 300.0);
        weights.put(Rabbit.class, 2.0);
        weights.put(Mouse.class, 0.05);
        weights.put(Goat.class, 60.0);
        weights.put(Sheep.class, 70.0);
        weights.put(Boar.class, 400.0);
        weights.put(Buffalo.class, 700.0);
        weights.put(Duck.class, 1.0);
        weights.put(Caterpillar.class, 0.01);

        weights.put(Herb.class, 1.0);

    }

    private void initializeSaturation() {

        saturation.put(Wolf.class, 8.0);
        saturation.put(Boa.class, 3.0);
        saturation.put(Fox.class, 2.0);
        saturation.put(Bear.class, 80.0);
        saturation.put(Eagle.class, 1.0);

        saturation.put(Horse.class, 60.0);
        saturation.put(Deer.class, 50.0);
        saturation.put(Rabbit.class, 0.45);
        saturation.put(Mouse.class, 0.01);
        saturation.put(Goat.class, 10.0);
        saturation.put(Sheep.class, 15.0);
        saturation.put(Boar.class, 50.0);
        saturation.put(Buffalo.class, 100.0);
        saturation.put(Duck.class, 0.15);
        saturation.put(Caterpillar.class, 0.0);

    }

    private void initializeEatingProbabilitySettings() {

        Map<Class, Integer> wolfsEatingProbability = new ConcurrentHashMap<>();
        wolfsEatingProbability.put(Horse.class, 10);
        wolfsEatingProbability.put(Deer.class, 15);
        wolfsEatingProbability.put(Rabbit.class, 60);
        wolfsEatingProbability.put(Mouse.class, 80);
        wolfsEatingProbability.put(Goat.class, 60);
        wolfsEatingProbability.put(Sheep.class, 70);
        wolfsEatingProbability.put(Boar.class, 15);
        wolfsEatingProbability.put(Buffalo.class, 10);
        wolfsEatingProbability.put(Duck.class, 40);

        Map<Class, Integer> boasEatingProbability = new ConcurrentHashMap<>();
        boasEatingProbability.put(Fox.class, 15);
        boasEatingProbability.put(Rabbit.class, 20);
        boasEatingProbability.put(Mouse.class, 40);
        boasEatingProbability.put(Duck.class, 10);

        Map<Class, Integer> foxesEatingProbability = new ConcurrentHashMap<>();
        foxesEatingProbability.put(Rabbit.class, 70);
        foxesEatingProbability.put(Mouse.class, 90);
        foxesEatingProbability.put(Duck.class, 60);
        foxesEatingProbability.put(Caterpillar.class, 40);

        Map<Class, Integer> bearsEatingProbability = new ConcurrentHashMap<>();
        bearsEatingProbability.put(Boa.class, 80);
        bearsEatingProbability.put(Horse.class, 40);
        bearsEatingProbability.put(Deer.class, 80);
        bearsEatingProbability.put(Rabbit.class, 80);
        bearsEatingProbability.put(Mouse.class, 90);
        bearsEatingProbability.put(Goat.class, 70);
        bearsEatingProbability.put(Sheep.class, 70);
        bearsEatingProbability.put(Boar.class, 50);
        bearsEatingProbability.put(Buffalo.class, 20);
        bearsEatingProbability.put(Duck.class, 10);

        Map<Class, Integer> eaglesEatingProbability = new ConcurrentHashMap<>();
        eaglesEatingProbability.put(Fox.class, 10);
        eaglesEatingProbability.put(Rabbit.class, 90);
        eaglesEatingProbability.put(Mouse.class, 90);
        eaglesEatingProbability.put(Duck.class, 80);

        Map<Class, Integer> miceEatingProbability = new ConcurrentHashMap<>();
        miceEatingProbability.put(Caterpillar.class, 90);

        Map<Class, Integer> boarsEatingProbability = new ConcurrentHashMap<>();
        boarsEatingProbability.put(Mouse.class, 50);
        boarsEatingProbability.put(Caterpillar.class, 90);

        Map<Class, Integer> ducksEatingProbability = new ConcurrentHashMap<>();
        ducksEatingProbability.put(Caterpillar.class, 90);

        eatingProbabilitySettings.put(Wolf.class, wolfsEatingProbability);
        eatingProbabilitySettings.put(Boa.class, boasEatingProbability);
        eatingProbabilitySettings.put(Fox.class, foxesEatingProbability);
        eatingProbabilitySettings.put(Bear.class, bearsEatingProbability);
        eatingProbabilitySettings.put(Eagle.class, eaglesEatingProbability);

        eatingProbabilitySettings.put(Mouse.class, miceEatingProbability);
        eatingProbabilitySettings.put(Boar.class, boarsEatingProbability);
        eatingProbabilitySettings.put(Duck.class, ducksEatingProbability);

    }

}
