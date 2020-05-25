package me.violinsolo.boman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.violinsolo.boman.databinding.AdapterRvListDeviceItemBinding;
import me.violinsolo.boman.listener.OnRecyclerViewItemClick;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/21 4:05 PM
 * @updateAt 2020/5/21 4:05 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private List<BleDevice> mData;
    private Context mContext;
    private OnRecyclerViewItemClick onRecyclerViewItemClick;

    public DeviceListAdapter(Context mContext) {
        this.mData = new ArrayList<>();
        this.mContext = mContext;
    }

    public void setOnRecyclerViewItemClick(OnRecyclerViewItemClick onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterRvListDeviceItemBinding mBinder = AdapterRvListDeviceItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        ViewHolder vh = new ViewHolder(mBinder);
        mBinder.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerViewItemClick != null) {
                    onRecyclerViewItemClick.onItemClick(view, (int) view.getTag());
                }
            }
        });
        return vh;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BleDevice bleDevice = mData.get(position);

        holder.tvDeviceName.setText(bleDevice.getName());
        holder.itemView.setTag(position);  // used on itemClickListener

//        holder.ivDeviceIcon
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDeviceIcon;
        private TextView tvDeviceName;
        private TextView tvDeviceStatus;

        public ViewHolder(AdapterRvListDeviceItemBinding mBinder) {
            super(mBinder.getRoot());

            ivDeviceIcon = mBinder.ivDeviceIcon;
            tvDeviceName = mBinder.tvDeviceName;
            tvDeviceStatus = mBinder.tvDeviceStatus;
        }
    }

    public BleDevice getItem(int position) {
        if (position <0 || position>=mData.size())
            throw new IllegalArgumentException("BleDevice index out of range.");
        return mData.get(position);
    }

    public void addDevice(BleDevice device){
        removeDevice(device);
        mData.add(device);
    }

    public void removeDevice(BleDevice device) {
        for (int i = 0; i < mData.size(); i++) {
            BleDevice t = mData.get(i);

            if (t.getKey().equals(device.getKey())) {
                mData.remove(i);

                break;
            }
        }
    }
}
