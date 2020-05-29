package me.violinsolo.boman.activity.prerequisite.bt;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.violinsolo.boman.R;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityLocationCheckBinding;
import me.violinsolo.boman.util.BluetoothUtil;
import me.violinsolo.boman.util.Config;
import me.violinsolo.boman.util.StatusBarUtilNEW;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
@RuntimePermissions
public class LocationCheckActivity extends BaseActivity<ActivityLocationCheckBinding> {
    public static final String TAG = LocationCheckActivity.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_GPS = 1;

    public Context mContext;
    public String macAddr;

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
        StatusBarUtilNEW.setLightMode(LocationCheckActivity.this);
        mBinder.toolbar.setNavigationIcon(R.mipmap.ic_back);
        mBinder.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // set highlight.
        SpannableStringBuilder style = new SpannableStringBuilder(mBinder.tvPermitLocationExplanation.getText());
        style.setSpan(new ForegroundColorSpan(Config.colorPrimary), 9, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Config.colorPrimary), 16, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinder.tvPermitLocationExplanation.setText(style);

        mBinder.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Show dialog to guide the user permit us");

                // NOTE: delegate the permission handling to generated method
                LocationCheckActivityPermissionsDispatcher.showCheckPermissionStateWithPermissionCheck(LocationCheckActivity.this);
            }
        });

        // 1. when we get into this interface, we need to check if the permission has been dynamic applied.
        LocationCheckActivityPermissionsDispatcher.showCheckPermissionStateWithPermissionCheck(LocationCheckActivity.this);


//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || BluetoothUtil.checkGPSIsOpen(mContext)) {
//            Log.d(TAG, "> Location service check PASS...");
//            goToNextPage();
//        }else {
//            Log.e(TAG, "> Location service check FAIL...");
//
//
//            // set highlight.
//            SpannableStringBuilder style = new SpannableStringBuilder(mBinder.tvPermitLocationExplanation.getText());
//            style.setSpan(new ForegroundColorSpan(Config.colorPrimary), 9, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            style.setSpan(new ForegroundColorSpan(Config.colorPrimary), 16, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            mBinder.tvPermitLocationExplanation.setText(style);
//
//            mBinder.btnNext.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG, "Show dialog to guide the user permit us");
//
//                    // NOTE: delegate the permission handling to generated method
//                    LocationCheckActivityPermissionsDispatcher.showCheckPermissionStateWithPermissionCheck(LocationCheckActivity.this);
//                    // 通过强行插入这段代码，把下面的内容都移动到权限赋予成功的界面去了。
////                    BluetoothUtil.showGoLocationSettingDialog(mContext,
////                            new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    finish();
////                                }
////                            },
////                            new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    if (BluetoothUtil.checkGPSIsOpen(mContext)) {
////                                        goToNextPage();
////                                    }else {
////                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                                        startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
////                                    }
////                                }
////                            });
//                }
//            });
//        }


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
            if (BluetoothUtil.checkGPSIsOpen(mContext)) {
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
        intent.putExtra(RadarActivity.EXTRA_DATA_MAC, macAddr); // cascade transport the value.

        startActivity(intent);
        finish();
    }

//    private boolean checkGPSIsOpen() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager == null)
//            return false;
//        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
//    }




    /**
     * -----------------------------------------
     * check permissions here...
     *
     * {
     *      Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,   /// this can be gained just through manifest declaration
     *      Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,   /// these are dangerous permissions need to dynamic apply
     *      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE   /// not use this yet.
     * }
     * -----------------------------------------
     */

    /**
     * 这个方法中写正常的逻辑（假设有该权限应该做的事）
     */
    @NeedsPermission({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showCheckPermissionState(){
        //检查是否开启位置信息（如果没有开启，则无法扫描到任何蓝牙设备在6.0）

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || BluetoothUtil.checkGPSIsOpen(mContext)) {
            Log.d(TAG, "> Location service check PASS...");
            goToNextPage();
        }else {
            Log.e(TAG, "> Location service check FAIL...");

            if (!BluetoothUtil.checkGPSIsOpen(mContext)){
                BluetoothUtil.showGoLocationSettingDialog(
                        mContext,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (BluetoothUtil.checkGPSIsOpen(mContext)) {
                                    goToNextPage();
                                }else {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                }
                            }
                        }
                );
            }
        }
//        if (!BluetoothUtil.checkGPSIsOpen(mContext)){
//            BluetoothUtil.showGoLocationSettingDialog(
//                    mContext,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    },
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (BluetoothUtil.checkGPSIsOpen(mContext)) {
//                                goToNextPage();
//                            }else {
//                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
//                            }
//                        }
//                    }
//            );
//        }
    }

    /**
     * 弹出权限同意窗口之前调用的提示窗口
     * @param request
     */
    @OnShowRationale({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showRationaleForPermissionState(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        BluetoothUtil.showRationaleDialog(mContext, R.string.permission_rationale, request);
    }

    /**
     * 提示窗口和权限同意窗口--被拒绝时调用
     */
    @OnPermissionDenied({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onPermissionStateDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
    }

    /**
     * 当完全拒绝了权限打开之后调用
     */
    @OnNeverAskAgain({Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onPermissionNeverAskAgain() {
        BluetoothUtil.showOpenSettingDialog(this, R.string.open_setting_permission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        LocationCheckActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
