package me.violinsolo.testlibsapp.activity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.violinsolo.testlibsapp.R;
import me.violinsolo.testlibsapp.base.BaseActivity;
import me.violinsolo.testlibsapp.databinding.ActivityTestGifBinding;

public class TestGifActivity extends BaseActivity<ActivityTestGifBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gif);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    @Override
    protected ActivityTestGifBinding onBind() {
        return ActivityTestGifBinding.inflate(getLayoutInflater());
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
        Glide.with(this).asGif().diskCacheStrategy(DiskCacheStrategy.RESOURCE).load(R.drawable.bluetooth_intro).into(mBinder.imageView);
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }
}
