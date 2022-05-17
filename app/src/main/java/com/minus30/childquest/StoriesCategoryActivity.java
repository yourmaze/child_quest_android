package com.minus30.childquest;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minus30.childquest.R;

import java.io.IOException;

public class StoriesCategoryActivity extends MainActivity{
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_category);

        addBottomMenu("game");

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

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        TextView categoryName = (TextView)findViewById(R.id.categoryName);

        // get category info
        Intent mIntent = getIntent();
        int categoryId = mIntent.getIntExtra("categoryId", 0);

        String query = "SELECT * FROM storiesCategory WHERE id = (" + categoryId + ")";

        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();

        // set title of the category
        categoryName.setText(cursor.getString(1));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(mLayoutManager);

        initializeData(cursor.getString(2));
        initializeAdapter(rv);
        cursor.close();
    }

    public void backButtonAction(View view) {
        this.finish();
    }
}