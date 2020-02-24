package io.agora.vlive.ui.actionsheets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

import io.agora.vlive.R;
import io.agora.vlive.utils.Global;

public class GiftActionSheet extends AbstractActionSheet implements View.OnClickListener {
    public interface GiftActionSheetListener {
        void onGiftSend(String name, String path, int value);
    }

    private static final int SPAN_COUNT = 4;
    private static final String ASSET_PREFIX = "gifts";

    private GiftActionSheetListener mListener;
    private RecyclerView mRecycler;
    private String[] mGiftNames;
    private String mValueFormat;
    private int mSelected = -1;

    public GiftActionSheet(Context context) {
        super(context);
        init();
    }

    public GiftActionSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftActionSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.action_gift, this, true);
        mRecycler = findViewById(R.id.live_room_action_sheet_gift_recycler);
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
        mRecycler.setAdapter(new GiftAdapter());
        mGiftNames = getResources().getStringArray(R.array.gift_names);
        mValueFormat = getResources().getString(R.string.live_room_gift_action_sheet_value_format);

        AppCompatTextView sendBtn = findViewById(R.id.live_room_action_sheet_gift_send_btn);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.live_room_action_sheet_gift_send_btn) {
            if (mListener != null && mSelected != -1) mListener.onGiftSend(
                    mGiftNames[mSelected],
                    Global.FakeData.GIFT_FILES[mSelected],
                    Global.FakeData.GIFT_VALUES[mSelected]);
        }
    }

    private class GiftAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GiftViewHolder(LayoutInflater.from(getContext()).
                    inflate(R.layout.action_gift_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GiftViewHolder giftViewHolder = (GiftViewHolder) holder;
            giftViewHolder.name.setText(mGiftNames[position]);
            giftViewHolder.value.setText(String.format(
                    mValueFormat, Global.FakeData.GIFT_VALUES[position]));
            giftViewHolder.setPosition(position);
            giftViewHolder.itemView.setActivated(mSelected == position);

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContext().getAssets().
                        open(ASSET_PREFIX + File.separator +
                                Global.FakeData.GIFT_FILES[position]));
                giftViewHolder.icon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return Global.FakeData.GIFT_FILES.length;
        }
    }

    private class GiftViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView icon;
        AppCompatTextView name;
        AppCompatTextView value;
        int position;

        GiftViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.live_room_action_sheet_gift_item_icon);
            name = itemView.findViewById(R.id.live_room_action_sheet_gift_item_name);
            value = itemView.findViewById(R.id.live_room_action_sheet_gift_item_value);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSelected = position;
                    mRecycler.getAdapter().notifyDataSetChanged();
                }
            });
        }

        void setPosition(int position) {
            this.position = position;
        }
    }

    @Override
    public void setActionSheetListener(AbsActionSheetListener listener) {
        if (listener instanceof GiftActionSheetListener) {
            mListener = (GiftActionSheetListener) listener;
        }
    }
}
