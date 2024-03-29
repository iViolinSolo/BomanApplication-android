package me.violinsolo.testlibsapp.model;

import me.violinsolo.testlibsapp.util.DateUtil;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/29 2:16 PM
 * @updateAt 2020/5/29 2:16 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class TemperatureRecord {
    // the 100 times value of a temperature taken by the thermometer, eg. value=3700 means it's 37.00℃
    private int value; // Celsius degree.
    // the timestamp for this record.
    private long timestamp;

    public TemperatureRecord(int value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrentDay() {
        return DateUtil.getDayADV(timestamp, null);
    }

    public String getCurrentSimpleTime() {
        return DateUtil.getHourMinuteADV(timestamp, null);
    }

    @Override
    public String toString() {
        return "TemperatureRecord{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
