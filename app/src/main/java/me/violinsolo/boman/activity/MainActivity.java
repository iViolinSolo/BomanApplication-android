package me.violinsolo.boman.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.violinsolo.boman.R;
import me.violinsolo.boman.adapter.DeviceAdapter;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.ble.BLEUtils;
import me.violinsolo.boman.ble.ObserverManager;
import me.violinsolo.boman.databinding.ActivityMainBinding;
import me.violinsolo.boman.util.SharedPrefUtils;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private Context mContext = MainActivity.this;
    private static final String TAG = MainActivity.class.getSimpleName();

    private String boundMacAddr = null;
    private SharedPrefUtils spUtil; // = new SharedPrefUtils(mContext); // nullpointerexception.
    private BLEUtils bleUtils;
    private DeviceAdapter deviceAdapter;


    private Animation operatingAnim;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bleUtils.clean();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: need refactor, show all bound devices and the right status.
        List<BleDevice> allConnectedDevice= bleUtils.showConnectedDevice();
        deviceAdapter.clearConnectedDevice();
        for (BleDevice bleDevice : allConnectedDevice) {
            deviceAdapter.addDevice(bleDevice, BLEUtils.BLEState.BOUND_CONNECTED);
        }
        deviceAdapter.notifyDataSetChanged();
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     *                eg.
     *                mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityMainBinding onBind() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {
        setSupportActionBar(mBinder.toolbar);

        spUtil = new SharedPrefUtils(mContext);

        deviceAdapter = new DeviceAdapter(mContext);
        mBinder.listDevices.setAdapter(deviceAdapter);

        bleUtils = new BLEUtils();
        bleUtils.init(getApplication());

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        progressDialog = new ProgressDialog(mContext);

        // reset the UI, simultaneously get the MAC address if possible.
        boundMacAddr = spUtil.getBoundDevice();
        if (boundMacAddr == null) {
            // no bound device
            viewWhenNoBLE();
        }else {
            viewWhenBindBLE();
//            deviceAdapter.ad
        }
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        mBinder.findBleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();

                spUtil.storeBoundDevice("jSA09dclkwe23", "Device name #3ds", 900);
            }
        });

        mBinder.disconnectBleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO disconnect the current device if connected.
                boundMacAddr = null;
                spUtil.removeBoundDevice();
            }
        });

        mBinder.autoConnectBleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });


        bleUtils.setBleConnectCallBack(new BLEUtils.BleConnectCallBack() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                mBinder.imgLoading.clearAnimation();
                mBinder.imgLoading.setVisibility(View.GONE);
//                btn_scan.setText(getString(R.string.start_scan));
                progressDialog.dismiss();
                Toast.makeText(mContext, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                deviceAdapter.addDevice(bleDevice, BLEUtils.BLEState.BOUND_CONNECTED);
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

//                deviceAdapter.removeDevice(bleDevice);
                deviceAdapter.updateDeviceState(bleDevice, BLEUtils.BLEState.BOUND_DISCONNECTED);
                deviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(MainActivity.this, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserver(bleDevice); // TODO, need to check observable functionality.
                }

            }
        });

        bleUtils.setBleScanCallback(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                deviceAdapter.clearScanDevice();
                deviceAdapter.notifyDataSetChanged();
                mBinder.imgLoading.startAnimation(operatingAnim);
                mBinder.imgLoading.setVisibility(View.VISIBLE);
//                btn_scan.setText(getString(R.string.stop_scan));
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                deviceAdapter.addDevice(bleDevice, BLEUtils.BLEState.UNBOUND);
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                mBinder.imgLoading.clearAnimation();
                mBinder.imgLoading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));
            }
        });
    }


    private void viewWhenNoBLE() {
        mBinder.findBleBtn.setVisibility(View.VISIBLE);
        mBinder.autoConnectBleBtn.setVisibility(View.GONE);
        mBinder.disconnectBleBtn.setVisibility(View.GONE);
    }

    private void viewWhenBindBLE() {
        mBinder.findBleBtn.setVisibility(View.GONE);
        mBinder.autoConnectBleBtn.setVisibility(View.VISIBLE);
        mBinder.disconnectBleBtn.setVisibility(View.VISIBLE);
    }

    private void nowScanAllAvailableDevices() {
//        bleUtils.setScanRule(null);
        bleUtils.startScan();
    }


    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        if (!checkBluetoothIsOpen()) {
            Log.e(TAG, "> Bluetooth not open.");
            Toast.makeText(mContext, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "> Manifest.permission.ACCESS_FINE_LOCATION => PackageManager.PERMISSION_GRANTED");
                onPermissionGranted(permission);
            } else {
                Log.e(TAG, "> Manifest.permission.ACCESS_FINE_LOCATION NOT PackageManager.PERMISSION_GRANTED");
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
//                    TODO: Finish here....
//                    setScanRule();
//                    startScan();
                    nowScanAllAvailableDevices();
                }
                break;
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    private boolean checkBluetoothIsOpen() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
//                    TODO: Finish here....
//                setScanRule();
//                startScan();
                nowScanAllAvailableDevices();
            }
        }
    }

}
