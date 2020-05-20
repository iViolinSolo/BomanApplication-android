package me.violinsolo.boman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import me.violinsolo.boman.R;
import me.violinsolo.boman.ble.BLEUtils;
import me.violinsolo.boman.databinding.AdapterDeviceBinding;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/20 10:18 AM
 * @updateAt 2020/5/20 10:18 AM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class DeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<BleDevice> devices;
    private List<BLEUtils.BLEState> deviceStates;

    public DeviceAdapter(Context context) {
        this.mContext = context;
        this.deviceStates = new ArrayList<>();
        this.devices = new ArrayList<>();
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }


    public void addDevice(BleDevice device, BLEUtils.BLEState state){
        removeDevice(device);

        devices.add(device);
        deviceStates.add(state);
    }

    public void removeDevice(BleDevice device) {
        for (int i = 0; i < devices.size(); i++) {
            BleDevice t = devices.get(i);

            if (t.getKey().equals(device.getKey())) {
                devices.remove(i);
                deviceStates.remove(i);

                break;
            }
        }
    }

    public void updateDeviceState(BleDevice device, BLEUtils.BLEState newState) {
        for (int i = 0; i < devices.size(); i++) {
            BleDevice t = devices.get(i);

            if (t.getKey().equals(device.getKey())) {
                deviceStates.set(i, newState);

                break;
            }
        }

    }

    public void clearScanDevice() {
        int totalLen = devices.size();
        for (int i = totalLen-1; i > 0; i--) {
            BleDevice device = devices.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                devices.remove(i);
                deviceStates.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        int totalLen = devices.size();
        for (int i = totalLen-1; i > 0; i--) {
            BleDevice device = devices.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                devices.remove(i);
                deviceStates.remove(i);
            }
        }
    }


    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public BleDevice getItem(int position) {
        if (position > devices.size())
            return null;
        return devices.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        }else {
            AdapterDeviceBinding curBinder = AdapterDeviceBinding.inflate(LayoutInflater.from(mContext), viewGroup, false);
            view = curBinder.getRoot();

            holder = new ViewHolder(curBinder);
            view.setTag(holder);

        }

        final BleDevice bleDevice = getItem(i);
        final BLEUtils.BLEState bleState = deviceStates.get(i);
        final AdapterDeviceBinding mBinder = holder.mBinder;
        if (bleDevice != null) {
            String name = bleDevice.getName();
            String mac = bleDevice.getMac();
            int rssi = bleDevice.getRssi();
            mBinder.txtName.setText(name);
            mBinder.txtMac.setText(mac);
            mBinder.txtRssi.setText(String.valueOf(rssi));

//            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
//            if (isConnected) {
//                mBinder.imgBlue.setImageResource(R.mipmap.ic_warm_online);
//                mBinder.txtName.setTextColor(0xFF1DE9B6);
//                mBinder.txtMac.setTextColor(0xFF1DE9B6);
//                mBinder.layoutIdle.setVisibility(View.GONE);
//                mBinder.layoutConnected.setVisibility(View.VISIBLE);
//            } else {
//                mBinder.imgBlue.setImageResource(R.mipmap.ic_warm_offline);
//                mBinder.txtName.setTextColor(0xFF000000);
//                mBinder.txtMac.setTextColor(0xFF000000);
//                mBinder.layoutIdle.setVisibility(View.VISIBLE);
//                mBinder.layoutConnected.setVisibility(View.GONE);
//            }

            switch (bleState) {
                case UNBOUND:
                    mBinder.imgBlue.setImageResource(R.mipmap.ic_warm_offline);
                    mBinder.txtName.setTextColor(0xFF000000);
                    mBinder.txtMac.setTextColor(0xFF000000);
                    mBinder.layoutIdle.setVisibility(View.VISIBLE);
                    mBinder.layoutConnected.setVisibility(View.GONE);
                    break;
                case BOUND_CONNECTED:
                    mBinder.imgBlue.setImageResource(R.mipmap.ic_warm_online);
                    mBinder.txtName.setTextColor(0xFF1DE9B6);
                    mBinder.txtMac.setTextColor(0xFF1DE9B6);
                    mBinder.layoutIdle.setVisibility(View.GONE);
                    mBinder.layoutConnected.setVisibility(View.VISIBLE);
                    mBinder.deviceState.setText(R.string.connected);
                    mBinder.deviceState.setTextColor(0xFF00796B);
                    mBinder.btnDisconnect.setVisibility(View.VISIBLE);
                    mBinder.btnDetail.setVisibility(View.VISIBLE);
                    break;
                case BOUND_DISCONNECTED:
                    mBinder.imgBlue.setImageResource(R.mipmap.ic_warm_offline);
                    mBinder.txtName.setTextColor(0xFF000000);
                    mBinder.txtMac.setTextColor(0xFF000000);
                    mBinder.layoutIdle.setVisibility(View.GONE);
                    mBinder.layoutConnected.setVisibility(View.VISIBLE);
                    mBinder.deviceState.setText(R.string.disconnect);
                    mBinder.deviceState.setTextColor(0xFFFF5252);
                    mBinder.btnDisconnect.setVisibility(View.GONE);
                    mBinder.btnDetail.setVisibility(View.GONE);
                    break;
            }
        }

        mBinder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConnect(bleDevice);
                }
            }
        });

        mBinder.btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDisConnect(bleDevice);
                }
            }
        });

        mBinder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDetail(bleDevice);
                }
            }
        });


        return view; // if null, java.lang.NullPointerException: Attempt to invoke virtual method 'int android.view.View.getImportantForAccessibility()' on a null object reference
    }


    /**
     * this is how we use view binding in adapter view holder.
     */
    class ViewHolder {
        AdapterDeviceBinding mBinder;
//        ViewHolder(@NonNull View itemView) {
//            mBinder = AdapterDeviceBinding.bind(itemView);
//        }

        public ViewHolder(AdapterDeviceBinding mBinder) {
            this.mBinder = mBinder;
        }
    }
}
