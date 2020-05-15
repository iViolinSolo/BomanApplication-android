package me.violinsolo.boman.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

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
