package com.minus30.childquest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import com.google.android.material.snackbar.Snackbar;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

import java.util.ArrayList;
import java.util.List;


public final class Data {
    private Data() {

    }

    static String getDownloadUrl(int questId) {
        return "http://clients.minus-30.ru/quest" + questId + ".zip";
    }

    @NonNull
    static String getNameFromUrl(final String url) {
        return Uri.parse(url).getLastPathSegment();
    }


    @NonNull
    public static String getSaveDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
    }


    public static boolean hasConnection(final Context context, View mainView)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        Snackbar.make(mainView, "Ошибка! Нет интернет соединения.", Snackbar.LENGTH_INDEFINITE).show();
        return false;
    }

}