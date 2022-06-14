package com.javarush.island.model.animals.predators;

import lombok.EqualsAndHashCode;
import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.settings.Settings;
import com.javarush.island.model.animals.Animal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@EqualsAndHashCode
public class Predator extends Animal {

    private final Map<Class, Integer> eatingProbabilitySettings;

    public Predator() {
        Settings settings = GameField.getInstance().getSettings();
        eatingProbabilitySettings = settings.getEatingProbabilitySettingsForClass(getClass());
    }

    @Override
    public void eat() {

        Settings settings = GameField.getInstance().getSettings();
        double saturation = this.getCurrentSaturation();

        if (saturation != 0) {
            double newSaturation = saturation - this.getFullSaturation() / settings.getMaxNumberOfDaysWithoutEating();
            if (newSaturation < 0.001)
                newSaturation = 0;
            this.setCurrentSaturation(newSaturation);
        }

        List<BasicItem> itemsOnPosition = this.getPosition().getItemsOnPosition();
        List<BasicItem> edibleItemsOnPosition = itemsOnPosition.stream()
                .filter(el -> eatingProbabilitySettings.containsKey(el.getClass()) && eatingProbabilitySettings.get(el.getClass()) > 0)
                .toList();

        if (edibleItemsOnPosition.isEmpty())
            return;

        int itemIndex = ThreadLocalRandom.current().nextInt(edibleItemsOnPosition.size());
        BasicItem itemToEat = edibleItemsOnPosition.get(itemIndex);
        int itemToEatEatingMaxProbability = eatingProbabilitySettings.get(itemToEat.getClass());

        int eatingProbability = ThreadLocalRandom.current().nextInt(101);
        if (eatingProbability < itemToEatEatingMaxProbability && itemToEat instanceof Animal animalToEat) {
            itemsOnPosition.remove(itemToEat);

            if (animalToEat.getWeight() > (this.getFullSaturation() - this.getCurrentSaturation())) {
                this.setCurrentSaturation(this.getFullSaturation());
            } else {
                this.setCurrentSaturation(this.getFullSaturation() - this.getCurrentSaturation() + animalToEat.getWeight());
            }
        }

    }

}
