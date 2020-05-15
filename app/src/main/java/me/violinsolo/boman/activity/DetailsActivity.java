package me.violinsolo.boman.activity;

import android.content.Context;
import android.os.Bundle;

import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityDetailsBinding;

public class DetailsActivity extends BaseActivity<ActivityDetailsBinding> {
    private static final String TAG = DetailsActivity.class.getSimpleName();

    public static final String EXTRA_DATA_BLE = "EXTRA_DATA_BLE";
    public Context mContext = DetailsActivity.this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityDetailsBinding onBind() {
        return ActivityDetailsBinding.inflate(getLayoutInflater());
    }

    /**
     * write init view codes, such as toolbar.
     */
    @Override
    protected void initViews() {

    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }
}
