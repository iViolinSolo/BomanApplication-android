package me.violinsolo.boman.util;

import java.util.ArrayList;
import java.util.UUID;

public class Config {


    public static final ArrayList<String> deviceNames = new ArrayList<>();


    public static final UUID ota_version = UUID.fromString("");


    static {
        deviceNames.add("thunder");
    }
}
