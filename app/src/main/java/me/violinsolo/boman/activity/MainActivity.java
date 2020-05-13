package me.violinsolo.boman.activity;

import android.os.Bundle;
import android.view.View;

import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     *                eg.
     *                mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityMainBinding onBind() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {
        setSupportActionBar(mBinder.toolbar);
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        mBinder.findBleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void viewWhenNoBLE() {
        mBinder.findBleBtn.setVisibility(View.VISIBLE);
        mBinder.autoConnectBleBtn.setVisibility(View.GONE);
        mBinder.disconnectBleBtn.setVisibility(View.GONE);
    }

    private void viewWhenBindBLE() {
        mBinder.findBleBtn.setVisibility(View.GONE);
        mBinder.autoConnectBleBtn.setVisibility(View.VISIBLE);
        mBinder.disconnectBleBtn.setVisibility(View.VISIBLE);
    }
}
