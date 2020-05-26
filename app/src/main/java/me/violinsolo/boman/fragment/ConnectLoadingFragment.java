package me.violinsolo.boman.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import me.violinsolo.boman.base.BaseFragment;
import me.violinsolo.boman.databinding.FragmentConnectLoadingBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectLoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectLoadingFragment extends BaseFragment<FragmentConnectLoadingBinding> {
    private String loadingTitle;


    public ConnectLoadingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConnectLoadingFragment.
     */
    public static ConnectLoadingFragment newInstance() {
        ConnectLoadingFragment fragment = new ConnectLoadingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_device_list, container, false);
        // Using ViewBinding, this inflate part will be transferred to BaseFragment to handle it.
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
    protected FragmentConnectLoadingBinding onBind(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentConnectLoadingBinding.inflate(LayoutInflater.from(getContext()), parent, false);
    }

    /**
     * init data here, like you can get data from extra.
     * eg.
     * var data = getIntent().getParcelableExtra(EXTRA_KEY_XXX);
     * if (getArguments() != null) {
     * failureTitle = getArguments().getString(ARG_FAILURE_TITLE);
     * }
     */
    @Override
    protected void initData() {

    }

    /**
     * write init view codes, such as TextView.setText().
     */
    @Override
    protected void initViews() {
        mBinder.tvLoadingTitle.setText(loadingTitle);
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {

    }

    public void setLoadingTitle(String loadingTitle) {
        this.loadingTitle = loadingTitle;
        if (mBinder != null) {
            mBinder.tvLoadingTitle.setText(loadingTitle);
        }
    }
}
