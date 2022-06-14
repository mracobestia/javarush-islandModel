package com.javarush.island.model.plants;

import com.javarush.island.model.common.FieldPosition;
import com.javarush.island.model.common.GameField;

import java.util.concurrent.ThreadLocalRandom;

public class Herb extends Plant {

    public static void grow() {

        FieldPosition[][] fieldPositions = GameField.getInstance().getPositions();
        for (FieldPosition[] positions : fieldPositions) {
            for (FieldPosition position : positions) {
                int herbCount = ThreadLocalRandom.current().nextInt(new Herb().getMaxNumberOfSpeciesOnPosition()/2);
                while (herbCount > 0) {
                    Herb herb = new Herb();
                    herb.setPosition(position);
                    position.addItemOnPosition(herb);

                    herbCount--;
                }
            }
        }
    }

}
