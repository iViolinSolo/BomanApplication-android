package me.violinsolo.boman.util;

import com.clj.fastble.data.BleDevice;

import me.violinsolo.boman.model.BleBoundDevice;

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

    // holder for app-level temperature unit
    public boolean isCelsius = true;

    // holder for app-level temperature mode,
    // true means it's body temperature mode, else is surface mode.
    public boolean isBodyTempMode = true;

    public enum DeviceTestMode {
        NORMAL_TEMPERATURE_MEASUREMENT_MODE, // default
        FACTORY_MODE,
        CONTINUOUSLY_MEASURING_MODE
    }
    // holder for app-level device mode, usually used in test page...
    public DeviceTestMode currentDeviceMode = DeviceTestMode.NORMAL_TEMPERATURE_MEASUREMENT_MODE;

    // holder for ble-level state of charge
    public int socVal = BleBoundDevice.SOC_NO_VALUE;
}
