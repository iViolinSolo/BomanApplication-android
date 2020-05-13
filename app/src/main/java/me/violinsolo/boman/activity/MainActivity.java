package me.violinsolo.boman.activity;

import android.os.Bundle;

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
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
//        mBinder
    }
}
