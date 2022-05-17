package com.minus30.childquest;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StoryPageActivity extends MenuActivity {
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    TextView scenarioDescription;
    TextView showMoreLink;
    View activityStoryPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_page);

        activityStoryPage = findViewById(R.id.activityStoryPage);
        scenarioDescription = (TextView)findViewById(R.id.scenarioDescription);
        showMoreLink = (TextView)findViewById(R.id.showMoreLink);

        int defaultKeyValue = 0;
        int storyId = getIntent().getIntExtra("storyId", defaultKeyValue);

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
        Cursor cursor = mDb.rawQuery("SELECT * FROM stories WHERE id = ?", new String[]{String.valueOf(storyId)});
        cursor.moveToFirst();

        TextView scenarioName = (TextView)findViewById(R.id.scenarioName);
        TextView scenarioPrice = (TextView)findViewById(R.id.scenarioPrice);
        TextView scenarioDuration = (TextView)findViewById(R.id.scenarioDuration);
        TextView scenarioRating = (TextView)findViewById(R.id.scenarioRating);
        ImageView scenarioImage = (ImageView)findViewById(R.id.scenarioImage);
        Button scenarioButton = (Button)findViewById(R.id.scenarioInfoLink);

        if(cursor.getInt(5) != 0 && !Purchase.userHaveQuest(this, storyId)){
            scenarioButton.setText("Купить за " + cursor.getInt(5) + " руб");
        } else {
            scenarioButton.setText("Смотреть");
        }
        scenarioButton.setTag(cursor.getInt(0));
        scenarioName.setText(cursor.getString(1));

        if(Purchase.userHaveQuest(this, storyId)) {
            scenarioPrice.setText("Куплено");
        } else {
            scenarioPrice.setText(StoryHelper.NumberToPriceText(cursor.getInt(5)));
        }


        scenarioDuration.setText(cursor.getString(6));
        scenarioRating.setText(cursor.getString(4));
        scenarioDescription.setText(cursor.getString(10));

        byte[] byteArray = Base64.decode(cursor.getBlob(3), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
        scenarioImage.setImageBitmap(bm);

        cursor.close();

        addBottomMenu("game");
    }


    public void ShowMoreClick(View v) {

        if (showMoreLink.getText().toString().equalsIgnoreCase("Показать полностью"))
        {
            scenarioDescription.setMaxLines(999);//your TextView
            showMoreLink.setText("Свернуть");
        }
        else
        {
            scenarioDescription.setMaxLines(3);//your TextView
            showMoreLink.setText("Показать полностью");
        }
    }

    // Переходим внутрь истории
    // Проверяем есть ли файлы контента, которые нужны, если да то продолжаем, иначе скачиваем и продолжаем
    public void goToStoryInfo(View v) {
        int questId = (Integer) v.getTag();

        Cursor cursor = mDb.rawQuery("SELECT * FROM stories WHERE id = ?", new String[]{String.valueOf(questId)});
        cursor.moveToFirst();
        int price = cursor.getInt(5);
        // если товар бесплатный или уже куплен, то отправляем на скачивание и игру
        // иначе даем пользователю оплатить квест
        if( price != 0 && !Purchase.userHaveQuest(this, questId)){
            // получаем sku квеста и даем оплатить квест
            ShopItem shopItem = new ShopItem();
            shopItem.getShopItemFromQuestId(this, questId);
            Billing.setConnectionToPlayMarketShop(this, shopItem.getSku());
        } else {
            if(checkQuestFilesExist(questId) == true){
                Intent intent;
                intent = new Intent(this, StoryInfoActivity.class);
                intent.putExtra("storyId", questId);

                View scenarioImage = findViewById(R.id.scenarioImage);
                View scenarioRating = findViewById(R.id.scenarioRating);
                View scenarioDuration = findViewById(R.id.scenarioDuration);
                Pair<View, String> p1 = Pair.create(scenarioImage, "scenarioImage");
                Pair<View, String> p2 = Pair.create(scenarioRating, "scenarioRating");
                Pair<View, String> p3 = Pair.create(scenarioDuration, "scenarioDuration");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3);
                startActivity(intent, options.toBundle());


            } else {
                downloadQuestFiles(questId);
            }
        }
    }

    public void backButtonAction(View view) {
        supportFinishAfterTransition();
        //this.finish();
    }

    // проверяем существование файлов квеста,
    // если их нет, то возвращаем false, иначе true
    public boolean checkQuestFilesExist(int storyId) {
        String query = "SELECT * FROM images WHERE quest_id = " + storyId + " UNION ALL SELECT * FROM audio WHERE quest_id = " + storyId;
        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            // если файл не существует
            if(!checkFileExist(cursor.getString(1))) {
                return false;
            }
            cursor.moveToNext();
        }
        cursor.close();

        return true;
    }

    public boolean checkFileExist(String fileName) {
        File f = new File(Data.getSaveDir(this) + fileName);
        return f.exists();
    }


    public void downloadQuestFiles(int storyId) {
        Intent intent;
        intent = new Intent(this, SingleDownloadActivity.class);
        intent.putExtra("questId", storyId);
        startActivity(intent);
    }

}
