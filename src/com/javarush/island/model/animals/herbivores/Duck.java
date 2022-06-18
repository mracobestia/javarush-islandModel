package com.javarush.island.model.animals.herbivores;

import com.javarush.island.model.animals.Animal;
import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.plants.Plant;
import com.javarush.island.model.settings.Settings;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@EqualsAndHashCode(callSuper = true)
public class Duck extends Herbivore {

    private final Map<Class, Integer> eatingProbabilitySettings;

    public Duck() {
        Settings settings = GameField.getInstance().getSettings();
        eatingProbabilitySettings = settings.getEatingProbabilitySettingsForClass(getClass());
    }

    @Override
    public Animal eat() {

        List<BasicItem> itemsOnPosition = this.getPosition().getItemsOnPosition();
        List<Animal> edibleItemsOnPosition = itemsOnPosition.stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .filter(el -> eatingProbabilitySettings.containsKey(el.getClass()) && eatingProbabilitySettings.get(el.getClass()) > 0)
                .toList();

        if (edibleItemsOnPosition.isEmpty()) {
            eatPlants(itemsOnPosition);
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
            eatPlants(itemsOnPosition);
        }

        return null;

    }

    public void eatPlants(List<BasicItem> itemsOnPosition) {

        List<BasicItem> plantsOnPosition = itemsOnPosition.stream()
                .filter(el -> el.getClass().getSuperclass().equals(Plant.class))
                .toList();

        if (plantsOnPosition.isEmpty()) {
            this.setNumberOfDaysWithoutEating(this.getNumberOfDaysWithoutEating()+1);
            return;
        }

        int plantIndex = ThreadLocalRandom.current().nextInt(plantsOnPosition.size());
        Plant plantToEat = (Plant) plantsOnPosition.get(plantIndex);

        itemsOnPosition.remove(plantToEat);

        if (plantToEat.getWeight() > (this.getFullSaturation() - this.getCurrentSaturation())) {
            this.setCurrentSaturation(this.getFullSaturation());
        } else {
            this.setCurrentSaturation(this.getFullSaturation() - this.getCurrentSaturation() + plantToEat.getWeight());
        }

        this.setNumberOfDaysWithoutEating(0);

    }

}
