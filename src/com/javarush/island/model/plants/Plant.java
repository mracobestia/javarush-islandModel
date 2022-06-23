package com.javarush.island.model.plants;

import com.javarush.island.model.common.BasicItem;
import com.javarush.island.model.common.GameField;
import com.javarush.island.model.settings.Settings;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class Plant extends BasicItem {

    private final int maxNumberOfSpeciesOnPosition;

    protected Plant() {
        Settings settings = GameField.getInstance().getSettings();
        maxNumberOfSpeciesOnPosition = settings.getMaxNumbersOfSpeciesOnPositionForClass(getClass());
    }

}
