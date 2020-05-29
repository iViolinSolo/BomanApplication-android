package me.violinsolo.boman.util;

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
        deviceNames.add("BM-");
    }

    // if the temperature value is bigger than temperatureThreshold, it demonstrates "Temperature High!"
    public static final int temperatureThreshold = 3750;


    public static final int colorPrimary = 0xFF6200EE;
    public static final int colorPrimaryDark = 0xFF3700B3;
    public static final int colorAccent = 0xFF03DAC5;
}
