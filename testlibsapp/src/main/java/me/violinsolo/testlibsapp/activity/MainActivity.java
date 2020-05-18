package me.violinsolo.testlibsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import me.violinsolo.testlibsapp.base.BaseActivity;
import me.violinsolo.testlibsapp.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

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
    protected ActivityMainBinding onBind() {
        return ActivityMainBinding.inflate(getLayoutInflater());
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

    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        mBinder.btnGoActiPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TestPermissionActivity.class);
                startActivity(intent);
            }
        });
    }
}
