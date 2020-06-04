package me.violinsolo.boman.subscribe;


import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class ObserverManager implements Observable {

    public static ObserverManager getInstance() {
        return ObserverManagerHolder.sObserverManager;
    }

    private static class ObserverManagerHolder {
        private static final ObserverManager sObserverManager = new ObserverManager();
    }

    private List<Observer> observers = new ArrayList<>();

    @Override
    public synchronized void addObserver(Observer obj) {
        observers.add(obj);
    }

    @Override
    public synchronized void deleteObserver(Observer obj) {
        int i = observers.indexOf(obj);
        if (i >= 0) {
            observers.remove(obj);
        }
    }

    @Override
    public synchronized void notifyObserverWhenConnected(BleDevice bleDevice) {
        for (int i = 0; i < observers.size(); i++) {
            Observer o = observers.get(i);
            if (o instanceof BleConnectionStateObserver) {
                ((BleConnectionStateObserver) o).onBLEConneted(bleDevice);
            }
        }
    }

    @Override
    public synchronized void notifyObserverWhenDisonnected(BleDevice bleDevice) {
        for (int i = 0; i < observers.size(); i++) {
            Observer o = observers.get(i);
            if (o instanceof BleConnectionStateObserver) {
                ((BleConnectionStateObserver) o).onBLEDisconneted(bleDevice);
            }
        }
    }

}
