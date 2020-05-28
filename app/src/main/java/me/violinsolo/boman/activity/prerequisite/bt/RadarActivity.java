package me.violinsolo.boman.activity.prerequisite.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import me.violinsolo.boman.R;
import me.violinsolo.boman.activity.DetailsActivity;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityRadarBinding;
import me.violinsolo.boman.fragment.ConnectFailureFragment;
import me.violinsolo.boman.fragment.ConnectLoadingFragment;
import me.violinsolo.boman.fragment.DeviceListFragment;
import me.violinsolo.boman.listener.OnFailureButtonClickLinstener;
import me.violinsolo.boman.listener.OnRecyclerViewItemClickListener;
import me.violinsolo.boman.subscribe.ObserverManager;
import me.violinsolo.boman.util.Config;
import me.violinsolo.boman.util.HexUtil;
import me.violinsolo.boman.util.Intermediate;

public class RadarActivity extends BaseActivity<ActivityRadarBinding> {
    public static final String TAG = RadarActivity.class.getSimpleName();
    public static final String EXTRA_DATA_MAC = "EXTRA_DATA_MAC";
//    private SharedPrefUtils spUtil;
    private boolean exitOnPurpose = true; // being used to control the back-btn use, you need do something is destory.

    public enum ConnState {
        PAGE_INIT,
        START_SCANNING,
        FOUND_DEVICES,
        NO_DEVICES,
        SCANNING_FINISH,
        START_CONNECTING,
        START_CONNECTING_MAC,
        CONNECTED,
        CONNECT_FAIL
    }

    public Context mContext;
    private List<Fragment> fragments;
    private ConnectFailureFragment connectFailureFragment;
    private ConnectLoadingFragment connectLoadingFragment;
    private DeviceListFragment deviceListFragment;

    public String macAddr;

    public ConnState curState;
    public List<BleDevice> filteredScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_radar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RadarActivity.onDestroy()");
        if (Intermediate.getInstance().statusIsScanning) {
            Log.i(TAG, "Now cancel scanning....");
            BleManager.getInstance().cancelScan(); // TODO the npe will be triggered when the Manager is not scanning
            // .cancelScan() can trigger onScanFinished method
            Intermediate.getInstance().statusIsScanning = false;
        }
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityRadarBinding onBind() {
        return ActivityRadarBinding.inflate(getLayoutInflater());
    }

