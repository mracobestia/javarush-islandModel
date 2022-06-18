package com.javarush.island.model.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@EqualsAndHashCode
public class FieldPosition {

    private final int x;
    private final int y;
    private List<BasicItem> itemsOnPosition = new CopyOnWriteArrayList<>();
    private ReentrantLock locker = new ReentrantLock();

    public FieldPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void addItemOnPosition(BasicItem item) {
        locker.lock();
        itemsOnPosition.add(item);
        locker.unlock();
    }

    public void clearItemOnPosition(BasicItem item) {
        locker.lock();
        itemsOnPosition.remove(item);
        locker.unlock();
    }

}
