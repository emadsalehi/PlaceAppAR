package com.example.placearapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.placearapp.R;

import java.util.List;


public class ShopItemsAdapter extends RecyclerView.Adapter<ShopItemsAdapter.MyViewHolder> {
    public Context mContext;
    public List<ShopItem> shopItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, details;
        public ImageView thumbnail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            details = (TextView) itemView.findViewById(R.id.details);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    public ShopItemsAdapter(Context mContext, List<ShopItem> shopItemList) {
        this.mContext = mContext;
        this.shopItemList = shopItemList;
    }

    @NonNull
    @Override
    public ShopItemsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.shopitem_card, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopItemsAdapter.MyViewHolder myViewHolder, int i) {
        ShopItem shopItem = shopItemList.get(i);
        myViewHolder.title.setText(shopItem.getName());
        myViewHolder.details.setText(shopItem.getDetails());
        Glide.with(mContext).load(shopItem.getThumbnail()).into(myViewHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return shopItemList.size();
    }
}