    /**
     * init data here, like you can get data from extra.
     * eg.
     * var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     */
    @Override
    protected void initData() {
        mContext = RadarActivity.this;
        fragments = new ArrayList<>();
        filteredScanResult = new ArrayList<>();

//        spUtil = new SharedPrefUtils(mContext);

        // macAddr will be null if it is not a bound-device-connection
        macAddr = getIntent().getStringExtra(RadarActivity.EXTRA_DATA_MAC);
//        Log.i(TAG, macAddr != null ? macAddr : "null");
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
//        mBinder.toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_32dp);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite), 0);
//        StatusBarUtil.setLightMode(RadarActivity.this); //TODO potential bug in light mode, maybe need to check the fragment demo to see how to use this line.
        mBinder.toolbar.setNavigationIcon(R.mipmap.ic_back);
        mBinder.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (!checkBluetoothIsOpen()||!checkGPSIsOpen()) {
            Log.e(TAG, "> Please keep bluetooth and location open.");
            Toast.makeText(mContext, "请保持蓝牙和位置服务开启。", Toast.LENGTH_SHORT).show();
            finish();
        }

        prepareFragments();

        if (macAddr == null) {
            curState = ConnState.PAGE_INIT;
        }else {
            curState = ConnState.START_CONNECTING_MAC;
        }
        showLoadingPage();
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        final BleGattCallback callback = new BleGattCallback() {
            @Override
            public void onStartConnect() {
                if (macAddr == null) {
                    curState = ConnState.START_CONNECTING;
                }else {
                    curState = ConnState.START_CONNECTING_MAC;
                }
                showLoadingPage();
                Log.i(TAG, "[Bluetooth] onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                curState = ConnState.CONNECT_FAIL;
                showFailurePage();
                Log.i(TAG, "[Bluetooth] onConnectFail");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                curState = ConnState.CONNECTED;

//                        spUtil.storeBoundDeviceV2(bleDevice);
                ObserverManager.getInstance().notifyObserverWhenConnected(bleDevice); // mainly notify MainActivity to store the current device.

                // Go to the setting page directly, more user friendly.
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXTRA_DATA_BLE, bleDevice);
                startActivity(intent);

                finish();
                Log.i(TAG, "[Bluetooth] onConnectSuccess");
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                if (isActiveDisConnected) {
                    Toast.makeText(mContext, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserverWhenDisonnected(device); // TODO, need to check observable functionality.
                } else {
                    Toast.makeText(mContext, getString(R.string.inactive_disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserverWhenDisonnected(device); // TODO, need to check observable functionality.
                }
                Log.i(TAG, "[Bluetooth] onDisConnected");
            }
        };


        if (macAddr == null) {
            // you can trigger the scanning functionality.
            // Auto-triggered Scanning Part.
            BleManager.getInstance().scan(new BleScanCallback() {
                @Override
                public void onScanFinished(List<BleDevice> scanResultList) {
                    Log.i(TAG, "[Bluetooth] onScanFinished");
                    curState = ConnState.SCANNING_FINISH;

                    if (filteredScanResult.isEmpty()) {
                        curState = ConnState.NO_DEVICES;
                        showFailurePage();
                    }

                    Intermediate.getInstance().statusIsScanning = false;
                }

                @Override
                public void onScanStarted(boolean success) {
                    if (success) {
                        curState = ConnState.START_SCANNING;
                        showLoadingPage();

                        Intermediate.getInstance().statusIsScanning = true;
                    }else {
                        curState = ConnState.NO_DEVICES;
                        showFailurePage();
                    }

                    Log.i(TAG, "[Bluetooth] onScanStarted");
                }

                @Override
                public void onScanning(BleDevice bleDevice) {
                    byte[] broadcastData = bleDevice.getScanRecord();

                    int advertisementLength = broadcastData.length;

                    final String tmp = "00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61";
                    String content = HexUtil.hexStrBigEndian(broadcastData);
                    Log.e(TAG, "\n\t\t"+bleDevice.getKey()+" \n\t\t[length]: "+advertisementLength+" -> \n\t\t"+tmp+"\n\t\t"+content);


                    String criticalInfo = "";
                    for (int i = 0; i < advertisementLength; i++) {
                        int secLen = broadcastData[i] & 0xff;

//                        if (i>=31) { // BUG, sometimes it will exceed the 31length.
//                            break; // or the response data field will out of bound.`
//                        }
                        if(secLen == 0) {
                            continue;
                        }

                        byte secType = broadcastData[i+1];
                        if (secType == (byte)0xff){
                            // current section is manufacture info section.

                            byte[] secData = new byte[secLen-1];
                            for (int j = 0; j < secLen-1; j++) {
                                secData[j] = broadcastData[j+i+2];
                            }

                            String plainHex = HexUtil.hexStrBigEndian(secData);
                            String plainAscii = HexUtil.str(secData, true);

//                            Log.e(TAG, bleDevice.getKey()+" \t\t [Manufacturer Info]: <"+plainHex+"> => ["+plainAscii+"]");
                        }else if (secType == (byte)0x09){
                            // current section is device name info section.

                            byte[] secData = new byte[secLen-1];
                            for (int j = 0; j < secLen-1; j++) {
                                secData[j] = broadcastData[j+i+2];
                            }

                            String plainHex = HexUtil.hexStrBigEndian(secData);
                            String plainAscii = HexUtil.str(secData, true);
                            criticalInfo = plainAscii;

                            Log.d(TAG, bleDevice.getKey()+" \t\t [Device Name Info]: <"+plainHex+"> => ["+plainAscii+"]");
                        }else {
                            // Nothing to do.
                        }

                        i += secLen; // skip current section, next iteration, i==i+secLen+1
                    }

                    for (String nm:
                            Config.deviceNames) {

                        Log.d(TAG, "[check] => "+bleDevice.getKey()+"\t\t|"+criticalInfo+"| == |"+nm+"|");
                        if (criticalInfo.startsWith(nm)) {
                            curState = ConnState.FOUND_DEVICES;
                            filteredScanResult.add(bleDevice);

                            if (filteredScanResult.size() == 1) {
                                showDevicesListPage();
                            }

                            deviceListFragment.addRvItem(bleDevice);
                            Log.d(TAG, "[Add] => "+bleDevice.getKey());
                            break;
                        }
                    }

                    Log.i(TAG, "[Bluetooth] onScanning");
                }
            });

        }else {
            // you should connect the device directly.
            if (Intermediate.getInstance().statusIsScanning) {
                BleManager.getInstance().cancelScan(); // TODO the npe will be triggered when the Manager is not scanning
                Intermediate.getInstance().statusIsScanning = false;
            }
            BleManager.getInstance().connect(macAddr, callback);
        }

        deviceListFragment.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BleDevice target = deviceListFragment.mAdapter.getItem(position);
                if (!BleManager.getInstance().isConnected(target)) {
                    // connect the device through BleDevice.
                    if (Intermediate.getInstance().statusIsScanning) {
                        BleManager.getInstance().cancelScan(); // TODO the npe will be triggered when the Manager is not scanning
                        Intermediate.getInstance().statusIsScanning = false;
                    }
                    BleManager.getInstance().connect(target, callback);
                }
            }
        });

        connectFailureFragment.setOnFailureButtonClickLinstener(new OnFailureButtonClickLinstener() {
            @Override
            public void OnClick(View view) {
                finish();
            }
        });
    }

