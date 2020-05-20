package me.violinsolo.boman.ble;


import com.clj.fastble.data.BleDevice;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public interface Observer {

    void disConnected(BleDevice bleDevice);
}
