package me.violinsolo.boman.util;

import com.clj.fastble.data.BleDevice;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/27 7:52 PM
 * @updateAt 2020/5/27 7:52 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class Intermediate {
    public static Intermediate getInstance() {
        return IntermediateHolder.instance;
    }

    private static class IntermediateHolder {
        private static final Intermediate instance = new Intermediate();
    }


    public boolean statusIsScanning = false;

    // holder for current bound device ...
    public BleDevice mBleHolder = null;
}