    private void prepareFragments() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }

        deviceListFragment = DeviceListFragment.newInstance();
        fragments.add(deviceListFragment);
        connectLoadingFragment = ConnectLoadingFragment.newInstance();
        fragments.add(connectLoadingFragment);
        connectFailureFragment = ConnectFailureFragment.newInstance("");
        fragments.add(connectFailureFragment);

        for (Fragment f :
                fragments) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, f).hide(f).commit();
        }

    }

    private void showLoadingPage() {
        // Attention, there are two types of LoadingPage
        getSupportFragmentManager().beginTransaction().show(connectLoadingFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(connectFailureFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(deviceListFragment).commit();

        if (curState == ConnState.START_SCANNING) {
            connectLoadingFragment.setLoadingTitle("开始扫描");
        }else if (curState == ConnState.START_CONNECTING) {
            connectLoadingFragment.setLoadingTitle("尝试连接新设备");
        }else if (curState == ConnState.START_CONNECTING_MAC) {
            connectLoadingFragment.setLoadingTitle("尝试连接绑定设备");
        }else if (curState == ConnState.PAGE_INIT) {
            connectLoadingFragment.setLoadingTitle("开始准备硬件环境");
        }
    }

    private void showDevicesListPage() {
        getSupportFragmentManager().beginTransaction().hide(connectLoadingFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(connectFailureFragment).commit();
        getSupportFragmentManager().beginTransaction().show(deviceListFragment).commit();
    }

    private void showFailurePage() {
        // Attention, there are two types of FailurePage
        getSupportFragmentManager().beginTransaction().hide(connectLoadingFragment).commitAllowingStateLoss();  // fix bug: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        getSupportFragmentManager().beginTransaction().show(connectFailureFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().hide(deviceListFragment).commitAllowingStateLoss();

        if (curState == ConnState.CONNECT_FAIL) {
            connectFailureFragment.setCurrentTitle("连接不到您的产品？");
        }else if (curState == ConnState.NO_DEVICES) {
            connectFailureFragment.setCurrentTitle("看不到您的产品？");
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
        if (bluetoothAdapter == null)
            return false;
        return bluetoothAdapter.isEnabled();
    }
}
