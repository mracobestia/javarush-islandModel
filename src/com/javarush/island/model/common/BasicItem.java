package com.javarush.island.model.common;

import com.javarush.island.model.settings.Settings;
import lombok.Data;

@Data
public abstract class BasicItem {

    private FieldPosition position;
    private final double weight;

    public BasicItem() {
        Settings settings = GameField.getInstance().getSettings();
        weight = settings.getWeightForClass(getClass());
    }

}
