package me.violinsolo.boman.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.violinsolo.boman.adapter.DeviceListAdapter;
import me.violinsolo.boman.base.BaseFragment;
import me.violinsolo.boman.databinding.FragmentDeviceListBinding;
import me.violinsolo.boman.listener.OnRecyclerViewItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceListFragment extends BaseFragment<FragmentDeviceListBinding> {
    public static final String TAG = DeviceListFragment.class.getSimpleName();

    public Context mContext;
    public DeviceListAdapter mAdapter;
    private List<BleDevice> bleDevices;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public DeviceListFragment() {
        // Required empty public constructor
        bleDevices = new ArrayList<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceListFragment.
     */
    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
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
    protected FragmentDeviceListBinding onBind(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentDeviceListBinding.inflate(LayoutInflater.from(getContext()), parent, false);
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
        mContext = getContext();
    }

    /**
     * write init view codes, such as TextView.setText().
     */
    @Override
    protected void initViews() {

        // set layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mBinder.rvListDevices.setLayoutManager(mLayoutManager);
        // set adapter
        mAdapter = new DeviceListAdapter(mContext);
        mBinder.rvListDevices.setAdapter(mAdapter);

        for (BleDevice ble :
                bleDevices) {
            mAdapter.addDevice(ble);
//            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * bind all listeners here.
     */
    @Override
    protected void bindListeners() {
        if (onRecyclerViewItemClickListener !=null) {
            mAdapter.setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener);
        }

    }


    public void addRvItem(BleDevice bleDevice) {
        bleDevices.add(bleDevice);
        if (mAdapter != null) {
//            Log.e(TAG, "Add "+bleDevice.getKey());
            // Before you show this fragment, adapter is null object due to the lifecycle.
            mAdapter.addDevice(bleDevice);
//            mAdapter.notifyDataSetChanged();
        }
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }
}
