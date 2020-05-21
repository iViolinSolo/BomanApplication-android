package me.violinsolo.boman.activity.prerequisite;

import android.content.Context;
import android.os.Bundle;

import me.violinsolo.boman.base.BaseActivity;
import me.violinsolo.boman.databinding.ActivityRadarBinding;

public class RadarActivity extends BaseActivity<ActivityRadarBinding> {
    public static final String TAG = RadarActivity.class.getSimpleName();

    public Context mContext;

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
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }
}
