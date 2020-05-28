package me.violinsolo.boman.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;

import androidx.appcompat.app.AlertDialog;
import me.violinsolo.boman.R;

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

    public static void showGoLocationSettingDialog(Context mContext,
                                                   DialogInterface.OnClickListener cancelCallback,
                                                   DialogInterface.OnClickListener permitCallback) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.notifyTitle)
                .setMessage(R.string.gpsNotifyMsg)
                .setNegativeButton(R.string.cancel, cancelCallback)
                .setPositiveButton(R.string.setting, permitCallback)
                .setCancelable(false)
                .show();
    }
}
