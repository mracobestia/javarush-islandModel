package com.javarush.island.model.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;

public class Settings {

    @Getter
    @JsonProperty
    private int defaultGameFieldWidth;
    @Getter
    @JsonProperty
    private int defaultGameFieldHeight;
    @Getter
    @JsonProperty
    private int animalReproduceFrequency;
    @Getter
    @JsonProperty
    private List<Class> possibleAnimalClasses;
    @Getter
    @JsonProperty
    private int maxNumberOfDaysWithoutEating;

    @JsonProperty
    private Map<Class, Integer> maxNumbersOfSpeciesOnPosition;
    @JsonProperty
    private Map<Class, Integer> maxTravelSpeed;
    @JsonProperty
    private Map<Class, Double> weights;
    @JsonProperty
    private Map<Class, Double> saturation;
    @JsonProperty
    private Map<Class, Map<Class, Integer>> eatingProbabilitySettings;

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

}
