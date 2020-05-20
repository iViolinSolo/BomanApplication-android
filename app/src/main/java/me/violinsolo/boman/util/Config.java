package me.violinsolo.boman.util;

import java.util.ArrayList;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 */
public class Config {


    public static final ArrayList<String> deviceNames = new ArrayList<>();


//    public static final UUID ota_version = UUID.fromString("");


    static {
        deviceNames.add("Thunderboard");
    }
}
