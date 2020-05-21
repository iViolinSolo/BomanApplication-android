package me.violinsolo.boman.activity.prerequisite;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.violinsolo.boman.R;
import me.violinsolo.boman.adapter.DeviceListAdapter;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityRadarBinding;

public class RadarActivity extends BaseActivity<ActivityRadarBinding> {
    public static final String TAG = RadarActivity.class.getSimpleName();

    public Context mContext;
    public DeviceListAdapter mAdapter;

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

        // set layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mBinder.rvListDevices.setLayoutManager(mLayoutManager);
        // set adapter
        mAdapter = new DeviceListAdapter();
        mBinder.rvListDevices.setAdapter(mAdapter);

    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

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
