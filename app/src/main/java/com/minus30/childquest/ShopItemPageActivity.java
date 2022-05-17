package com.minus30.childquest;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShopItemPageActivity extends MenuActivity{
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_item_page);


        int defaultKeyValue = 0;
        int shopId = getIntent().getIntExtra("shopId", defaultKeyValue);

        ShopItem shopItem = new ShopItem();
        shopItem.getShopItemFromShopId(this, shopId);

        Log.d("MyLogs", "shopItem " + shopItem);

        TextView shopItemTitle = (TextView)findViewById(R.id.shopItemTitle);
        TextView shopItemContent = (TextView)findViewById(R.id.shopItemContent);
        Button buyButton = (Button)findViewById(R.id.buyButton);

        shopItemTitle.setText(shopItem.getName());
        shopItemContent.setText(shopItem.getDescription());
        String buyButtonText = getString(R.string.shop_item_buy_for) + " " + shopItem.getPrice() + " ₽";
        buyButton.setText(buyButtonText);
        buyButton.setTag(shopItem.getSku());

        addBottomMenu("shop");
    }

    public void goToPurchase(View view) {
        String purchaseSku =  (String) view.getTag();
        Log.d("MyLogs", "qwe" + purchaseSku);
        Billing.setConnectionToPlayMarketShop(this, purchaseSku);
    }

    public void backButtonAction(View view) {
        this.finish();
    }
}