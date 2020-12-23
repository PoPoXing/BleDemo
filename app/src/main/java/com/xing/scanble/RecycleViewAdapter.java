package com.xing.scanble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class RecycleViewAdapter extends RecyclerView.Adapter {
    private LayoutInflater mInflater;
    ArrayList<HashMap<String,Object>> mListItem;
    ScrlViewItemClickListener mItemListener;

    public RecycleViewAdapter(Context context, ArrayList<HashMap<String,Object>> listItem) {
        mInflater = LayoutInflater.from(context);
        mListItem = listItem;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mItemImage;
        TextView mItemTitle, mItemText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemImage = (ImageView) itemView.findViewById(R.id.item_image);
            mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
            mItemText = (TextView) itemView.findViewById(R.id.item_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mItemListener != null) {
                        mItemListener.onItemClick(view, getAdapterPosition());//(Integer)view.getTag());
                    }

                }
            });
        }

        public TextView getItemTitle()  { return mItemTitle; }
        public TextView getItemText()  { return mItemText; }
        public ImageView getItemImage()  { return mItemImage; }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vHolder = (ViewHolder) holder;
        vHolder.mItemTitle.setText((String) mListItem.get(position).get("ItemTitle"));
        vHolder.mItemText.setText((String) mListItem.get(position).get("ItemText"));
        vHolder.mItemImage.setImageResource((Integer) mListItem.get(position).get("ItemImage"));
        vHolder.mItemTitle.setTag(position);
        vHolder.mItemText.setTag(position);
        vHolder.mItemImage.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    public void setOnItemClickListener(ScrlViewItemClickListener listener) {
        mItemListener = listener;
    }//绑定MainActivity传进来的点击监听器
}
