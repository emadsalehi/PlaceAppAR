package com.example.placearapp.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.placearapp.R;
import com.example.placearapp.fragment.ProductPageFragment;

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

        ShopItem shopItem = shopItemList.get(i);

        MyViewHolder myViewHolder = new MyViewHolder(itemView);

        itemView.setOnClickListener(view -> {
            Log.i("itemView", "called");
            ProductPageFragment productPageFragment = ProductPageFragment.newInstance
                    (String.valueOf(shopItem.getThumbnail()),shopItem.getName());
            replaceFragment(productPageFragment);
        });

        return myViewHolder;
    }

    public void replaceFragment(Fragment destFragment)
    {
        Log.i("replace", "called");
        // First get FragmentManager object.
        FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
        List<Fragment> allFramgents = fragmentManager.getFragments();

        for (Fragment fragment : allFramgents) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(R.id.hf, destFragment);

        // Commit the Fragment replace action.
        fragmentTransaction.commit();
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
