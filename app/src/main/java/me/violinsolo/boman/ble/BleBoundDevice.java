package me.violinsolo.boman.ble;

import com.clj.fastble.data.BleDevice;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/27 10:49 AM
 * @updateAt 2020/5/27 10:49 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class BleBoundDevice {
    public static final int SOC_NO_VALUE = -1;

    String mac;
    String name;
    int socVal;
    boolean connStatus;
    BleDevice bleDevice;


    public BleBoundDevice(BleDevice device) {
        this.bleDevice = device;
        if (device != null) {
            this.mac = device.getMac();
            this.name = device.getName();
        }

        this.socVal = SOC_NO_VALUE;
        this.connStatus = false;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return name + mac;
    }

    public static int getSocNoValue() {
        return SOC_NO_VALUE;
    }

    public int getSocVal() {
        return socVal;
    }

    public boolean isConnStatus() {
        return connStatus;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSocVal(int socVal) {
        this.socVal = socVal;
    }

    public void setConnStatus(boolean connStatus) {
        this.connStatus = connStatus;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }
}