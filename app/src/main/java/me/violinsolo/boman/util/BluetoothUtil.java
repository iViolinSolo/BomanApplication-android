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
}
