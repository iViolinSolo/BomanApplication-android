package me.violinsolo.boman.activity.prerequisite;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import me.violinsolo.boman.R;
import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityLocationCheckBinding;

public class LocationCheckActivity extends BaseActivity<ActivityLocationCheckBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_check);
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

    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {
        if (checkGPSIsOpen()) {
            Intent intent = new Intent(LocationCheckActivity.this, BluetoothCheckActivity.class);

            startActivity(intent);
            finish();
        }else {

        }
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

}
