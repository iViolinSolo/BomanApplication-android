package me.violinsolo.boman.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.UUID;

import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.ble.Observer;
import me.violinsolo.boman.ble.ObserverManager;
import me.violinsolo.boman.databinding.ActivityDetailsBinding;

public class DetailsActivity extends BaseActivity<ActivityDetailsBinding> implements Observer {
    private static final String TAG = DetailsActivity.class.getSimpleName();

    public static final String EXTRA_DATA_BLE = "EXTRA_DATA_BLE";
    public Context mContext = DetailsActivity.this;

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().clearCharacterCallback(bleDevice);
        ObserverManager.getInstance().deleteObserver(this);
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
        if (bleDevice == null)
            finish();
    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {
        setSupportActionBar(mBinder.toolbar);

        ObserverManager.getInstance().addObserver(this);

        mBinder.tvDeviceInfo.setText(String.format("%s - %s", bleDevice.getName(), bleDevice.getMac()));
        mBinder.tvDeviceInfo.setTextColor(0xFF8DA9E4);


        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
//        for (BluetoothGattService service : gatt.getServices()) {
////            service.
//        }
        BluetoothGattService service_env_sensing = gatt.getService(UUID.fromString("0000181A-0000-1000-8000-00805F9B34FB"));
        BluetoothGattCharacteristic characteristic_temperature = service_env_sensing.getCharacteristic(UUID.fromString("00002A6E-0000-1000-8000-00805F9B34FB"));
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
                                    StringBuilder sb = new StringBuilder();

                                    // 数据原因，所以倒叙读出

                                    for (int i = data.length - 1; i >= 0; i--) {
                                        String hex = Integer.toHexString(data[i] & 0xFF);
                                        if (hex.length() == 1) {
                                            hex = '0' + hex;
                                        }
                                        sb.append(hex);
                                        if (addSpace)
                                            sb.append(" ");
                                    }
                                    content = sb.toString().trim();

                                    Long result = Long.getLong(content, 16);
                                    content+= (" => " + (result != null ? result.toString() : "null"));
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
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }

    @Override
    public void disConnected(BleDevice device) {
        if (device != null && bleDevice != null && device.getKey().equals(bleDevice.getKey())) {
            finish();
        }

    }
}
