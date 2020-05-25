package me.violinsolo.boman.activity.prerequisite;

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
import me.violinsolo.boman.listener.OnRecyclerViewItemClick;
import me.violinsolo.boman.subscribe.ObserverManager;
import me.violinsolo.boman.util.Config;
import me.violinsolo.boman.util.HexUtil;
import me.violinsolo.boman.util.SharedPrefUtils;

public class RadarActivity extends BaseActivity<ActivityRadarBinding> {
    public static final String TAG = RadarActivity.class.getSimpleName();
    private SharedPrefUtils spUtil;

    public static enum ConnState {
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

    public ConnState curState;
    public List<BleDevice> filteredScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_radar);
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

        spUtil = new SharedPrefUtils(mContext);
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
        mBinder.toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_32dp);
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
        showLoadingPage();
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        // Auto-triggered Scanning Part.
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                curState = ConnState.SCANNING_FINISH;

                if (filteredScanResult.isEmpty()) {
                    curState = ConnState.NO_DEVICES;
                    showFailurePage();
                }
            }

            @Override
            public void onScanStarted(boolean success) {
                if (success) {
                    curState = ConnState.START_SCANNING;
                }else {
                    curState = ConnState.NO_DEVICES;
                    showFailurePage();
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                byte[] broadcastData = bleDevice.getScanRecord();

                int advertisementLength = broadcastData.length;

                String content = HexUtil.hexStrBigEndian(broadcastData);
                Log.e(TAG, bleDevice.getKey()+" \t[length]: "+advertisementLength+" -> "+content);


                String criticalInfo = "";
                for (int i = 0; i < advertisementLength; i++) {
                   int secLen = broadcastData[i] & 0xff;

                   if (i>=31) {
                       break; // or the response data field will out of bound.`
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

                       Log.e(TAG, bleDevice.getKey()+" \t\t [Manufacturer Info]: <"+plainHex+"> => ["+plainAscii+"]");
                   }else if (secType == (byte)0x09){
                       // current section is device name info section.

                       byte[] secData = new byte[secLen-1];
                       for (int j = 0; j < secLen-1; j++) {
                           secData[j] = broadcastData[j+i+2];
                       }

                       String plainHex = HexUtil.hexStrBigEndian(secData);
                       String plainAscii = HexUtil.str(secData, true);
                       criticalInfo = plainAscii;

                       Log.e(TAG, bleDevice.getKey()+" \t\t [Device Name Info]: <"+plainHex+"> => ["+plainAscii+"]");
                   }else {
                       // Nothing to do.
                   }

                   i += secLen; // skip current section, next iteration, i==i+secLen+1
                }

                for (String nm:
                        Config.deviceNames) {
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

            }
        });

        deviceListFragment.setOnRecyclerViewItemClick(new OnRecyclerViewItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                BleDevice target = deviceListFragment.mAdapter.getItem(position);
                BleManager.getInstance().connect(target, new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                        curState = ConnState.START_CONNECTING;
                        showLoadingPage();
                    }

                    @Override
                    public void onConnectFail(BleDevice bleDevice, BleException exception) {
                        curState = ConnState.CONNECT_FAIL;
                        showFailurePage();
                    }

                    @Override
                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        curState = ConnState.CONNECTED;

                        spUtil.storeBoundDevice(bleDevice);

                        Intent intent = new Intent(mContext, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.EXTRA_DATA_BLE, bleDevice);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                        if (isActiveDisConnected) {
                            Toast.makeText(mContext, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                            ObserverManager.getInstance().notifyObserver(device); // TODO, need to check observable functionality.
                        }
                    }
                });
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
    }

    private void showDevicesListPage() {
        getSupportFragmentManager().beginTransaction().hide(connectLoadingFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(connectFailureFragment).commit();
        getSupportFragmentManager().beginTransaction().show(deviceListFragment).commit();
    }

    private void showFailurePage() {
        // Attention, there are two types of FailurePage
        getSupportFragmentManager().beginTransaction().hide(connectLoadingFragment).commit();
        getSupportFragmentManager().beginTransaction().show(connectFailureFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(deviceListFragment).commit();
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
