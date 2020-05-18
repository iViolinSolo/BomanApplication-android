package me.violinsolo.testlibsapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import me.violinsolo.testlibsapp.R;
import me.violinsolo.testlibsapp.base.BaseFragment;
import me.violinsolo.testlibsapp.databinding.FragmentFirstBinding;

public class FirstFragment extends BaseFragment<FragmentFirstBinding> {
    private static final String TAG = FirstFragment.class.getSimpleName();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * need to initilize the ViewBinding Class in every sub activity class.
     *
     * @param inflater
     * @param parent
     * @return mBinder the field of view binding handler. Initialize it before use
     * eg.
     * mBinder = FragmentXMLNameBinding.inflate(inflater, container, boolean attachToParent = false);
     */
    @Override
    protected FragmentFirstBinding onBind(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentFirstBinding.inflate(inflater, parent, false);
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        mBinder.btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Now try to get Bluetooth permission.");

                Log.e(TAG, "Now after Bluetooth permission granted.");
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }


}
