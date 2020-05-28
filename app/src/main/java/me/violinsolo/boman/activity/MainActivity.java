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
            BleManager.getInstance().cancelScan(); // the npe will be triggered when the Manager is not scanning
            Intermediate.getInstance().statusIsScanning = false;
        }
        if (Intermediate.getInstance().mBleHolder!=null
                && BleManager.getInstance().isConnected(Intermediate.getInstance().mBleHolder)) {
            BleManager.getInstance().disconnect(Intermediate.getInstance().mBleHolder);
            Intermediate.getInstance().mBleHolder = null;
        }
        if (BleManager.getInstance().getAllConnectedDevice().size()>0){
            BleManager.getInstance().disconnectAllDevice();
        }
        BleManager.getInstance().destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                if (Intermediate.getInstance().mBleHolder!=null
                        && BleManager.getInstance().isConnected(Intermediate.getInstance().mBleHolder)) {

                    // Go to the setting page directly, no need to connect again.
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra(DetailsActivity.EXTRA_DATA_BLE, Intermediate.getInstance().mBleHolder);
                    startActivity(intent);
                }else if (boundBleDevice!= null) {
                    Intent intent = new Intent(mContext, LocationCheckActivity.class);
                    intent.putExtra(RadarActivity.EXTRA_DATA_MAC, boundBleDevice.getMac());
                    startActivity(intent);
                }else {
                    throw new RuntimeException("[Fatal Error] you can not reach here.");
                }
            }
        });

    }


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
        if (Intermediate.getInstance().mBleHolder != null) {
            Intermediate.getInstance().mBleHolder = null;
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
        Intermediate.getInstance().mBleHolder = bleDevice;
    }
}
