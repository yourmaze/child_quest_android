package com.minus30.childquest;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends MenuActivity{
    private List<ShopItem> shopItems;
    private RecyclerView rv;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        addBottomMenu("shop");

        // connect to Database
        mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        initializeData();
        initializeAdapter();
    }


    public void goToShopItemPage(View v) {
        Intent intent;
        intent = new Intent(this, ShopItemPageActivity.class);
        intent.putExtra("shopId", (Integer) v.getTag());
        startActivity(intent);
    }

    private void initializeData(){
        shopItems = new ArrayList<>();

        Cursor cursor = mDb.rawQuery("SELECT * FROM game_shop WHERE status = ?", new String[] {"1"});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(!Purchase.userHaveQuest(this, Integer.valueOf(cursor.getString(6)))){
                ShopItem shopItem = new ShopItem();
                shopItems.add(shopItem.setShopItem(cursor.getInt(0), cursor.getString(2), cursor.getString(3), cursor.getInt(5), cursor.getInt(7)));
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void initializeAdapter(){
        ShopRVAdapter adapter = new ShopRVAdapter(shopItems);
        rv.setAdapter(adapter);
    }

    public void clearUserPurchase(View view){
        UserDatabaseHelper userDBHelper;
        SQLiteDatabase userDb;
        // connect to User Database
        userDBHelper = new UserDatabaseHelper(this);
        try {
            userDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            userDb = userDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        userDb.delete("purchases", null, null);
    }

    public void showUserPurchase(View view){
        UserDatabaseHelper userDBHelper;
        SQLiteDatabase userDb;
        // connect to User Database
        userDBHelper = new UserDatabaseHelper(this);
        try {
            userDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            userDb = userDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        Log.d("MyLogs", "БД пуста, если дальше нет строк ");
        Cursor cursor = userDb.rawQuery("SELECT * FROM purchases", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d("MyLogs", "БД " + cursor.getInt(1));
            cursor.moveToNext();
        }
        cursor.close();
    }

}


