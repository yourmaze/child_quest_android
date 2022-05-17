package com.minus30.childquest;

import android.app.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class Billing {

    private static BillingClient billingClient;

    private Billing() {

    }


    public static void setConnectionToPlayMarketShop(Activity activity, String purchaseSku) {
        // слушатель изменений
        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
                Log.d("MyLogsBuy", "onPurchasesUpdated " + purchases);

                // если пришел ответ, что покупка совершилась
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        Log.d("MyLogsBuy", "Покупка совершилась " + purchase.getSku() + " " + billingResult.getResponseCode());
                        String questSku = purchase.getSku();

                        // Если мы на активити StoriesPage то возвращаем на StoriesPage, иначе на ShopActivity
                        if(savePurchase(activity, questSku)){
                            activity.finish();
                            Intent intent;
                            intent = new Intent(activity, StoryPageActivity.class);
                            int questId = ShopItem.getQuestIdFromSku(activity, questSku);
                            intent.putExtra("storyId", questId);
                            activity.startActivity(intent);
                        }
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    Log.d("MyLogsBuy", "Отклонено пользователем " + billingResult.getResponseCode());
                } else {
                    // Handle any other error codes.
                    Log.d("MyLogsBuy", "Ошибка покупки " + billingResult.getResponseCode());
                }

            }
        };

        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();


        billingClient.startConnection(new BillingClientStateListener() {
            // после подключения вызывается функция onBillingSetupFinished,
            // если проблемы с соединением, то onBillingServiceDisconnected
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // если подключение успешно
                    // получаем все покупки пользователя
                    getUserPurchase();
                    //Совершаем покупку
                    purchaseQuest(purchaseSku);
                }
            }

            // получаем все покупки пользователя
            private void getUserPurchase() {
                Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // запрос выполнен успешно - делаем обработку
                    List<Purchase> purchases = purchasesResult.getPurchasesList();
                    for (Purchase p : purchases) {
                        String sku = p.getSku();
                        Log.d("MyLogsBuy", "Куплены товары " + sku);
                    }
                }
            }

            private void purchaseQuest(String purchaseSku) {
                List<String> skuList = new ArrayList<>();
                skuList.add(purchaseSku);
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);


                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult billingResult,
                                                             List<SkuDetails> skuDetailsList) {
                                for (SkuDetails object : skuDetailsList) {
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(object)
                                            .build();
                                    int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
                                }
                            }
                        });
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("MyLogsBuy", "onBillingServiceDisconnected");
            }
        });

    }

    // сохраняем покупки
    // подключаемся к базе данных получаем id квеста, связанного со sku продукта
    // подключаемся к базе данных пользователя и сохраняем в покупки id купленного квеста
    public static boolean savePurchase(Activity activity, String purchaseSku) {
        DatabaseHelper mDBHelper;
        SQLiteDatabase mDb;
        UserDatabaseHelper userDBHelper;
        SQLiteDatabase userDb;
        // connect to Database
        mDBHelper = new DatabaseHelper(activity);
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

        // получаем id квеста
        Cursor cursor = mDb.rawQuery("SELECT * FROM game_shop WHERE sku = ?", new String[]{purchaseSku});
        cursor.moveToFirst();
        int purchaseQuestId = cursor.getInt(6);
        //Log.d("MyLogs", "" + cursor.getString(6));
        cursor.close();


        // connect to User Database
        userDBHelper = new UserDatabaseHelper(activity);
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


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("quest_id", purchaseQuestId);

        // Insert the new row, returning the primary key value of the new row
        userDb.insert("purchases", null, values);

        return true;
    }

}