package com.minus30.childquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minus30.childquest.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MenuActivity{
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private List<Story> stories;

    final String FIRST_START = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBottomMenu("game");

        // проверка первый ли раз запускается приложение,
        // если да, то показываем App Intro
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean(FIRST_START, true);

                if (isFirstStart) {
                    Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(intent);

                }
            }
        });
        // Start the thread
        t.start();


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

        // create List of Recycle Views
        ArrayList<RecyclerView> rvList = new ArrayList<RecyclerView>();
        rvList.add((RecyclerView)findViewById(R.id.rv1));
        rvList.add((RecyclerView)findViewById(R.id.rv2));
        rvList.add((RecyclerView)findViewById(R.id.rv3));
        //rvList.add((RecyclerView)findViewById(R.id.rv4));

        // create List of category titles
        ArrayList<TextView> rvCategoriesTitles = new ArrayList<TextView>();
        rvCategoriesTitles.add((TextView)findViewById(R.id.categoryName1));
        rvCategoriesTitles.add((TextView)findViewById(R.id.categoryName2));
        rvCategoriesTitles.add((TextView)findViewById(R.id.categoryName3));
        //rvCategoriesTitles.add((TextView)findViewById(R.id.categoryName4));

        // create List of category links
        ArrayList<TextView> rvCategoriesLinks = new ArrayList<TextView>();
        rvCategoriesLinks.add((TextView)findViewById(R.id.categoryLink1));
        rvCategoriesLinks.add((TextView)findViewById(R.id.categoryLink2));
        rvCategoriesLinks.add((TextView)findViewById(R.id.categoryLink3));
        //rvCategoriesLinks.add((TextView)findViewById(R.id.categoryLink4));

        // get categories and send stories to different rv
        Cursor cursor = mDb.rawQuery("SELECT * FROM storiesCategory", null);
        cursor.moveToFirst();
        Integer i = 0;
        while (!cursor.isAfterLast()) {
            // set title and link of the category
            rvCategoriesTitles.get(i).setText(cursor.getString(1));
            rvCategoriesLinks.get(i).setTag(cursor.getInt(0));
            LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rvList.get(i).setLayoutManager(llm);
            initializeData(cursor.getString(2));
            initializeAdapter(rvList.get(i));
            cursor.moveToNext();
            i++;
        }
        cursor.close();
    }

    // onclick function for go to the story
    public void goToStory(View v) {

        /*View scenarioImage = v.findViewById(R.id.scenarioImage);

        Intent intent = new Intent(this, StoryPageActivity.class);
        intent.putExtra("storyId", (Integer) v.getTag());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, scenarioImage, "scenarioImage");
        startActivity(intent, options.toBundle());*/

        View scenarioImage = v.findViewById(R.id.scenarioImage);
        View scenarioRating = v.findViewById(R.id.scenarioRating);
        View scenarioPrice = v.findViewById(R.id.scenarioPrice);
        Pair<View, String> p1 = Pair.create(scenarioImage, "scenarioImage");
        Pair<View, String> p2 = Pair.create(scenarioRating, "scenarioRating");
        Pair<View, String> p3 = Pair.create(scenarioPrice, "scenarioPrice");

        Intent intent = new Intent(this, StoryPageActivity.class);
        intent.putExtra("storyId", (Integer) v.getTag());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3);
        startActivity(intent, options.toBundle());



    }

    public void crashTest(View v) {
        throw new RuntimeException("Test Crash"); // Force a crash
    }






    //onclick function for go to the story category
    public void goToStoryCategory(View v) {
        Intent intent;
        intent = new Intent(this, StoriesCategoryActivity.class);
        intent.putExtra("categoryId", (Integer) v.getTag());
        startActivity(intent);
    }

    // initialized data for our RecycleView adapter
    protected void initializeData(String storiesIds){
        stories = new ArrayList<>();
        String query = "SELECT * FROM stories WHERE id IN (" + storiesIds + ")";
        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Story story = new Story();
            byte[] byteArray = Base64.decode(cursor.getBlob(3), Base64.DEFAULT);
            Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);

            int price = Integer.parseInt(cursor.getString(5));
            if(Purchase.userHaveQuest(this, cursor.getInt(0))){
                price = -1;
            }

            stories.add(story.setStory(cursor.getInt(0), cursor.getString(1), bm, cursor.getString(4), price, cursor.getString(6)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    // start our Recycle View Adapter for show stories list
    protected void initializeAdapter(RecyclerView RVCategory){
        StoryRVAdapter adapter = new StoryRVAdapter(stories);
        RVCategory.setAdapter(adapter);
    }
}