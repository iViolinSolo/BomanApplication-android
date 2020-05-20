package me.violinsolo.testlibsapp.ble;

import android.bluetooth.BluetoothDevice;

import com.clj.fastble.data.BleDevice;

/**
 * using inheritance to mock a fake BleDevice instance, representing the stored bound
 * bluetooth device.
 *
 * using polymorphism and the override technique to intercept the getter functions, which will
 * be invoked in DeviceAdapter while generating each listview item through BleDevice instance.
 */
/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class BleRepr extends BleDevice {
    String mac;
    String name;
    int rssi;


    public BleRepr(String mac, String name, int rssi) {
        super((BluetoothDevice) null); // just mock a bluetooth device.
        this.mac = mac;
        this.name = name;
        this.rssi = rssi;
    }

    @Override
    public String getMac() {
        return mac;
    }

    @Override
    public int getRssi() {
        return rssi;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getKey() {
        return name + mac;
    }
}
