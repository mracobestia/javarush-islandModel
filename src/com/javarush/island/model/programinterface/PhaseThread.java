package com.javarush.island.model.programinterface;

import com.javarush.island.model.animals.Animal;
import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.FieldPosition;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.common.exceptions.AnimalReproducingException;
import com.javarush.island.model.common.exceptions.AnimalTravellingException;
import com.javarush.island.model.plants.Herb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

public class PhaseThread implements Runnable {

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

        try{
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            errorProcessing(ex.getMessage());
        }

        resetAnimalTravelFlag();
        phaser.arriveAndAwaitAdvance();

        try{
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            errorProcessing(ex.getMessage());
        }

        try {
            animalsTravelling();
        } catch (AnimalTravellingException ex) {
            errorProcessing(ex.getErrorMessage());
        }
        phaser.arriveAndAwaitAdvance();

        try{
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            errorProcessing(ex.getMessage());
        }

        animalsEating();
        phaser.arriveAndAwaitAdvance();

        try{
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            errorProcessing(ex.getMessage());
        }

        try {
            animalsReproducing();
        } catch (AnimalReproducingException ex) {
            errorProcessing(ex.getErrorMessage());
        }
        phaser.arriveAndAwaitAdvance();

        try{
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            errorProcessing(ex.getMessage());
        }

        animalsDyingOfHunger();
        phaser.arriveAndDeregister();

    }

    private void errorProcessing(String errorMessage) {
        System.err.println(name + " : " + errorMessage);

        if (!phaser.isTerminated()) {
            phaser.forceTermination();
        }
        if (!Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
        }
    }

    private List<Animal> getAnimalList() {
        return position.getItemsOnPosition()
                .stream()
                .filter(el -> Animal.isAnimal(el.getClass()))
                .map(Animal.class::cast)
                .toList();
    }

    private void resetAnimalTravelFlag() {

        List<Animal> itemsOnPosition = getAnimalList();
        for (var item : itemsOnPosition) {
            if (item.isTravelThisDay()) {
                item.setTravelThisDay(false);
            }
        }

    }

    private void animalsTravelling() {

        List<Animal> itemsOnPosition = getAnimalList();
        for (var item : itemsOnPosition) {
            if (!item.isTravelThisDay()) {
                item.travel();
            }
        }

    }

    private void animalsEating() {

        List<BasicItem> itemsToRemove = new ArrayList<>();
        List<Animal> itemsOnPosition = getAnimalList();
        for (var item : itemsOnPosition) {
            BasicItem eatenItem = item.eat();
            if (eatenItem != null) {
                itemsToRemove.add(eatenItem);
            }
        }

        for (var item : itemsToRemove) {
            position.clearItemOnPosition(item);
        }

    }

    private void animalsReproducing() {

        List<Animal> itemsOnPosition = getAnimalList();
        for (var item : itemsOnPosition) {
            item.reproduce();
        }

    }

    private void animalsDyingOfHunger() {

        int maxNumberOfDaysWithoutEating = GameField.getInstance().getSettings().getMaxNumberOfDaysWithoutEating();

        List<Animal> itemsOnPosition = getAnimalList().stream()
                .filter(el -> el.getNumberOfDaysWithoutEating() >= maxNumberOfDaysWithoutEating)
                .toList();
        for (var item : itemsOnPosition) {
            position.clearItemOnPosition(item);
        }

    }

}
