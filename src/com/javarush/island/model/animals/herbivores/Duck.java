package com.javarush.island.model.animals.herbivores;

import com.javarush.island.model.animals.Animal;
import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.settings.Settings;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Duck extends Herbivore {

    private Map<Class, Integer> eatingProbabilitySettings;

    public Duck() {
        Settings settings = GameField.getInstance().getSettings();
        eatingProbabilitySettings = settings.getEatingProbabilitySettingsForClass(getClass());
    }

    @Override
    public void eat() {
        super.eat();

        List<BasicItem> itemsOnPosition = this.getPosition().getItemsOnPosition();
        List<Animal> edibleItemsOnPosition = itemsOnPosition.stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .filter(el -> eatingProbabilitySettings.containsKey(el.getClass()) && eatingProbabilitySettings.get(el.getClass()) > 0)
                .toList();

        if (edibleItemsOnPosition.isEmpty())
            return;

        int itemIndex = ThreadLocalRandom.current().nextInt(edibleItemsOnPosition.size());
        Animal animalToEat = edibleItemsOnPosition.get(itemIndex);
        int itemToEatEatingMaxProbability = eatingProbabilitySettings.get(animalToEat.getClass());

        int eatingProbability = ThreadLocalRandom.current().nextInt(101);
        if (eatingProbability < itemToEatEatingMaxProbability) {
            itemsOnPosition.remove(animalToEat);

            if (animalToEat.getWeight() > (this.getFullSaturation() - this.getCurrentSaturation())) {
                this.setCurrentSaturation(this.getFullSaturation());
            } else {
                this.setCurrentSaturation(this.getFullSaturation() - this.getCurrentSaturation() + animalToEat.getWeight());
            }
        }
    }
}
