package com.javarush.island.model.programinterface;

import com.javarush.island.model.animals.Animal;
import com.javarush.island.model.common.FieldPosition;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.common.exceptions.AnimalReproducingException;
import com.javarush.island.model.plants.Herb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

public class PhaseThread extends Thread {

    Phaser phaser;
    String name;
    FieldPosition position;

    PhaseThread(Phaser phaser, String name, FieldPosition position){

        this.phaser = phaser;
        this.name = name;
        this.position = position;

        phaser.register();
    }

    @Override
    public void run(){

        Herb.grow(position);
        phaser.arriveAndAwaitAdvance();

        resetAnimalTravelFlag();
        phaser.arriveAndAwaitAdvance();

        animalsTravelling();
        phaser.arriveAndAwaitAdvance();

        animalsEating();
        phaser.arriveAndAwaitAdvance();

        try {
            animalsReproducing();
        } catch (AnimalReproducingException ex) {
            System.err.println(name + " : " + ex.getErrorMessage());
        } finally {
            if (!phaser.isTerminated()) {
                phaser.forceTermination();
            }
       }

        phaser.arriveAndAwaitAdvance();

        animalsDyingOfHunger();
        phaser.arriveAndDeregister();

    }

    private void resetAnimalTravelFlag() {

        List<Animal> itemsOnPosition = position.getItemsOnPosition()
                .stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .toList();
        for (var item : itemsOnPosition) {
            if (item.isTravelThisDay()) {
                item.setTravelThisDay(false);
            }
        }

    }

    private void animalsTravelling() {

        List<Animal> itemsOnPosition = position.getItemsOnPosition()
                .stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .toList();
        for (var item : itemsOnPosition) {
            if (!item.isTravelThisDay()) {
                item.travel();
            }
        }

    }

    private void animalsEating() {

        List<Animal> itemsToRemove = new ArrayList();
        List<Animal> itemsOnPosition = position.getItemsOnPosition()
                .stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .toList();
        for (var item : itemsOnPosition) {
                Animal eatenItem = item.eat();
                if (eatenItem!=null) {
                    itemsToRemove.add(eatenItem);
                }
        }

        for (var item : itemsToRemove) {
            position.clearItemOnPosition(item);
        }

    }

    private void animalsReproducing() {

        List<Animal> itemsOnPosition = position.getItemsOnPosition()
                .stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .toList();
        for (var item : itemsOnPosition) {
            item.reproduce();
        }

    }

    private void animalsDyingOfHunger() {

        int maxNumberOfDaysWithoutEating = GameField.getInstance().getSettings().getMaxNumberOfDaysWithoutEating();

        List<Animal> itemsOnPosition = position.getItemsOnPosition()
                .stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .filter(el -> el.getNumberOfDaysWithoutEating() >= maxNumberOfDaysWithoutEating)
                .toList();
        for (var item : itemsOnPosition) {
            position.clearItemOnPosition(item);
        }

    }

}
