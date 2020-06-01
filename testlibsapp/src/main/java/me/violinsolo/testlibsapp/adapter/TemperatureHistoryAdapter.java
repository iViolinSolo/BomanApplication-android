package me.violinsolo.testlibsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import me.violinsolo.testlibsapp.R;
import me.violinsolo.testlibsapp.adapter.event.Extension;
import me.violinsolo.testlibsapp.databinding.AdapterRvTemperatureHistoryGroupTitleItemBinding;
import me.violinsolo.testlibsapp.databinding.AdapterRvTemperatureHistoryRecordItemBinding;
import me.violinsolo.testlibsapp.listener.OnRecyclerViewItemClickListener;
import me.violinsolo.testlibsapp.model.TemperatureRecord;
import me.violinsolo.testlibsapp.util.Config;
import me.violinsolo.testlibsapp.util.Intermediate;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/29 3:30 PM
 * @updateAt 2020/5/29 3:30 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class TemperatureHistoryAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final String TAG = TemperatureHistoryAdapter.class.getSimpleName();

    public static final int VIEW_TYPE_GROUP_TITLE = 0;
    public static final int VIEW_TYPE_TEMPERATURE_RECORD = 1;
    public static final int VIEW_TYPE_UNKNOWN = -1;

    private Context mContext;
    private List<Object> mData;  // Object contains String and TemperatureRecord.
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;


    public TemperatureHistoryAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    // Attention, the order of mData will be reversed when we present the data.
    public synchronized void addRecord(TemperatureRecord record) {
        long recordTime = record.getTimestamp();

        if (mData.isEmpty()) {
            mData.add(record);
            String currentDay = record.getCurrentDay();
            mData.add(currentDay);

            notifyDataSetChanged();
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
                notifyDataSetChanged();
            }else {
                // 最后一个不是string，就强行帮他维护一个
                mData.add(record);
                String currentDay = record.getCurrentDay();
                mData.add(currentDay);

                notifyDataSetChanged();
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GROUP_TITLE) {
            AdapterRvTemperatureHistoryGroupTitleItemBinding mBinder = AdapterRvTemperatureHistoryGroupTitleItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
            GroupTitleViewHolder vh = new GroupTitleViewHolder(mBinder);

            return vh;
        }else if (viewType == VIEW_TYPE_TEMPERATURE_RECORD) {
            AdapterRvTemperatureHistoryRecordItemBinding mBinder = AdapterRvTemperatureHistoryRecordItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
            TemperatureRecordViewHolder vh = new TemperatureRecordViewHolder(mBinder);

            // only this type is clickable
            mBinder.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRecyclerViewItemClickListener != null) {
                        onRecyclerViewItemClickListener.onItemClick(view, (int) view.getTag());
                    }
                }
            });
            return vh;
        }else if (viewType == VIEW_TYPE_UNKNOWN){

            return null;
        }
        return null;
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     *
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        Object obj = mData.get(mData.size() - position - 1);
        if (obj instanceof String) {
            return VIEW_TYPE_GROUP_TITLE;
        }else if (obj instanceof TemperatureRecord) {
            return VIEW_TYPE_TEMPERATURE_RECORD;
        }else {
            return VIEW_TYPE_UNKNOWN;
        }
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
        // get plain data..
        Object obj = mData.get(mData.size() - position - 1);

        if (obj instanceof String) {
            // get data..
            String groupTitle = (String) obj;

            if (holder instanceof GroupTitleViewHolder) {
                GroupTitleViewHolder gtvh = (GroupTitleViewHolder) holder;

                gtvh.tvGroupTitle.setText(groupTitle);

                gtvh.itemView.setTag(position);
            }else {
                throw new RuntimeException("RecyclerView ViewHolder's type does not match Data's type: data:"+groupTitle+" position:"+position);
            }
        }else if (obj instanceof TemperatureRecord) {
            // get data..
            TemperatureRecord record = (TemperatureRecord) obj;

            if (holder instanceof TemperatureRecordViewHolder) {
                TemperatureRecordViewHolder trvh = (TemperatureRecordViewHolder) holder;

                trvh.tvTime.setText(record.getCurrentSimpleTime());
                // TODO the convertion between Celsius and Fahrenheit need to be added.
                int temperatureValue = record.getValue();
                float trueValue = temperatureValue/100f;
                trvh.tvTemperatureValue.setText(String.format("%s", trueValue));
                String tSymbol = Intermediate.getInstance().isCelsius? "℃": "℉";
                trvh.tvTemperatureSymbol.setText(tSymbol);
                trvh.tvTemperatureNotify.setText(temperatureValue > Config.temperatureThreshold? "体温偏高":"体温正常");
                int tColor = mContext.getResources().getColor(temperatureValue > Config.temperatureThreshold? R.color.colorRed:R.color.colorBlack);
                trvh.tvTemperatureNotify.setTextColor(tColor);

                trvh.itemView.setTag(position);

                // now we set the background of it.
                // some sort of intricate.
                // 每个都要判断一下前面的元素和后面的元素，才能决定自己应该是四种背景布局中的哪一种。


                // in this mData array list, data are stored reversely.
                // ----------------------------------------------------
                // mData:
                //
                // ... old records, old record group title ------> new records, new record group title ...
                // ----------------------------------------------------
                //
                // ----------------------------------------------------
                // UI:
                //
                // new group title          --> small position, big array index
                // new records
                // ...
                // old group title
                // old records              --> big position, small array index
                // ----------------------------------------------------

                // ... String, TemperatureRecord, [current position] , TemperatureRecord, String ...
                // ... ------, prevIdx, curIdx, formIdx, -----, ...
                //
                // ...
                // formIdx
                // curIdx
                // prevIdx
                // ...

                int curIdx = mData.size() - 1 - position;
                int prevIdx = curIdx -1; // here word "previous" means the previous data, older data.
                int formIdx = curIdx +1; // here word "former" means the former data, newer data.
                int bgxml = R.drawable.temperature_record_all_corner; // default bgxml
                if (prevIdx < 0 || mData.get(prevIdx) instanceof String) {
                    //  the bottom of the background xml should be round corners.
                    if (mData.get(formIdx) instanceof String) {
                        //  the top of the background xml should be round corners.
                        bgxml = R.drawable.temperature_record_all_corner;
                    }else if (mData.get(formIdx) instanceof TemperatureRecord) {
                        //  the top of the background xml should be NO corners.
                        bgxml = R.drawable.temperature_record_lower_corner;
                    }else {
                        // never come into this area.
                    }
                }else if (mData.get(prevIdx) instanceof TemperatureRecord) {
                    //  the bottom of the background xml should be NO corners.
                    if (mData.get(formIdx) instanceof String) {
                        //  the top of the background xml should be round corners.
                        bgxml = R.drawable.temperature_record_upper_corner;

                    }else if (mData.get(formIdx) instanceof TemperatureRecord) {
                        //  the top of the background xml should be NO corners.
                        bgxml = R.drawable.temperature_record_no_corner;
                    }else {
                        // never come into this area.
                    }
                }else {
                    // never come into this area.
                }

                trvh.clViewRoot.setBackgroundResource(bgxml);

//                if (prevIdx<0) { // TODO we will fix this bug in the future...
//                    // curIdx == size-1
//                    // 当前元素是最后一个元素
//                    Log.e(TAG, "Last element, custom layout params");
//
////                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
//                    ViewGroup.LayoutParams layoutParams = trvh.clViewRoot.getLayoutParams();
//                    layoutParams.;//4个参数按顺序分别是左上右下
//                    trvh.clViewRoot.setLayoutParams(layoutParams);
//                }
            }else {
                throw new RuntimeException("RecyclerView ViewHolder's type does not match Data's type: data:"+record+" position:"+position);
            }
        }else {
            throw new RuntimeException("RecyclerView Data's type is unknown. position:"+position);
        }
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

    public static class TemperatureRecordViewHolder extends ViewHolder implements Extension {
        private ConstraintLayout clViewRoot;
        private TextView tvTime;
        private TextView tvTemperatureValue;
        private TextView tvTemperatureSymbol;
        private TextView tvTemperatureNotify;

        public TemperatureRecordViewHolder(AdapterRvTemperatureHistoryRecordItemBinding mBinder) {
            super(mBinder.getRoot());

            clViewRoot = mBinder.clTemperatureRecordRoot;
            tvTime = mBinder.tvTime;
            tvTemperatureValue = mBinder.tvTemperatureValue;
            tvTemperatureSymbol = mBinder.tvTemperatureSymbol;
            tvTemperatureNotify = mBinder.tvTemperatureNotify;
        }

        @Override
        public float getActionWidth() {
            return clViewRoot.getWidth();
        }
    }

    public static class  GroupTitleViewHolder extends ViewHolder implements Extension {
        private TextView tvGroupTitle;

        public GroupTitleViewHolder(AdapterRvTemperatureHistoryGroupTitleItemBinding mBiner) {
            super(mBiner.getRoot());

            tvGroupTitle = mBiner.tvGroupTitle;
        }

        @Override
        public float getActionWidth() {
            return NO_AVAILABLE;
        }
    }
}
