package me.violinsolo.boman.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.violinsolo.boman.R;
import me.violinsolo.boman.activity.prerequisite.bt.LocationCheckActivity;
import me.violinsolo.boman.activity.prerequisite.bt.RadarActivity;
import me.violinsolo.boman.activity.prerequisite.login.LoginPortalActivity;
import me.violinsolo.boman.adapter.DeviceBoundAdapater;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityMainBinding;
import me.violinsolo.boman.listener.OnRecyclerViewItemClickListener;
import me.violinsolo.boman.model.BleBoundDevice;
import me.violinsolo.boman.subscribe.Observer;
import me.violinsolo.boman.subscribe.ObserverManager;
import me.violinsolo.boman.util.Intermediate;
import me.violinsolo.boman.util.SharedPrefUtils;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> implements Observer {
    private Context mContext;
    private static final String TAG = MainActivity.class.getSimpleName();

    private BleBoundDevice boundBleDevice = null; // this reference can only be null when initialize it or delete the boundary.
    private SharedPrefUtils spUtil; // = new SharedPrefUtils(mContext); // nullpointerexception.

    private DeviceBoundAdapater mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "mainactivity.ondestroy");

        ObserverManager.getInstance().deleteObserver(this);

        if (boundBleDevice!=null) {
            boundBleDevice.setConnected(false);
            spUtil.storeBoundDeviceV2(boundBleDevice);
        }
        if (Intermediate.getInstance().statusIsScanning) {
            BleManager.getInstance().cancelScan(); // TODO the npe will be triggered when the Manager is not scanning
            Intermediate.getInstance().statusIsScanning = false;
        }
        if (BleManager.getInstance().getAllConnectedDevice().size()>0){
            BleManager.getInstance().disconnectAllDevice();
        }
        BleManager.getInstance().destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        // TODO: need refactor, show all bound devices and the right status.
//        List<BleDevice> allConnectedDevice= bleUtils.showConnectedDevice();
//        deviceAdapter.clearConnectedDevice();
//        for (BleDevice bleDevice : allConnectedDevice) {
//            deviceAdapter.addDevice(bleDevice, BLEUtils.BLEState.BOUND_CONNECTED);
//        }
//        deviceAdapter.notifyDataSetChanged();


        boundBleDevice = spUtil.getBoundDeviceV2();
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
        spUtil = new SharedPrefUtils(mContext);

        ObserverManager.getInstance().addObserver(this); // register the observer, triggered in RadarActivity.
    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {

        // set layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mBinder.rvListDevices.setLayoutManager(mLayoutManager);
        // set adapter
        mAdapter = new DeviceBoundAdapater(mContext);
        mBinder.rvListDevices.setAdapter(mAdapter);

//        bleUtils = new BLEUtils();
//        bleUtils.init(getApplication());

        // reset the UI, simultaneously get the MAC address if possible.
        boundBleDevice = spUtil.getBoundDeviceV2();
        if (boundBleDevice == null) {
            // no bound device
            viewWhenNoBLE();
        }else {
            viewWhenBindBLE();

            // TODO in the future version, change the loading bound devices via for loop.
            mAdapter.addDevice(boundBleDevice);
        }
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        // =====================================
        // Listeners when we do not have a device yet.
        // =====================================
        mBinder.btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LocationCheckActivity.class));
            }
        });

        // =====================================
        // Listeners when we have a bound device.
        // =====================================
        mBinder.disconnectBleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boundBleDevice!=null) {
                    List<BleDevice> connectedDevices = BleManager.getInstance().getAllConnectedDevice();

                    for (BleDevice device :
                            connectedDevices) {
                        if (boundBleDevice.getKey().equals(device.getKey())) {
                            if (BleManager.getInstance().isConnected(device)) {
                                BleManager.getInstance().disconnect(device);
                            }

                            break;
                        }
                    }

                    // fix bug, we can still delete the device even though we can not reach the device.
                    mAdapter.removeDevice(boundBleDevice);

                    boundBleDevice = null; // delete the bound device and remove the reference.
                    spUtil.removeBoundDeviceV2();

                    viewWhenNoBLE();
                }
            }
        });

        mBinder.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginPortalActivity.class));
            }
        });

        mAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (boundBleDevice!= null) {
                    Intent intent = new Intent(mContext, LocationCheckActivity.class);
                    intent.putExtra(RadarActivity.EXTRA_DATA_MAC, boundBleDevice.getMac());
                    startActivity(intent);
                }else {
                    throw new RuntimeException("[Fatal Error] you can not reach here.");
                }
            }
        });

    }


//    private void nowScanAllAvailableDevices() {
//        return;
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
//    }


