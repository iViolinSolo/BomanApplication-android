package me.violinsolo.boman.subscribe;

import com.clj.fastble.data.BleDevice;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/6/4 3:23 PM
 * @updateAt 2020/6/4 3:23 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public interface BleConnectionStateObserver extends Observer {
    void onBLEDisconneted(BleDevice bleDevice);
    void onBLEConneted(BleDevice bleDevice);
}
