package com.minus30.childquest;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.io.IOException;


class Story {
    int storyId;
    String name;
    Bitmap photoBitmap;
    String rating;
    Integer price;
    String duration;

    public Story setStory(int storyId, String name, Bitmap photoBitmap, String rating, Integer price, String duration){
        this.storyId = storyId;
        this.name = name;
        this.photoBitmap = photoBitmap;
        this.rating = rating;
        this.price = price;
        this.duration = duration;

        return this;
    }
}


