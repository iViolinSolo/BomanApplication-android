package me.violinsolo.boman.activity.prerequisite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import me.violinsolo.boman.R;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityLocationCheckBinding;

public class LocationCheckActivity extends BaseActivity<ActivityLocationCheckBinding> {
    public static final String TAG = LocationCheckActivity.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_GPS = 1;

    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_location_check); // if you set twice, the ViewBinding will not work
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityLocationCheckBinding onBind() {
        return ActivityLocationCheckBinding.inflate(getLayoutInflater());
    }

    /**
     * init data here, like you can get data from extra.
     * eg.
     * var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     */
    @Override
    protected void initData() {
        mContext = LocationCheckActivity.this;

    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {

        setSupportActionBar(mBinder.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkGPSIsOpen()) {
            Log.d(TAG, "> Location service check PASS...");
            goToNextPage();
        }else {
            Log.e(TAG, "> Location service check FAIL...");

            mBinder.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Show dialog to guide the user permit us");
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                }
            });
        }


    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                goToNextPage();
            }else {
                Log.e(TAG, "User cancel the action to permit us with the privilege to access Location.");
                Toast.makeText(mContext, "请授予我们位置权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToNextPage() {
        Log.d(TAG, "Now go to next page: Bluetooth check activity.");

        Intent intent = new Intent(mContext, BluetoothCheckActivity.class);

        startActivity(intent);
        finish();
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

}
