package me.violinsolo.testlibsapp.activity;

import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;

import me.violinsolo.testlibsapp.base.BaseActivity;
import me.violinsolo.testlibsapp.databinding.ActivityTestImageStatusBarBinding;

public class TestImageStatusBarActivity extends BaseActivity<ActivityTestImageStatusBarBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_image_status_bar);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityTestImageStatusBarBinding onBind() {
        return ActivityTestImageStatusBarBinding.inflate(getLayoutInflater());
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
        StatusBarUtil.setTransparent(this);
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }
}
