package com.minus30.childquest;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlogPageActivity extends MenuActivity{

    private List<BlogPost> posts;
    private RecyclerView rv;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_page);

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

        addBottomMenu("blog");

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        initializeData();
        initializeAdapter();
    }


    public void goToBlogPost(View v) {
        BlogPost person = (BlogPost)v.getTag();

        View blogPostImage = v.findViewById(R.id.blogPostImage);

        Intent intent = new Intent(this, BlogPostPageActivity.class);
        intent.putExtra("postId", person.postId);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, blogPostImage, "blogImage");
        startActivity(intent, options.toBundle());
    }

    private void initializeData(){
        posts = new ArrayList<>();

        Cursor cursor = mDb.rawQuery("SELECT * FROM blogPosts", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BlogPost post = new BlogPost();
            byte[] byteArray = Base64.decode(cursor.getBlob(4), Base64.DEFAULT);
            Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);

            posts.add(post.setBlogPost(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), bm));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void initializeAdapter(){
        BlogRVAdapter adapter = new BlogRVAdapter(posts);
        rv.setAdapter(adapter);
    }
}