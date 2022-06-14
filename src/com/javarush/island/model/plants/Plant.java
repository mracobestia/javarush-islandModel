package com.javarush.island.model.plants;

import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.settings.Settings;
import lombok.Getter;

@Getter
public abstract class Plant extends BasicItem {

    private final double weight;
    private final int maxNumberOfSpeciesOnPosition;

    protected Plant() {
        Settings settings = GameField.getInstance().getSettings();

        weight = settings.getWeightForClass(getClass());
        maxNumberOfSpeciesOnPosition = settings.getMaxNumbersOfSpeciesOnPositionForClass(getClass());
    }

}
