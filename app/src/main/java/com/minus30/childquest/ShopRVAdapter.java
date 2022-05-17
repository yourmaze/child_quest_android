package com.minus30.childquest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.minus30.childquest.R;

import java.util.List;

public class ShopRVAdapter extends RecyclerView.Adapter<ShopRVAdapter.ShopViewHolder> {

    public static class ShopViewHolder extends RecyclerView.ViewHolder  {

        CardView shopItem;
        TextView shopItemTitle;
        TextView shopItemExcerpt;
        TextView shopItemPrice;
        TextView shopItemRecurrent;

        ShopViewHolder(View itemView) {
            super(itemView);
            shopItem = (CardView)itemView.findViewById(R.id.shopItem);
            shopItemTitle = (TextView)itemView.findViewById(R.id.shopItemTitle);
            shopItemExcerpt = (TextView)itemView.findViewById(R.id.shopItemExcerpt);
            shopItemPrice = (TextView)itemView.findViewById(R.id.shopItemPrice);
            shopItemRecurrent = (TextView)itemView.findViewById(R.id.shopItemRecurrent);
        }
    }


    List<ShopItem> shopItems;

    ShopRVAdapter(List<ShopItem> shopItems){
        this.shopItems = shopItems;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_list_loop, viewGroup, false);
        ShopViewHolder pvh = new ShopViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(ShopViewHolder ShopViewHolder, int i) {
        ShopViewHolder.shopItem.setTag(shopItems.get(i).shopId);
        ShopViewHolder.shopItemTitle.setText(shopItems.get(i).name);
        ShopViewHolder.shopItemExcerpt.setText(shopItems.get(i).excerpt);
        ShopViewHolder.shopItemPrice.setText(StoryHelper.NumberToPriceText(shopItems.get(i).price));
    }

    @Override
    public int getItemCount() {
        return shopItems.size();
    }
}