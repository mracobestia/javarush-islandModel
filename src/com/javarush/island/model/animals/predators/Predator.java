package com.javarush.island.model.animals.predators;

import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.settings.Settings;
import com.javarush.island.model.animals.Animal;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@EqualsAndHashCode(callSuper = true)
public class Predator extends Animal {

    private final Map<Class, Integer> eatingProbabilitySettings;

    public Predator() {
        Settings settings = GameField.getInstance().getSettings();
        eatingProbabilitySettings = settings.getEatingProbabilitySettingsForClass(getClass());
    }

    @Override
    public Animal eat() {

        Settings settings = GameField.getInstance().getSettings();
        double saturation = this.getCurrentSaturation();

        if (saturation != 0) {
            double newSaturation = saturation - this.getFullSaturation() / settings.getMaxNumberOfDaysWithoutEating();
            if (newSaturation < 0.001)
                newSaturation = 0;
            this.setCurrentSaturation(newSaturation);
        }

        List<BasicItem> itemsOnPosition = this.getPosition().getItemsOnPosition();
        List<Animal> edibleItemsOnPosition = itemsOnPosition.stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .filter(el -> eatingProbabilitySettings.containsKey(el.getClass()) && eatingProbabilitySettings.get(el.getClass()) > 0)
                .toList();

        if (edibleItemsOnPosition.isEmpty()) {
            this.setNumberOfDaysWithoutEating(this.getNumberOfDaysWithoutEating()+1);
            return null;
        }

        int itemIndex = ThreadLocalRandom.current().nextInt(edibleItemsOnPosition.size());
        Animal animalToEat = edibleItemsOnPosition.get(itemIndex);
        int itemToEatEatingMaxProbability = eatingProbabilitySettings.get(animalToEat.getClass());

        int eatingProbability = ThreadLocalRandom.current().nextInt(101);
        if (eatingProbability < itemToEatEatingMaxProbability) {

            if (animalToEat.getWeight() > (this.getFullSaturation() - this.getCurrentSaturation())) {
                this.setCurrentSaturation(this.getFullSaturation());
            } else {
                this.setCurrentSaturation(this.getFullSaturation() - this.getCurrentSaturation() + animalToEat.getWeight());
            }

            this.setNumberOfDaysWithoutEating(0);

            return animalToEat;
        } else {
            this.setNumberOfDaysWithoutEating(this.getNumberOfDaysWithoutEating()+1);
        }

        return null;

    }

}
