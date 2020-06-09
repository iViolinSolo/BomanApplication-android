package me.violinsolo.boman.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import me.violinsolo.boman.R;
import me.violinsolo.boman.model.ThermometerDeviceInfo;
import permissions.dispatcher.PermissionRequest;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/28 5:16 PM
 * @updateAt 2020/5/28 5:16 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class BluetoothUtil {

    public static boolean checkGPSIsOpen(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);

    }


    public static boolean checkBluetoothIsOpen() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            return false;
        return bluetoothAdapter.isEnabled();
    }

    public static AlertDialog showGoLocationSettingDialog(Context mContext,
                                                   DialogInterface.OnClickListener cancelCallback,
                                                   DialogInterface.OnClickListener permitCallback) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.notifyTitle)
                .setMessage(R.string.gpsNotifyMsg)
                .setNegativeButton(R.string.cancel, cancelCallback)
                .setPositiveButton(R.string.go_gps_setting, permitCallback)
                .setCancelable(false)
                .show();
        return dialog;
    }

    /**
     * 弹出请求提示框之前的提示框
     * @param messageResId
     * @param request
     */
    public static AlertDialog showRationaleDialog(Context mContext,
                                           @StringRes int messageResId,
                                           final PermissionRequest request) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
        return dialog;
    }

    /**
     * 打开设置界面
     */
    public static AlertDialog showOpenSettingDialog(final Context context, @StringRes int messageResId){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setPositiveButton(R.string.open_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        IntentUtil.startAppSettings(context);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
        return dialog;
    }


    /**
     * =====================================================
     *
     * The following codes are codes for fetch and register tunnel in GATT
     *
     * =====================================================
     */

    public static final String service_1 = "EDFEC62E-9910-0BAC-5241-D8BDA6932A30";

    // 属性：WRITE & Notify    信息返回值通过Notify发回
    public static final String characteristic_Control_Point = "2D86686A-53DC-25B3-0C4A-F0E10C8DEE3F";
    // 属性：NOTIFY            额温计测温推送
    public static final String characteristic_Thermometry_Notify = "6c12cd67-35dc-fc91-654c-bcf099b89815";
    // 属性：READ              状态查看
    public static final String characteristic_Read_State = "4508e952-1aa8-fc93-0656-83b455d91e1e";
    // 属性：READ              温度补偿查看
    public static final String characteristic_Read_Temperature_Offset = "4b790e18-7f67-34a1-6441-f45c7ea74e1f";


    public static void handle_Control_Point(BleDevice bleDevice, int what) {
        if (BleManager.getInstance().isConnected(bleDevice)) {

            BleManager.getInstance().write(bleDevice,
                    service_1,
                    characteristic_Control_Point,
                    new byte[]{},
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
            BleManager.getInstance().notify(bleDevice,
                    service_1,
                    characteristic_Control_Point,
                    new BleNotifyCallback() {
                        @Override
                        public void onNotifySuccess() {

                        }

                        @Override
                        public void onNotifyFailure(BleException exception) {

                        }

                        @Override
                        public void onCharacteristicChanged(byte[] data) {

                        }
                    });

        }else {
            throw new RuntimeException(bleDevice.getName() + ":"+bleDevice.getMac()+" is not connected...");
        }
    }

    public static void handle_Thermometer_Notify(BleDevice bleDevice) {
        if (BleManager.getInstance().isConnected(bleDevice)) {

            BleManager.getInstance().notify(bleDevice,
                    service_1,
                    characteristic_Thermometry_Notify,
                    new BleNotifyCallback() {
                        @Override
                        public void onNotifySuccess() {

                        }

                        @Override
                        public void onNotifyFailure(BleException exception) {

                        }

                        @Override
                        public void onCharacteristicChanged(byte[] data) {

                        }
                    });

        }else {
            throw new RuntimeException(bleDevice.getName() + ":"+bleDevice.getMac()+" is not connected...");
        }
    }

    public static void handle_Read_State(BleDevice bleDevice) {
        if (BleManager.getInstance().isConnected(bleDevice)) {

            BleManager.getInstance().read(bleDevice,
                    service_1,
                    characteristic_Read_State,
                    new BleReadCallback() {
                        @Override
                        public void onReadSuccess(byte[] data) {

                        }

                        @Override
                        public void onReadFailure(BleException exception) {

                        }
                    });

        }else {
            throw new RuntimeException(bleDevice.getName() + ":"+bleDevice.getMac()+" is not connected...");
        }
    }

    public static void handle_Read_Temperature_Offset(BleDevice bleDevice) {
        if (BleManager.getInstance().isConnected(bleDevice)) {

            BleManager.getInstance().read(bleDevice,
                    service_1,
                    characteristic_Read_Temperature_Offset,
                    new BleReadCallback() {
                        @Override
                        public void onReadSuccess(byte[] data) {

                        }

                        @Override
                        public void onReadFailure(BleException exception) {

                        }
                    });

        }else {
            throw new RuntimeException(bleDevice.getName() + ":"+bleDevice.getMac()+" is not connected...");
        }
    }


    public static final String service_2 = "1DFEC62E-2910-0BAC-5241-D8BDA6932A10";
    // 属性：WRITE & Notify    信息返回值通过Notify发回; 同步文件控制
    public static final String characteristic_Synchronize_Control = "26a31a1b-420c-d987-b441-c4c57b33b414";
    // 属性：Notify             同步文件内容
    public static final String characteristic_Synchronize_Content = "11a31a33-420c-d987-b441-c4c57b33b477";


    /**
     * =====================================================
     *
     * The following codes are used to be Helper functions which can have some positive effects.
     *
     * =====================================================
     */

    public static final byte notifySuccess = (byte) 0xB5;

    public static final byte controlPointHeader = (byte) 0xA5;
    public static final byte controlPointSetTime = (byte) 0x01;
    public static final byte controlPointReadTime = (byte) 0x02;
    public static final byte controlPointReadSOC = (byte) 0x03; // battery remain level
    public static final byte controlPointSetTemperatureUnit = (byte) 0x04;
    public static final byte celsiusMode = (byte) 0x01;
    public static final byte fahrenheitMode = (byte) 0x02;
    public static final byte controlPointReadEnvironmentTemperature = (byte) 0x05;
    public static final byte controlPointReadTemperature = (byte) 0x06;
    public static final byte controlPointSetTemperatureOffset= (byte) 0x07;
    public static final byte controlPointSetTemperatureMode= (byte) 0xF1;
    public static final byte bodyTemperatureMode = (byte) 0x01;
    public static final byte surfaceTemperatureMode = (byte) 0x02;
    public static final byte controlPointSetDeviceTestMode= (byte) 0xF2;
    public static final byte normalTemperatureMeasurementMode = (byte) 0x01;
    public static final byte factoryMode = (byte) 0x02;
    public static final byte continuouslyMeasuringMode = (byte) 0x03;
    public static final byte controlPointSetTempOffset_16_35_BlackBody = (byte) 0xA1;
    public static final byte controlPointSetTempOffset_16_37_BlackBody = (byte) 0xA2;
    public static final byte controlPointSetTempOffset_16_39_BlackBody = (byte) 0xA3;
    public static final byte controlPointSetTempOffset_25_35_BlackBody = (byte) 0xB1;
    public static final byte controlPointSetTempOffset_25_37_BlackBody = (byte) 0xB2;
    public static final byte controlPointSetTempOffset_25_39_BlackBody = (byte) 0xB3;
    public static final byte controlPointSetTempOffset_39_35_BlackBody = (byte) 0xC1;
    public static final byte controlPointSetTempOffset_39_37_BlackBody = (byte) 0xC2;
    public static final byte controlPointSetTempOffset_39_39_BlackBody = (byte) 0xC3;
    public static final byte controlPointSetTempOffset_42_35_BlackBody = (byte) 0xD1;
    public static final byte controlPointSetTempOffset_42_37_BlackBody = (byte) 0xD2;
    public static final byte controlPointSetTempOffset_42_39_BlackBody = (byte) 0xD3;
//    public static final byte = (byte) 0x;
//    public static final byte = (byte) 0x;
//    public static final byte = (byte) 0x;
//    public static final byte = (byte) 0x;
//    public static final byte = (byte) 0x;
//    public static final byte = (byte) 0x;
//    public static final byte = (byte) 0x;
//    public static final byte = (byte) 0x;


    // ===============================================================================
    // Control Point helper codes...
    // ===============================================================================

    // called in 0xA501
    public static byte[] genCurrentTimeData() {
        long milliseconds = DateUtil.getCurrentTimestamp();
        int[] timeArr = DateUtil.castMillis2IntArray(milliseconds, null);

        byte[] data = new byte[7];
        data[0] = (byte) ((timeArr[0] >> 8) & 0xFF);  // 大端，高位在前，低位在后
        data[1] = (byte) (timeArr[0] & 0xFF);
        data[2] = (byte) (timeArr[1] & 0xFF);
        data[3] = (byte) (timeArr[2] & 0xFF);
        data[4] = (byte) (timeArr[3] & 0xFF);
        data[5] = (byte) (timeArr[4] & 0xFF);
        data[6] = (byte) (timeArr[5] & 0xFF);

        return data;
    }

    // called in 0xA502
    // assert data.length == 6,
    // the first byte in this array in NOTIFY has been engulfed so data array has a length 6.
    public static long parseCurrentTimeData(byte[] data) {
        int[] timeArr = new int[6];

        // remember the priority of << is higher than &, using () to specify the right calling order...
        int year = ((data[0] & 0xFF) << 8) + (data[1] & 0xFF);  // 大端，高位在前，低位在后
        timeArr[0] = year;
        timeArr[1] = data[2] & 0xFF;
        timeArr[2] = data[3] & 0xFF;
        timeArr[3] = data[4] & 0xFF;
        timeArr[4] = data[5] & 0xFF;
        timeArr[5] = data[6] & 0xFF;

        long milliseconds = DateUtil.castIntArray2Millis(timeArr, null);

        return milliseconds;
    }

    // called in 0xA503
    // the first byte in this array in NOTIFY has been engulfed so data array has a length 1,
    // which will only pass the ONLY byte into this function.
    public static int parseCurrentSOCValue(byte data) {
        int socVal = (data & 0xFF);

        return socVal;
    }

    // called in 0xA504
    public static byte genCurrentTemperatureUnit() {
        return Intermediate.getInstance().isCelsius? celsiusMode: fahrenheitMode;
    }


    // called in 0xA505
    // assert data.length == 7,
    public static float parseCurrentEnvironmentTemperature(byte[] data) {
        byte[] envTemp = new byte[]{data[0], data[1]};

        short envTemperature10times = HexUtil.toShort(envTemp);

        return envTemperature10times / 10f;
    }


    // called in 0xA506
    public static short parseCurrentTemperature(byte[] data) {
        byte[] bodyTemp = new byte[]{data[6], data[7]};

        byte[] surfaceTemp = new byte[]{data[0], data[1]};

        // body temperature ...
        short bodyTemperature10times = HexUtil.toShort(bodyTemp);
        short surfaceTemperature10times = HexUtil.toShort(surfaceTemp);

        return bodyTemperature10times;
    }

    // called in 0xA507
    public static byte[] genCurrentTemperatureOffset(short tempOffset) {
//        byte[] data = new byte[2];
//        data[0] = (byte) ((tempOffset >> 8) & 0xFF);  // 大端，高位在前，低位在后
//        data[1] = (byte) (tempOffset & 0xFF);
//
//        return data;
        return parseShortToByteArr(tempOffset);
    }

    // called in 0xA5F1
    // should be invoked every time when you connect the thermometer.
    public static byte genCurrentTemperatureMode() {
        return Intermediate.getInstance().isBodyTempMode? bodyTemperatureMode: surfaceTemperatureMode;
    }

    // called in 0xA5F2
    public static byte genCurrentDeviceTestMode() {
        byte result;
        Intermediate.DeviceTestMode mode = Intermediate.getInstance().currentDeviceMode;

        switch (mode) {
            case FACTORY_MODE:
                result = factoryMode;
                break;
            case CONTINUOUSLY_MEASURING_MODE:
                result = continuouslyMeasuringMode;
                break;
            case NORMAL_TEMPERATURE_MEASUREMENT_MODE:
            default:
                result = normalTemperatureMeasurementMode;
                break;
        }

        return result;
    }

    // TEST CALLED CODES...
    private static byte[] parseShortToByteArr(short val) {
        byte[] data = new byte[2];
        data[0] = (byte) ((val >> 8) & 0xFF);  // 大端，高位在前，低位在后
        data[1] = (byte) (val & 0xFF);

        return data;
    }

    // called in 0xA5A1
    public static byte[] genCurrentTest_16_35_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5A2
    public static byte[] genCurrentTest_16_37_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5A3
    public static byte[] genCurrentTest_16_39_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5B1
    public static byte[] genCurrentTest_25_35_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5B2
    public static byte[] genCurrentTest_25_37_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5B3
    public static byte[] genCurrentTest_25_39_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5C1
    public static byte[] genCurrentTest_39_35_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5C2
    public static byte[] genCurrentTest_39_37_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5C3
    public static byte[] genCurrentTest_39_39_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5D1
    public static byte[] genCurrentTest_42_35_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5D2
    public static byte[] genCurrentTest_42_37_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }
    // called in 0xA5D3
    public static byte[] genCurrentTest_42_39_BlackBodyTempOffset(short tempOffset) {
        return parseShortToByteArr(tempOffset);
    }



    // ===============================================================================
    // Thermometry Notify helper codes...
    // ===============================================================================

    public static short parsePassiveTemperature(byte[] data) {
        byte[] bodyTemp = new byte[]{data[6], data[7]};

        byte[] surfaceTemp = new byte[]{data[0], data[1]};

        // body temperature ...
        short bodyTemperature10times = HexUtil.toShort(bodyTemp);
        short surfaceTemperature10times = HexUtil.toShort(surfaceTemp);

        return bodyTemperature10times;
    }

    // ===============================================================================
    // Read State helper codes...
    // ===============================================================================

    public static ThermometerDeviceInfo parseDeviceState(byte[] data) {
        // mac
        byte[] macArr = new byte[]{data[0], data[1], data[2], data[3], data[4], data[5]};
        // device version ascii
        byte[] version = new byte[]{data[6], data[7], data[8], data[9], data[10]};


        return null;
    }
}
