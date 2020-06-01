package me.violinsolo.testlibsapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.violinsolo.testlibsapp.adapter.TemperatureHistoryAdapter;
import me.violinsolo.testlibsapp.base.BaseActivity;
import me.violinsolo.testlibsapp.databinding.ActivityAdcvBinding;
import me.violinsolo.testlibsapp.model.TemperatureRecord;
import me.violinsolo.testlibsapp.util.DateUtil;

public class AdcvActivity extends BaseActivity<ActivityAdcvBinding> {
    private static final String TAG = AdcvActivity.class.getSimpleName();
    public Context mContext;
    private TemperatureHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_adcv);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityAdcvBinding onBind() {
        return ActivityAdcvBinding.inflate(getLayoutInflater());
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
        // set layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mBinder.rvHist.setLayoutManager(mLayoutManager);
        // set adapter
        mAdapter = new TemperatureHistoryAdapter(mContext);
        mBinder.rvHist.setAdapter(mAdapter);


        Random random = new Random();
        // mock data
        for (int i = 0; i < 20; i++) {
            long curTime = DateUtil.getCurrentTimestamp();
            int m = random.nextInt(200)-100;  // [-100. 100)
            long tr = curTime+m*1000000L;
            int tt = 3750+m;

            TemperatureRecord record = new TemperatureRecord(tt, tr);
            Log.d(TAG, record.toString());
            mAdapter.addRecord(record);
        }
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }
}
