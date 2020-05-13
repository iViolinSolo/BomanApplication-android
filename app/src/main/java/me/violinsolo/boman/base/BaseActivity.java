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
     * bind all listeners here.
     */
    protected abstract void bindListeners();
}
