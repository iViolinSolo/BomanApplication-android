package me.violinsolo.boman.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import me.violinsolo.boman.databinding.AdapterRvTemperatureHistoryGroupTitleItemBinding;
import me.violinsolo.boman.databinding.AdapterRvTemperatureHistoryRecordItemBinding;
import me.violinsolo.boman.model.TemperatureRecord;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/29 3:30 PM
 * @updateAt 2020/5/29 3:30 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class TemperatureHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Object> mData;  // Object contains String and TemperatureRecord.


    public TemperatureHistoryAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
    }

    // Attention, the order of mData will be reversed when we present the data.
    public synchronized void addRecord(TemperatureRecord record) {
        long recordTime = record.getTimestamp();

        if (mData.isEmpty()) {
            mData.add(record);
            String currentDay = record.getCurrentDay();
            mData.add(currentDay);
        }else {
            int lastIdx = mData.size()-1;
            Object last = mData.get(lastIdx);
            if (last instanceof String) {
                String strLast = (String) last;
                String strRecord = record.getCurrentDay();

                if (strLast.equals(strRecord)) {
                    mData.remove(lastIdx);
                }
                mData.add(record);
                mData.add(strRecord);
                // 这里是强行把两个操作合并在一起了，
                // 如果record的时间是strLast，去除这个标记，然后append数据，加入新标签，其实就是老标签
                // 如果record的时间不是strLast，相当于是个新的数据，这个时候需要直接append数据，然后加入新标签。

                // TODO: in the future, update this method so that you could insert a random record which can not guarantee the data sequence that conforms to date.
            }else {
                // 最后一个不是string，就强行帮他维护一个
                mData.add(record);
                String currentDay = record.getCurrentDay();
                mData.add(currentDay);
            }
        }
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return null;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    public static class TemperatureRecordViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private TextView tvTemperatureValue;
        private TextView tvTemperatureSymbol;
        private TextView tvTemperatureNotify;

        public TemperatureRecordViewHolder(AdapterRvTemperatureHistoryRecordItemBinding mBinder) {
            super(mBinder.getRoot());

            tvTime = mBinder.tvTime;
            tvTemperatureValue = mBinder.tvTemperatureValue;
            tvTemperatureSymbol = mBinder.tvTemperatureSymbol;
            tvTemperatureNotify = mBinder.tvTemperatureNotify;
        }
    }

    public static class  GroupTitleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupTitle;

        public GroupTitleViewHolder(AdapterRvTemperatureHistoryGroupTitleItemBinding mBiner) {
            super(mBiner.getRoot());

            tvGroupTitle = mBiner.tvGroupTitle;
        }
    }
}
