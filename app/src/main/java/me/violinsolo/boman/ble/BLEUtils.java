package me.violinsolo.boman.ble;

import android.app.Application;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.List;
import java.util.UUID;

public class BLEUtils {
    public enum BLEState {UNBOUND, BOUND_CONNECTED, BOUND_DISCONNECTED}
    public abstract static class BleConnectCallBack extends BleGattCallback {}

    private BleScanCallback bleScanCallback = null;
    private BleConnectCallBack bleConnectCallBack = null;

    public BLEUtils() {
    }

    public void setBleScanCallback(BleScanCallback bleScanCallback) {
        this.bleScanCallback = bleScanCallback;
    }

    public void setBleConnectCallBack(BleConnectCallBack bleConnectCallBack) {
        this.bleConnectCallBack = bleConnectCallBack;
    }

    // init and config.
    public void init(Application app) {
        BleManager.getInstance().init(app);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
    // destroy.
    public void clean() {
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    // start scan ...
    public void startScan() {
        BleManager.getInstance().scan(bleScanCallback);
    }

    private void setScanRule(String macAddr) {
        String[] uuids = null;
//        String str_uuid = et_uuid.getText().toString();
//        if (TextUtils.isEmpty(str_uuid)) {
//            uuids = null;
//        } else {
//            uuids = str_uuid.split(",");
//        }
        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5) {
                    serviceUuids[i] = null;
                } else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

        String[] names = null;;
//        String str_name = et_name.getText().toString();
//        if (TextUtils.isEmpty(str_name)) {
//            names = null;
//        } else {
//            names = str_name.split(",");
//        }

//        String mac = et_mac.getText().toString();
        String mac = macAddr;

//        boolean isAutoConnect = sw_auto.isChecked();
        boolean isAutoConnect = true;

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }


    // connect to specified device.
    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, bleConnectCallBack);
    }

    public List<BleDevice> showConnectedDevice() {
        return BleManager.getInstance().getAllConnectedDevice();
    }

    public boolean disconnectTo() {
        return false;
    }


//    private void readRssi(BleDevice bleDevice) {
//        BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
//            @Override
//            public void onRssiFailure(BleException exception) {
//                Log.i(TAG, "onRssiFailure" + exception.toString());
//            }
//
//            @Override
//            public void onRssiSuccess(int rssi) {
//                Log.i(TAG, "onRssiSuccess: " + rssi);
//            }
//        });
//    }
//
//    private void setMtu(BleDevice bleDevice, int mtu) {
//        BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
//            @Override
//            public void onSetMTUFailure(BleException exception) {
//                Log.i(TAG, "onsetMTUFailure" + exception.toString());
//            }
//
//            @Override
//            public void onMtuChanged(int mtu) {
//                Log.i(TAG, "onMtuChanged: " + mtu);
//            }
//        });
//    }

}
