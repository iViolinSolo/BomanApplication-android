package me.violinsolo.boman.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<T extends ViewBinding > extends AppCompatActivity {

    protected T mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinder = onBind();

        setContentView(mBinder.getRoot());

        initData();
        initViews();
        bindListeners();
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     *                  eg.
     *                  mBinder = ActivityXMLNameBinding.inflate(getLayoutInflater());
     */
    protected abstract T onBind();

    /**
     * init data here, like you can get data from extra.
     *  eg.
     *  var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     */
    protected abstract void initData();

    /**
     * write init view codes, such as toolbar.
     */
    protected abstract void initViews();

    /**
     * bind all listeners here.
     */
    protected abstract void bindListeners();
}
