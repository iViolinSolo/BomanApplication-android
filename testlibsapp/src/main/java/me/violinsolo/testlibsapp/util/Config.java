package me.violinsolo.testlibsapp.util;

import java.util.ArrayList;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class Config {


    public static final ArrayList<String> deviceNames = new ArrayList<>();


//    public static final UUID ota_version = UUID.fromString("");


    static {
        deviceNames.add("Thunderboard");
    }

    // if the temperature value is bigger than temperatureThreshold, it demonstrates "Temperature High!"
    public static final int temperatureThreshold = 3750;
}
