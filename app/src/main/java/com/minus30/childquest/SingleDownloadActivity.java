package com.minus30.childquest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.HttpUrlConnectionDownloader;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Downloader;
import com.tonyodev.fetch2core.Extras;
import com.tonyodev.fetch2core.FetchObserver;
import com.tonyodev.fetch2core.Func;
import com.tonyodev.fetch2core.MutableExtras;
import com.tonyodev.fetch2core.Reason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.widget.Toast;

import java.io.File;
import java.util.List;

import cdflynn.android.library.checkview.CheckView;


public class SingleDownloadActivity extends AppCompatActivity implements FetchObserver<Download> {

    private static final int STORAGE_PERMISSION_CODE = 100;

    private View mainView;
    private TextView progressTextView, downloadTitle, downloadText;
    private Request request;
    private Fetch fetch;
    int questId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_download);
        mainView = findViewById(R.id.activity_single_download);
        progressTextView = findViewById(R.id.progressTextView);
        downloadTitle = findViewById(R.id.downloadTitle);
        downloadText = findViewById(R.id.downloadText);

        // проверяем есть ли интернет-соеднинение
        Data.hasConnection(this, mainView);

        questId = getIntent().getIntExtra("questId", 0);

        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(1)
                .setHttpDownloader(new HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL))
                .build();
        Fetch.Impl.setDefaultInstanceConfiguration(fetchConfiguration);


        fetch = Fetch.Impl.getDefaultInstance();
        checkStoragePermission();
    }

    private final FetchListener fetchListener = new AbstractFetchListener() {

        @Override
        public void onCompleted(@NotNull Download download) {
            progressTextView.setVisibility(View.GONE);
            downloadTitle.setText("Распаковываем контент");
            downloadText.setText("для шикарного развлечения");

            Log.d("MyLogs", "Error " + download.getStatus() + " " + download.getDownloaded() + " " + download.getFileUri() + " " + download.getTotal() + " " + download.getRequest());

            if(download.getError() == Error.NONE){

                decompressArchive(download.getFile());
            }
        }
    };



    @Override
    protected void onResume() {
        fetch.addListener(fetchListener);
        super.onResume();
        if (request != null) {
            fetch.attachFetchObserversForDownload(request.getId(), this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (request != null) {
            fetch.removeFetchObserversForDownload(request.getId(), this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fetch.close();
    }

    @Override
    public void onChanged(Download data, @NotNull Reason reason) {
        updateViews(data, reason);
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Log.d("asd ", "Error ");
            enqueueDownload();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("asd ", "Error ");
            enqueueDownload();
        } else {
            Snackbar.make(mainView, R.string.permission_not_enabled, Snackbar.LENGTH_LONG).show();
        }
    }

    private void enqueueDownload() {
        final String url = Data.getDownloadUrl(questId);

        final String filePath = Data.getSaveDir(this) + Data.getNameFromUrl(url);
        request = new Request(url, filePath);
        request.setExtras(getExtrasForRequest(request));

        fetch.attachFetchObserversForDownload(request.getId(), this)
                .enqueue(request, new Func<Request>() {
                    @Override
                    public void call(@NotNull Request result) {
                        request = result;
                    }
                }, new Func<Error>() {
                    @Override
                    public void call(@NotNull Error result) {
                        Log.d("enqueueDownload() ", "Error " + result);
                    }
                });
    }

    private Extras getExtrasForRequest(Request request) {
        final MutableExtras extras = new MutableExtras();
        extras.putBoolean("testBoolean", true);
        extras.putString("testString", "test");
        extras.putFloat("testFloat", Float.MIN_VALUE);
        extras.putDouble("testDouble",Double.MIN_VALUE);
        extras.putInt("testInt", Integer.MAX_VALUE);
        extras.putLong("testLong", Long.MAX_VALUE);
        return extras;
    }

    private void updateViews(@NotNull Download download, Reason reason) {
        if (request.getId() == download.getId()) {
            setProgressView(download.getStatus(), download.getProgress());
            if (download.getError() != Error.NONE) {
                showDownloadErrorSnackBar(download.getError());
            }
        }
    }

    private void setProgressView(@NonNull final Status status, final int progress) {
        progressTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        switch (status) {
            case QUEUED: {
                progressTextView.setText(R.string.queued);
                break;
            }
            case ADDED: {
                progressTextView.setText(R.string.added);
                break;
            }
            case COMPLETED:
            case DOWNLOADING: {
                if (progress == -1) {
                    progressTextView.setText(R.string.downloading);
                } else {
                    final String progressString = getResources().getString(R.string.percent_progress, progress);
                    progressTextView.setText(progressString);
                    progressTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                }
                break;
            }
            default: {
                progressTextView.setText(R.string.status_unknown);
                break;
            }
        }
    }

    private void showDownloadErrorSnackBar(@NotNull Error error) {
        final Snackbar snackbar = Snackbar.make(mainView, "К сожалению возникла ошибка загрузки. Проверьте подключение к интернету. Если это не помогло, то свяжитесь с нами и сообщите нам код ошибки. Код ошибки: " + error, Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        TextView snackTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);

        snackTextView.setMaxLines(13);
        snackbar.setAction(R.string.retry, v -> {
            fetch.retry(request.getId());
            snackbar.dismiss();
        });
        snackbar.show();
    }

    public void decompressArchive(String archivePath) {
        CheckView mCheckView = findViewById(R.id.check);
        ProgressBar bar = (ProgressBar) findViewById(R.id.progress);
        bar.setVisibility(View.VISIBLE);
        String path = Data.getSaveDir(this);

        new Decompress(archivePath, path, bar).execute();

        new CountDownTimer(500, 500) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                downloadTitle.setText("Готово!");
                downloadText.setText("Мы сделали все что нужно для хорошей игры");
                bar.setVisibility(View.GONE);
                mCheckView.check();

                // Удаляем файл архива
                File file = new File(archivePath);

                file.delete();

                new CountDownTimer(500, 500) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        Intent intent;
                        intent = new Intent(SingleDownloadActivity.this, StoryInfoActivity.class);
                        intent.putExtra("storyId", (Integer) questId);
                        startActivity(intent);
                        finish();
                    }
                }.start();
            }
        }.start();
    }



}