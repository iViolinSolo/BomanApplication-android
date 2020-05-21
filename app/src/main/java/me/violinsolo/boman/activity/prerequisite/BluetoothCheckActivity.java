package me.violinsolo.boman.activity.prerequisite;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityBluetoothCheckBinding;
import me.violinsolo.boman.util.Config;

public class BluetoothCheckActivity extends BaseActivity<ActivityBluetoothCheckBinding> {
    public static final String TAG = BluetoothCheckActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bluetooth_check);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityBluetoothCheckBinding onBind() {
        return ActivityBluetoothCheckBinding.inflate(getLayoutInflater());
    }

    /**
     * init data here, like you can get data from extra.
     * eg.
     * var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     */
    @Override
    protected void initData() {
        mContext = BluetoothCheckActivity.this;
    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {

        setSupportActionBar(mBinder.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可

        if (checkBluetoothIsOpen()) {
            Log.d(TAG, "> Bluetooth service check PASS...");
            goToNextPage();

        }else {
            Log.e(TAG, "> Bluetooth service check FAIL...");

            // set highlight.
            SpannableStringBuilder style = new SpannableStringBuilder(mBinder.tvPermitBtExplanation.getText());
            style.setSpan(new ForegroundColorSpan(Config.colorPrimary), 5, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBinder.tvPermitBtExplanation.setText(style);

            mBinder.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Ensures Bluetooth is available on the device and it is enabled. If not,
                    // displays a dialog requesting user permission to enable Bluetooth.
                    if (!checkBluetoothIsOpen()) {

                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
                    }

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
        if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (checkBluetoothIsOpen()) {
                goToNextPage();
            }else {
                Log.e(TAG, "User cancel the action to permit us with the privilege to open bluetooth.");
                Toast.makeText(mContext, "请授予我们蓝牙权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToNextPage() {
        Log.d(TAG, "Now go to next page: Search Device activity.");

        Intent intent = new Intent(mContext, RadarActivity.class);

        startActivity(intent);
        finish();
    }

    private boolean checkBluetoothIsOpen() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            return false;
        return bluetoothAdapter.isEnabled();
    }
}
