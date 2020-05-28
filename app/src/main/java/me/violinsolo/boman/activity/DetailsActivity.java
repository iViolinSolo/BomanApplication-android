package me.violinsolo.boman.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.jaeger.library.StatusBarUtil;

import java.util.UUID;

import me.violinsolo.boman.R;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityDetailsBinding;
import me.violinsolo.boman.subscribe.Observer;
import me.violinsolo.boman.subscribe.ObserverManager;
import me.violinsolo.boman.util.HexUtil;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class DetailsActivity extends BaseActivity<ActivityDetailsBinding> implements Observer {
    private static final String TAG = DetailsActivity.class.getSimpleName();

    public static final String EXTRA_DATA_BLE = "EXTRA_DATA_BLE";
    public Context mContext = DetailsActivity.this;

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;

    private Handler mHandler;
    private Runnable runnableTemperature;
    private Runnable runnableUV;
    private Runnable runnableHumidity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().clearCharacterCallback(bleDevice);
        ObserverManager.getInstance().deleteObserver(this);

        mHandler.removeCallbacks(runnableHumidity);
        mHandler.removeCallbacks(runnableUV);
        mHandler.removeCallbacks(runnableTemperature);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (currentPage != 0) {
//                currentPage--;
//                changePage(currentPage);
//                return true;
//            } else {
//                finish();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityDetailsBinding onBind() {
        return ActivityDetailsBinding.inflate(getLayoutInflater());
    }

    /**
     * init data here, like you can get data from extra.
     * eg.
     * var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     */
    @Override
    protected void initData() {
        bleDevice = getIntent().getParcelableExtra(EXTRA_DATA_BLE);
        if (bleDevice == null || !BleManager.getInstance().isConnected(bleDevice))
            finish();
    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {
        setSupportActionBar(mBinder.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
        StatusBarUtil.setLightMode(DetailsActivity.this);
        mBinder.toolbar.setNavigationIcon(R.mipmap.ic_back);
        mBinder.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ObserverManager.getInstance().addObserver(this);

        mBinder.tvDeviceInfo.setText(String.format("%s - %s", bleDevice.getName(), bleDevice.getMac()));
        mBinder.tvDeviceInfo.setTextColor(0xFF8DA9E4);


        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
//        for (BluetoothGattService service : gatt.getServices()) {
////            service.
//        }
        BluetoothGattService service_env_sensing = gatt.getService(UUID.fromString("0000181A-0000-1000-8000-00805F9B34FB"));
        BluetoothGattCharacteristic characteristic_temperature = service_env_sensing.getCharacteristic(UUID.fromString("00002A6E-0000-1000-8000-00805F9B34FB"));


        mHandler = new Handler();
        runnableTemperature = new Runnable() {
            @Override
            public void run() {
                // temperature
                BleManager.getInstance().read(
                        bleDevice,
//                characteristic_temperature.getUuid().toString(),
//                service_env_sensing.getUuid().toString(),
                        "0000181A-0000-1000-8000-00805F9B34FB",
                        "00002A6E-0000-1000-8000-00805F9B34FB",
                        new BleReadCallback() {

                            @Override
                            public void onReadSuccess(final byte[] data) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String content = "";
                                        boolean addSpace = false;

                                        if (data == null || data.length < 1)
                                            content = "No Data Fetched";
                                        else {
//                                            StringBuilder sb = new StringBuilder();
//
//                                            // 数据原因，所以倒叙读出
//                                            for (int i = 0; i < data.length ; i++) {
//                                                Log.d(TAG, i+" -> "+data[i]);
//                                            }
//
//                                            for (int i = data.length - 1; i >= 0; i--) {
//                                                String hex = Integer.toHexString(data[i] & 0xFF);
//                                                if (hex.length() == 1) {
//                                                    hex = '0' + hex;
//                                                }
//                                                sb.append(hex);
//                                                if (addSpace)
//                                                    sb.append(" ");
//                                            }
//                                            content = sb.toString().trim();
//
//                                            long result = Long.parseLong(content, 16);

                                            long result = HexUtil.lng(data, false);
                                            content+= (" => " + result/100f+" ℃");

                                            Log.d(TAG, "temperature => "+result/100f+" ℃");
                                        }
//                                String plainHexData = HexUtil.formatHexString(data, true);

                                        mBinder.tvTemperature.setText(content);
                                    }
                                });
                            }

                            @Override
                            public void onReadFailure(final BleException exception) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinder.tvTemperature.setText(exception.toString());
                                    }
                                });
                            }
                        });
                mHandler.postDelayed(this, 1000);
            }
        };

        runnableUV = new Runnable() {
            @Override
            public void run() {

                // uv index
                BleManager.getInstance().read(
                        bleDevice,
//                characteristic_temperature.getUuid().toString(),
//                service_env_sensing.getUuid().toString(),
                        "0000181A-0000-1000-8000-00805F9B34FB",
                        "00002A76-0000-1000-8000-00805F9B34FB",
                        new BleReadCallback() {

                            @Override
                            public void onReadSuccess(final byte[] data) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String content = "";
                                        boolean addSpace = false;

                                        if (data == null || data.length < 1)
                                            content = "No Data Fetched";
                                        else {
//                                            StringBuilder sb = new StringBuilder();
//
//                                            // 数据原因，所以倒叙读出
//                                            for (int i = 0; i < data.length ; i++) {
//                                                Log.d(TAG, i+" -> "+data[i]);
//                                            }
//
//                                            for (int i = data.length - 1; i >= 0; i--) {
//                                                String hex = Integer.toHexString(data[i] & 0xFF);
//                                                if (hex.length() == 1) {
//                                                    hex = '0' + hex;
//                                                }
//                                                sb.append(hex);
//                                                if (addSpace)
//                                                    sb.append(" ");
//                                            }
//                                            content = sb.toString().trim();

//                                            content = HexUtil.hexStrLittleEndian(data, false);
//                                            long result = Long.parseLong(content, 16);
//                                            content+= (" => " + result/100f+" uv");

                                            long result = HexUtil.lng(data, false);
                                            content+= (" => " + result/100f+" uv");

                                            Log.d(TAG, "uv index => "+result/100f+" uv");
                                        }
//                                String plainHexData = HexUtil.formatHexString(data, true);

                                        mBinder.tvUv.setText(content);
                                    }
                                });
                            }

                            @Override
                            public void onReadFailure(final BleException exception) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinder.tvTemperature.setText(exception.toString());
                                    }
                                });
                            }
                        });
                mHandler.postDelayed(this, 1000);
            }
        };

        runnableHumidity = new Runnable() {
            @Override
            public void run() {
                // humidity
                BleManager.getInstance().read(
                        bleDevice,
//                characteristic_temperature.getUuid().toString(),
//                service_env_sensing.getUuid().toString(),
                        "0000181A-0000-1000-8000-00805F9B34FB",
                        "00002A6F-0000-1000-8000-00805F9B34FB",
                        new BleReadCallback() {

                            @Override
                            public void onReadSuccess(final byte[] data) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String content = "";
                                        boolean addSpace = false;

                                        if (data == null || data.length < 1)
                                            content = "No Data Fetched";
                                        else {
//                                            StringBuilder sb = new StringBuilder();
//
//                                            // 数据原因，所以倒叙读出
//                                            for (int i = 0; i < data.length ; i++) {
//                                                Log.d(TAG, i+" -> "+data[i]);
//                                            }
//
//                                            for (int i = data.length - 1; i >= 0; i--) {
//                                                String hex = Integer.toHexString(data[i] & 0xFF);
//                                                if (hex.length() == 1) {
//                                                    hex = '0' + hex;
//                                                }
//                                                sb.append(hex);
//                                                if (addSpace)
//                                                    sb.append(" ");
//                                            }
//                                            content = sb.toString().trim();

//                                            content = HexUtil.hexStrLittleEndian(data, false);
//                                            long result = Long.parseLong(content, 16);
                                            long result = HexUtil.lng(data, false);
                                            content+= (" => " + result/100f+" %");


                                            Log.d(TAG, "humidity index => "+result/100f+" %");
                                        }
//                                String plainHexData = HexUtil.formatHexString(data, true);

                                        mBinder.tvHumidity.setText(content);
                                    }
                                });
                            }

                            @Override
                            public void onReadFailure(final BleException exception) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinder.tvTemperature.setText(exception.toString());
                                    }
                                });
                            }
                        });
                mHandler.postDelayed(this, 1000);
            }
        };

        mHandler.postDelayed(runnableTemperature, 333);
        mHandler.postDelayed(runnableUV, 666);
        mHandler.postDelayed(runnableHumidity, 999);

    }


    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }

    @Override
    public void onBLEDisconneted(BleDevice device) {
        if (device != null && bleDevice != null && device.getKey().equals(bleDevice.getKey())) {
            finish();
        }

    }

    @Override
    public void onBLEConneted(BleDevice bleDevice) {
        // So far, nothing to do here.
        // since bleDevice is the bleDevice you can get through intent.
    }
}
