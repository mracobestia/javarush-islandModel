package com.javarush.island.model.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@EqualsAndHashCode
public class FieldPosition {

    private int x;
    private int y;
    private List<BasicItem> itemsOnPosition = new CopyOnWriteArrayList<>();

    public FieldPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void addItemOnPosition(BasicItem item) {
        itemsOnPosition.add(item);
    }

    public void clearItemOnPosition(BasicItem item) {
        itemsOnPosition.remove(item);
    }

}
