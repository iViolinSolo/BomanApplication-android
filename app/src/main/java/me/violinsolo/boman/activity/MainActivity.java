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
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.violinsolo.boman.R;
import me.violinsolo.boman.activity.prerequisite.bt.LocationCheckActivity;
import me.violinsolo.boman.activity.prerequisite.login.LoginPortalActivity;
import me.violinsolo.boman.adapter.DeviceAdapter;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.ble.BLEUtils;
import me.violinsolo.boman.ble.BleRepr;
import me.violinsolo.boman.databinding.ActivityMainBinding;
import me.violinsolo.boman.subscribe.ObserverManager;
import me.violinsolo.boman.util.SharedPrefUtils;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private Context mContext;
    private static final String TAG = MainActivity.class.getSimpleName();

    private BleRepr boundBleDevice = null;
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


        boundBleDevice = spUtil.getBoundDevice();
        if (boundBleDevice==null) {
            viewWhenNoBLE();
        }else {
            viewWhenBindBLE();
        }
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
     * init data here, like you can get data from extra.
     * eg.
     * var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     */
    @Override
    protected void initData() {
        mContext = MainActivity.this;
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
//        bleUtils.init(getApplication());

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        progressDialog = new ProgressDialog(mContext);

        // reset the UI, simultaneously get the MAC address if possible.
        boundBleDevice = spUtil.getBoundDevice();
        if (boundBleDevice == null) {
            // no bound device
            viewWhenNoBLE();
        }else {
            viewWhenBindBLE();
            deviceAdapter.addDevice(boundBleDevice, BLEUtils.BLEState.BOUND_DISCONNECTED);
            deviceAdapter.notifyDataSetChanged();

            checkPermissions();
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

//                spUtil.storeBoundDevice("jSA09dclkwe23", "Device name #3ds", 900);
            }
        });

        mBinder.disconnectBleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boundBleDevice!=null) {
                    BleDevice t = bleUtils.getCurrentConnectedDevice();
                    if (t!=null) {
                        bleUtils.disconnect(t);
                        deviceAdapter.removeDevice(t);
                        deviceAdapter.notifyDataSetChanged();
                    }

                    // TODO disconnect the current device if connected.
                    boundBleDevice = null;
                    spUtil.removeBoundDevice();

                    viewWhenNoBLE();
                }
            }
        });

        mBinder.autoConnectBleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                checkPermissions();
                // TODO need check permission before use it.
                // TODO now just try to get all permissions.

                if (bleUtils.getCurrentConnectedDevice()!=null && bleUtils.isConnected(bleUtils.getCurrentConnectedDevice())) {
                    Intent intent = new Intent(mContext, DetailsActivity.class);

                    intent.putExtra(DetailsActivity.EXTRA_DATA_BLE, bleUtils.getCurrentConnectedDevice());

                    startActivity(intent);
                }
            }
        });

        mBinder.checkLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LocationCheckActivity.class));
            }
        });

        mBinder.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginPortalActivity.class));
            }
        });

        deviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                bleUtils.connect(bleDevice);
                spUtil.storeBoundDevice(bleDevice);
            }

            @Override
            public void onDisConnect(final BleDevice bleDevice) {
                bleUtils.disconnect(bleDevice);
            }

            @Override
            public void onDetail(BleDevice bleDevice) {
//                if (BleManager.getInstance().isConnected(bleDevice)) {
//                    Intent intent = new Intent(MainActivity.this, OperationActivity.class);
//                    intent.putExtra(OperationActivity.KEY_DATA, bleDevice);
//                    startActivity(intent);
//                }
//                TODO need implement.
                Toast.makeText(mContext, "Not available now", Toast.LENGTH_LONG).show();
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
                bleUtils.setCurrentConnectedDevice(null);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                deviceAdapter.addDevice(bleDevice, BLEUtils.BLEState.BOUND_CONNECTED);
                deviceAdapter.notifyDataSetChanged();

                bleUtils.setCurrentConnectedDevice(bleDevice);
                spUtil.storeBoundDevice(bleDevice);

                viewWhenBindBLE();
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


        // ===========================================
        mBinder.btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LocationCheckActivity.class));
            }
        });
    }


    private void viewWhenNoBLE() {
        mBinder.findBleBtn.setVisibility(View.VISIBLE);
        mBinder.autoConnectBleBtn.setVisibility(View.GONE);
        mBinder.disconnectBleBtn.setVisibility(View.GONE);
        setStatusBarWhenNoBound();
    }

    private void viewWhenBindBLE() {
        mBinder.findBleBtn.setVisibility(View.GONE);
        mBinder.autoConnectBleBtn.setVisibility(View.VISIBLE);
        mBinder.disconnectBleBtn.setVisibility(View.VISIBLE);
        setStatusBarWhenHBound();
    }

    private void nowScanAllAvailableDevices() {
        return;
//        if (boundBleDevice == null) {
//            bleUtils.setScanRule(null);
//            bleUtils.startScan();
//        }else {
//            bleUtils.setScanRule(boundBleDevice.getMac());
//            BleManager.getInstance().scan(new BleScanCallback() {
//                @Override
//                public void onScanStarted(boolean success) {
//                    deviceAdapter.clearScanDevice();
//                    deviceAdapter.notifyDataSetChanged();
//                    mBinder.imgLoading.startAnimation(operatingAnim);
//                    mBinder.imgLoading.setVisibility(View.VISIBLE);
////                btn_scan.setText(getString(R.string.stop_scan));
//                }
//
//                @Override
//                public void onLeScan(BleDevice bleDevice) {
//                    super.onLeScan(bleDevice);
//                }
//
//                @Override
//                public void onScanning(BleDevice bleDevice) {
////                    deviceAdapter.addDevice(bleDevice, BLEUtils.BLEState.UNBOUND);
////                    deviceAdapter.notifyDataSetChanged();
//                    bleUtils.connect(bleDevice);
//                }
//
//                @Override
//                public void onScanFinished(List<BleDevice> scanResultList) {
//                    mBinder.imgLoading.clearAnimation();
//                    mBinder.imgLoading.setVisibility(View.INVISIBLE);
////                btn_scan.setText(getString(R.string.start_scan));
//                }
//            });
//
//        }
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



    private void setStatusBarWhenNoBound() {
        StatusBarUtil.setTransparent(MainActivity.this);
        mBinder.toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mBinder.getRoot().setBackground(getDrawable(R.drawable.main_background));
        StatusBarUtil.setLightMode(MainActivity.this);

        mBinder.flNewRoot.setVisibility(View.VISIBLE);
        mBinder.flBoundRoot.setVisibility(View.GONE);
    }

    private void setStatusBarWhenHBound() {
        StatusBarUtil.setTransparent(MainActivity.this);
        mBinder.toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mBinder.getRoot().setBackground(null);
        StatusBarUtil.setLightMode(MainActivity.this);

        mBinder.flNewRoot.setVisibility(View.GONE);
        mBinder.flBoundRoot.setVisibility(View.VISIBLE);
    }

}
