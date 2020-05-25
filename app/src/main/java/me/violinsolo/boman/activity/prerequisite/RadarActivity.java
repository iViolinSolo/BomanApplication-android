package me.violinsolo.boman.activity.prerequisite;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import me.violinsolo.boman.R;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityRadarBinding;
import me.violinsolo.boman.fragment.ConnectFailureFragment;
import me.violinsolo.boman.fragment.ConnectLoadingFragment;
import me.violinsolo.boman.fragment.DeviceListFragment;

public class RadarActivity extends BaseActivity<ActivityRadarBinding> {
    public static final String TAG = RadarActivity.class.getSimpleName();
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
                boolean addSpace = true;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < advertisementLength; i++) {
                    String hex = Integer.toHexString(broadcastData[i] & 0xFF);
                    sb.append(hex);
                    if (addSpace)
                        sb.append(" ");
                }
                String content = sb.toString().trim();
                Log.e(TAG, bleDevice.getKey()+" \t[length]: "+advertisementLength+" -> "+content);

                if (content.equals("ss")) {
                    curState = ConnState.FOUND_DEVICES;
                    filteredScanResult.add(bleDevice);

                    if (filteredScanResult.size() == 1) {
                        showDevicesListPage();
                    }
                }
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
