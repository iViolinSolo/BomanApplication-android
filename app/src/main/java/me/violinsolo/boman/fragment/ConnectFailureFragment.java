package me.violinsolo.boman.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import me.violinsolo.boman.base.BaseFragment;
import me.violinsolo.boman.databinding.FragmentConnectFailureBinding;
import me.violinsolo.boman.listener.OnFailureButtonClickLinstener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectFailureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectFailureFragment extends BaseFragment<FragmentConnectFailureBinding> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAILURE_TITLE = "ARG_FAILURE_TITLE";

    private String failureTitle;
    private OnFailureButtonClickLinstener onFailureButtonClickLinstener;

    public ConnectFailureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param failureTitle Parameter that can hold the title to present,
     *                     if the previous stage is connecting a device, then present connect fail.
     *                     And if the previous stage is searching a device, then present search fail.
     * @return A new instance of fragment ConnectFailureFragment.
     */
    public static ConnectFailureFragment newInstance(String failureTitle) {
        ConnectFailureFragment fragment = new ConnectFailureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FAILURE_TITLE, failureTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            failureTitle = getArguments().getString(ARG_FAILURE_TITLE);
        }
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
    protected FragmentConnectFailureBinding onBind(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentConnectFailureBinding.inflate(LayoutInflater.from(getContext()), parent, false);
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

        // using cache to set title.
        mBinder.tvFailTitle.setText(failureTitle);
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        mBinder.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFailureButtonClickLinstener!=null) {
                    onFailureButtonClickLinstener.OnClick(view);
                }
            }
        });
    }

    public void setCurrentTitle(String failureTitle) {
        this.failureTitle = failureTitle;
        if (mBinder!=null) {
            mBinder.tvFailTitle.setText(failureTitle);
        }
    }

    public void setOnFailureButtonClickLinstener(OnFailureButtonClickLinstener onFailureButtonClickLinstener) {
        this.onFailureButtonClickLinstener = onFailureButtonClickLinstener;
    }
}
