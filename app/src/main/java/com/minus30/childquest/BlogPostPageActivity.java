package com.minus30.childquest;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minus30.childquest.R;

import java.io.IOException;

public class BlogPostPageActivity extends MenuActivity{
    final String LOG_TAG = "myLogs";
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_page);

        int defaultKeyValue = 0;
        int postId = getIntent().getIntExtra("postId", defaultKeyValue);

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
        Cursor cursor = mDb.rawQuery("SELECT * FROM blogPosts WHERE id = ?", new String[]{String.valueOf(postId)});
        cursor.moveToFirst();

        TextView postTitle = (TextView)findViewById(R.id.singlePostTitle);
        TextView postCaption = (TextView)findViewById(R.id.singlePostContent);
        ImageView postImage = (ImageView)findViewById(R.id.singlePostImage);


        postTitle.setText(cursor.getString(1));

        postCaption.setText(Html.fromHtml(cursor.getString(3)), TextView.BufferType.SPANNABLE);

        byte[] byteArray = Base64.decode(cursor.getBlob(4), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
        postImage.setImageBitmap(bm);


        cursor.close();

        addBottomMenu("blog");
    }

    public void backButtonAction(View view) {
        supportFinishAfterTransition();
    }
}
