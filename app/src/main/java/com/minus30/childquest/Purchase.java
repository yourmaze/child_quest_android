package com.minus30.childquest;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

class Purchase {

    public static boolean userHaveQuest(Context context, int quest_id){
        UserDatabaseHelper mDBHelper;
        SQLiteDatabase mDb;

        mDBHelper = new UserDatabaseHelper(context);
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

        Cursor cursor = mDb.rawQuery("SELECT * FROM purchases WHERE quest_id = ?", new String[]{String.valueOf(quest_id)});
        cursor.moveToFirst();

        if (cursor != null && cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}