//    private static final int REQUEST_CODE_OPEN_GPS = 1;
//    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
//
//    @Override
//    public final void onRequestPermissionsResult(int requestCode,
//                                                 @NonNull String[] permissions,
//                                                 @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_CODE_PERMISSION_LOCATION:
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < grantResults.length; i++) {
//                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                            onPermissionGranted(permissions[i]);
//                        }
//                    }
//                }
//                break;
//        }
//    }

//    private void checkPermissions() {
//        if (!checkBluetoothIsOpen()) {
//            Log.e(TAG, "> Bluetooth not open.");
//            Toast.makeText(mContext, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
//        List<String> permissionDeniedList = new ArrayList<>();
//        for (String permission : permissions) {
//            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
//            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "> Manifest.permission.ACCESS_FINE_LOCATION => PackageManager.PERMISSION_GRANTED");
//                onPermissionGranted(permission);
//            } else {
//                Log.e(TAG, "> Manifest.permission.ACCESS_FINE_LOCATION NOT PackageManager.PERMISSION_GRANTED");
//                permissionDeniedList.add(permission);
//            }
//        }
//        if (!permissionDeniedList.isEmpty()) {
//            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
//            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
//        }
//    }
//
//    private void onPermissionGranted(String permission) {
//        switch (permission) {
//            case Manifest.permission.ACCESS_FINE_LOCATION:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
//                    new AlertDialog.Builder(this)
//                            .setTitle(R.string.notifyTitle)
//                            .setMessage(R.string.gpsNotifyMsg)
//                            .setNegativeButton(R.string.cancel,
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            finish();
//                                        }
//                                    })
//                            .setPositiveButton(R.string.setting,
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
//                                        }
//                                    })
//
//                            .setCancelable(false)
//                            .show();
//                } else {
////                    TODO: Finish here....
////                    setScanRule();
////                    startScan();
//                    nowScanAllAvailableDevices();
//                }
//                break;
//        }
//    }

//    private boolean checkGPSIsOpen() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager == null)
//            return false;
//        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
//    }
//
//    private boolean checkBluetoothIsOpen() {
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        return bluetoothAdapter.isEnabled();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_OPEN_GPS) {
//            if (checkGPSIsOpen()) {
////                    TODO: Finish here....
////                setScanRule();
////                startScan();
//                nowScanAllAvailableDevices();
//            }
//        }
//    }


    private void autoShowUIAndConnIfPossible() {
        // reset the UI, simultaneously get the MAC address if possible.
        boundBleDevice = spUtil.getBoundDeviceV2();
        if (boundBleDevice == null) {
            // no bound device
            viewWhenNoBLE();
        }else {
            viewWhenBindBLE();
            // TODO in the future version, change the loading bound devices via for loop.
            mAdapter.addDevice(boundBleDevice);

            //TODO auto connect in silence mode.
        }
    }


    private void viewWhenNoBLE() {
//        mBinder.autoConnectBleBtn.setVisibility(View.GONE);
        mBinder.disconnectBleBtn.setVisibility(View.GONE);
        setStatusBarWhenNoBound();

        mBinder.flNewRoot.setVisibility(View.VISIBLE);
        mBinder.flBoundRoot.setVisibility(View.GONE);
    }

    private void viewWhenBindBLE() {
//        mBinder.autoConnectBleBtn.setVisibility(View.VISIBLE);
        mBinder.disconnectBleBtn.setVisibility(View.VISIBLE);
        setStatusBarWhenHBound();

        mBinder.flNewRoot.setVisibility(View.GONE);
        mBinder.flBoundRoot.setVisibility(View.VISIBLE);
    }

    private void setStatusBarWhenNoBound() {
        StatusBarUtil.setTransparent(MainActivity.this);
//        mBinder.toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mBinder.getRoot().setBackground(getDrawable(R.drawable.main_background));
        StatusBarUtil.setLightMode(MainActivity.this);

    }

    private void setStatusBarWhenHBound() {
        StatusBarUtil.setTransparent(MainActivity.this);
//        mBinder.toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mBinder.getRoot().setBackground(null);
        StatusBarUtil.setLightMode(MainActivity.this);

    }

    @Override
    public void onBLEDisconneted(BleDevice bleDevice) {
        if (boundBleDevice != null) {
            boundBleDevice.setConnected(false);
            spUtil.storeBoundDeviceV2(boundBleDevice);
            mAdapter.addDevice(boundBleDevice);
        }
    }

    @Override
    public void onBLEConneted(BleDevice bleDevice) {
        if (boundBleDevice == null) {
            boundBleDevice = new BleBoundDevice(bleDevice);
//            boundBleDevice.setConnected(true);
//            spUtil.storeBoundDeviceV2(boundBleDevice);
//            mAdapter.addDevice(boundBleDevice);
        }
        // TODO need to check this part in the future if you want to bind and connect different kinds of devices.
        // TODO since I have combine the two types of connections callbacks which are triggered in connect device through mac and connect
        // TODO device after sacnning, this observer/observable part will be trigger in both types, so CHECK before use. Also see in RadarActivity
//        else { // Since we will trigger this notification in the bound-device-connection stage, so it will always be triggered.
//            Log.e(TAG, "[Fatal Error] you can not reach here.");
//            throw new RuntimeException("[Fatal Error] you can never bind a device after you have bound it and before you remove it.");
//        }

        boundBleDevice.setConnected(true);
        spUtil.storeBoundDeviceV2(boundBleDevice);
        mAdapter.addDevice(boundBleDevice);
    }
}
