package me.violinsolo.boman.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;

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
                .setPositiveButton(R.string.setting, permitCallback)
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
}
