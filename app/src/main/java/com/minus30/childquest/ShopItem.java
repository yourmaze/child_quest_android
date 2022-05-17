package com.minus30.childquest;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

class ShopItem {
    int shopId;
    String sku;
    String name;
    String description;
    String excerpt;
    int price;
    String buy_info;
    int recurrent;
    int status;



    // в методе подключаемся к таблице game_shop и получаем все данные по id элемента
    public void getShopItemFromShopId(Context context, int shopId){
        DatabaseHelper mDBHelper;
        SQLiteDatabase mDb;

        mDBHelper = new DatabaseHelper(context);
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
        Cursor cursor = mDb.rawQuery("SELECT * FROM game_shop WHERE id = ?", new String[]{String.valueOf(shopId)});
        cursor.moveToFirst();

        this.shopId = shopId;
        this.sku = cursor.getString(1);
        this.name = cursor.getString(2);
        this.excerpt = cursor.getString(3);
        this.description = cursor.getString(4);
        this.price = cursor.getInt(5);
        this.buy_info = cursor.getString(6);
        this.recurrent = cursor.getInt(7);
        this.status = cursor.getInt(8);

        cursor.close();
    }


    // в методе подключаемся к таблице game_shop и получаем все данные по id элемента
    public void getShopItemFromQuestId(Context context, int shopId){
        DatabaseHelper mDBHelper;
        SQLiteDatabase mDb;

        mDBHelper = new DatabaseHelper(context);
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
        Cursor cursor = mDb.rawQuery("SELECT * FROM game_shop WHERE buy_info = ?", new String[]{String.valueOf(shopId)});
        cursor.moveToFirst();

        this.shopId = shopId;
        this.sku = cursor.getString(1);
        this.name = cursor.getString(2);
        this.excerpt = cursor.getString(3);
        this.description = cursor.getString(4);
        this.price = cursor.getInt(5);
        this.buy_info = cursor.getString(6);
        this.recurrent = cursor.getInt(7);
        this.status = cursor.getInt(8);

        cursor.close();
    }


    // в методе подключаемся к таблице game_shop и получаем все данные по id элемента
    public static int getQuestIdFromSku(Context context, String sku){
        DatabaseHelper mDBHelper;
        SQLiteDatabase mDb;

        mDBHelper = new DatabaseHelper(context);
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
        Cursor cursor = mDb.rawQuery("SELECT * FROM game_shop WHERE sku = ?", new String[]{sku});
        cursor.moveToFirst();

        return Integer.valueOf(cursor.getString(6));
    }



    public ShopItem setShopItem(int shopId, String name, String excerpt, int price, int recurrent){
        this.shopId = shopId;
        this.name = name;
        this.excerpt = excerpt;
        this.price = price;
        this.recurrent = recurrent;

        return this;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getBuy_info() {
        return buy_info;
    }

    public int getRecurrent() {
        return recurrent;
    }

    public int getStatus() {
        return status;
    }
}


