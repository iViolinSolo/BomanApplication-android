package me.violinsolo.boman.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    protected T mBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        mBinder = onBind(inflater, container);

        bindListeners();

        return mBinder.getRoot();
    }


    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @return mBinder the field of view binding handler. Initialize it before use
     *                  eg.
     *                  mBinder = FragmentXMLNameBinding.inflate(inflater, container, boolean attachToParent = false);
     */
    protected abstract T onBind(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent);


    /**
     * bind all listeners here.
     */
    protected abstract void bindListeners();
}
