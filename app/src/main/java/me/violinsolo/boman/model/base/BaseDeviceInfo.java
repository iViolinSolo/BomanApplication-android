package me.violinsolo.boman.model.base;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/6/8 6:53 PM
 * @updateAt 2020/6/8 6:53 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public abstract class BaseDeviceInfo {

    public enum DeviceType {
        THERMOMETER((byte)0x01), UNKNOWN((byte)0xFF);

        private byte typeCode;

        DeviceType(byte typeCode) {
            this.typeCode = typeCode;
        }

        public byte getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(byte typeCode) {
            this.typeCode = typeCode;
        }

        public static DeviceType parse(byte target) {
            for (DeviceType value : DeviceType.values()) {
                if (value.getTypeCode() == target) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum BluetoothVersion {
        BT4_2((byte)0x02), BT5_0((byte)0x10), BT5_1((byte)0x11), BT5_2((byte)0x12);

        private byte typeCode;

        BluetoothVersion(byte typeCode) {
            this.typeCode = typeCode;
        }

        public byte getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(byte typeCode) {
            this.typeCode = typeCode;
        }

        public static BluetoothVersion parse(byte target) {
            for (BluetoothVersion value : BluetoothVersion.values()) {
                if (value.getTypeCode() == target)
                    return value;
            }
            return null;
        }
    }
}
