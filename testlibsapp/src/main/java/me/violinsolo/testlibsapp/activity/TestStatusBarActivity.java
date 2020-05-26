package me.violinsolo.testlibsapp.activity;

import android.os.Bundle;
import android.view.View;

import com.jaeger.library.StatusBarUtil;

import me.violinsolo.testlibsapp.R;
import me.violinsolo.testlibsapp.base.BaseActivity;
import me.violinsolo.testlibsapp.databinding.ActivityTestStatusBarBinding;

public class TestStatusBarActivity extends BaseActivity<ActivityTestStatusBarBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_status_bar);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityTestStatusBarBinding onBind() {
        return ActivityTestStatusBarBinding.inflate(getLayoutInflater());
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
        StatusBarUtil.setColor(this, 0xff05D1A7);
        setSupportActionBar(mBinder.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可
        mBinder.toolbar.setNavigationIcon(R.mipmap.back);
        mBinder.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }
}
