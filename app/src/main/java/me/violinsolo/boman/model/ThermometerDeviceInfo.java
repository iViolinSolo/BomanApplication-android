package me.violinsolo.boman.model;

import me.violinsolo.boman.model.base.BaseDeviceInfo;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/6/2 10:36 PM
 * @updateAt 2020/6/2 10:36 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class ThermometerDeviceInfo extends BaseDeviceInfo {
    private String mac; //MAC地址 6个字节
    private String version; //下位机程序版本号ASCII码翻译 5个字节
    private DeviceType deviceType; //设备类型：0x01为额温计 1个字节
    private BluetoothVersion bluetoothType; //蓝牙协议类型：0x02为4.2   0x10为5.0   0x11为5.1   0x12为5.2   1个字节
    private TemperatureUnit temperatureUnit; //摄氏度华氏度设置 1个字节
    private MeasuringMode surfaceOrBody; //表面温度人体温度设置 1个字节
    private DeviceMode mode; //模式设置 1个字节
    private short syncDataSize; //所需同步数据数量 大端 2个字节
    private byte[] reservedArea; //保留字节12个

    public ThermometerDeviceInfo(String mac, String version, DeviceType deviceType, BluetoothVersion bluetoothType, TemperatureUnit temperatureUnit, MeasuringMode surfaceOrBody, DeviceMode mode, short syncDataSize, byte[] reservedArea) {
        this.mac = mac;
        this.version = version;
        this.deviceType = deviceType;
        this.bluetoothType = bluetoothType;
        this.temperatureUnit = temperatureUnit;
        this.surfaceOrBody = surfaceOrBody;
        this.mode = mode;
        this.syncDataSize = syncDataSize;
        this.reservedArea = reservedArea;
    }

    public enum TemperatureUnit {
        CELSIUS((byte)0x01), FAHRENHEIT((byte)0x02);

        private byte typeCode;

        TemperatureUnit(byte typeCode) {
            this.typeCode = typeCode;
        }

        public byte getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(byte typeCode) {
            this.typeCode = typeCode;
        }

        public static TemperatureUnit parse(byte target) {
            for (TemperatureUnit value : TemperatureUnit.values()) {
                if (value.getTypeCode() == target)
                    return value;
            }

            return null;
        }
    }

    public enum MeasuringMode {
        BODY_MODE((byte)0x01), SURFACE_MODE((byte)0x02);

        private byte typeCode;

        MeasuringMode(byte typeCode) {
            this.typeCode = typeCode;
        }

        public byte getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(byte typeCode) {
            this.typeCode = typeCode;
        }

        public static MeasuringMode parse(byte target) {
            for (MeasuringMode value : MeasuringMode.values()) {
                if (value.getTypeCode() == target)
                    return value;
            }

            return null;
        }
    }

    public enum DeviceMode {
        NORMAL_TEMPERATURE_MEASUREMENT_MODE((byte)0x01),
        FACTORY_MODE((byte)0x02),
        CONTINUOUSLY_MEASURING_MODE((byte)0x02);

        private byte typeCode;

        DeviceMode(byte typeCode) {
            this.typeCode = typeCode;
        }

        public byte getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(byte typeCode) {
            this.typeCode = typeCode;
        }

        public static DeviceMode parse(byte target) {
            for (DeviceMode value : DeviceMode.values()) {
                if (value.getTypeCode() == target)
                    return value;
            }

            return null;
        }
    }
}
