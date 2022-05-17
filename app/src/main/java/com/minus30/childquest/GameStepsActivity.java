package com.minus30.childquest;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameStepsActivity extends MenuActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    int stepCursor = 0;
    int stepQuantity = 0;
    ArrayList<Object> stepsArrayList = new ArrayList<>();
    // all audio, that play in this moment
    List<MediaPlayer> mPlayers = new ArrayList<MediaPlayer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_steps);

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


        // get category info
        Intent mIntent = getIntent();
        int storyId = mIntent.getIntExtra("storyId", 0);

        String query = "SELECT * FROM stories WHERE id = (" + storyId + ")";
        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();
        String steps = cursor.getString(7);
        cursor.close();

        // create json array from string
        JSONArray mJsonArray = null;
        try {
            mJsonArray = new JSONArray(steps);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        stepQuantity = mJsonArray.length();

        // convert json to array list of JSONObjects
        for (int i = 0; i < stepQuantity; i++) {
            JSONArray mJsonObjectStep = null;
            try {
                mJsonObjectStep = mJsonArray.getJSONArray(i);
                stepsArrayList.add(mJsonObjectStep);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            initializedStep(stepCursor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void stopPlay(MediaPlayer mPlayer, LinearLayout playButton){
        TextView playButtonText = playButton.findViewById(R.id.playButtonText);
        ImageView playButtonImg = playButton.findViewById(R.id.playButtonImg);
        Drawable res = getResources().getDrawable(android.R.drawable.ic_media_play);
        playButtonImg.setImageDrawable(res);
        playButtonText.setText("Включить");

        mPlayer.stop();
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            playButton.setEnabled(true);
        }
        catch (Throwable t) {
        }
    }

    public void play(MediaPlayer mPlayer, LinearLayout playButton){
        TextView playButtonText = playButton.findViewById(R.id.playButtonText);
        ImageView playButtonImg = playButton.findViewById(R.id.playButtonImg);
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
            Drawable res = getResources().getDrawable(android.R.drawable.ic_media_pause);
            playButtonImg.setImageDrawable(res);
            playButtonText.setText("Пауза");
        } else  {
            mPlayer.pause();
            Drawable res = getResources().getDrawable(android.R.drawable.ic_media_play);
            playButtonImg.setImageDrawable(res);
            playButtonText.setText("Включить");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeAllPlay();
    }

    private void removeAllPlay(){
        MediaPlayer object;
        final int size = mPlayers.size();
        for (int i = 0; i < size; i++)
        {
            object = mPlayers.get(i);
            object.stop();
            try {
                object.prepare();
                object.seekTo(0);
            }
            catch (Throwable t) {
            }
        }
        mPlayers.clear();
    }

    public void fullScreenImage(View v) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.modal_fullscreen_image);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_transparent)));

        String query = "SELECT * FROM images WHERE id IN (" + (String)v.getTag() + ")";
        Cursor imageCursor = mDb.rawQuery(query, null);
        imageCursor.moveToFirst();

        List<Uri> images = new ArrayList<Uri>();
        while (!imageCursor.isAfterLast()) {
            Uri imageUri = Uri.parse(Data.getSaveDir(this) + imageCursor.getString(1));
            images.add(imageUri);
            imageCursor.moveToNext();
        }
        imageCursor.close();

        ViewPager viewPager = dialog.findViewById(R.id.viewPager);
        SliderAdapter sliderAdapter = new SliderAdapter(this, images);
        viewPager.setAdapter(sliderAdapter);


        // if slides count more then 1, show navigation
        if(sliderAdapter.getCount() != 1){
            ImageButton leftNav = dialog.findViewById(R.id.left_nav);
            ImageButton rightNav = dialog.findViewById(R.id.right_nav);

            leftNav.setVisibility(View.GONE);
            rightNav.setVisibility(View.VISIBLE);

            // Images left navigation
            leftNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = viewPager.getCurrentItem();
                    if (tab > 0) {
                        tab--;
                        viewPager.setCurrentItem(tab);
                    } else if (tab == 0) {
                        viewPager.setCurrentItem(tab);
                    }
                }
            });

            // Images right navigatin
            rightNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = viewPager.getCurrentItem();
                    tab++;
                    viewPager.setCurrentItem(tab);
                }
            });

            // listener скрыть/показать стрелочки влево-вправо
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    if(viewPager.getCurrentItem() == 0){
                        leftNav.setVisibility(View.GONE);
                    } else {
                        leftNav.setVisibility(View.VISIBLE);
                    }

                    if(viewPager.getCurrentItem() == sliderAdapter.getCount()-1){
                        rightNav.setVisibility(View.GONE);
                    } else {
                        rightNav.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }


        // close button
        ImageButton dialogButton = dialog.findViewById(R.id.backButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void shareImages(View v) {
        // get Bitmap img from database
        String query = "SELECT * FROM images WHERE id IN (" + (String)v.getTag() + ")";
        Cursor cursor = mDb.rawQuery(query, null);
        Log.d("MyLogs", "cursor " + cursor.getCount());

        cursor.moveToFirst();

        ArrayList<Uri> myImageFileUri = new ArrayList<Uri>();

        while (!cursor.isAfterLast()) {
            File imageFile = new File(Data.getSaveDir(this), cursor.getString(1));

            Log.d("MyLogs", "imageFile " + imageFile);

            myImageFileUri.add(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", imageFile));
            Log.d("MyLogs", "myImageFileUri " + myImageFileUri);
            cursor.moveToNext();
        }
        cursor.close();

        Log.d("MyLogs", "myImageFileUri " + myImageFileUri);

        //create a intent
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, myImageFileUri);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share with"));
    }


    public void prevStep(View view) throws JSONException {
        removeAllPlay();
        if(stepCursor > 0) {
            stepCursor--;
            LinearLayout cardContainer = findViewById(R.id.cardContainer);
            cardContainer.removeAllViews();
            initializedStep(stepCursor);
        } else {
            this.finish();
        }
    }

    public void nextStep(View view) throws JSONException {
        removeAllPlay();
        stepCursor++;
        if(stepCursor < stepQuantity) {
            LinearLayout cardContainer = findViewById(R.id.cardContainer);
            cardContainer.removeAllViews();
            initializedStep(stepCursor);
        } else {
            Intent intent;
            intent = new Intent(this, EndGamePageActivity.class);
            startActivity(intent);
        }
    }

    // set title, generate cards and buttons every step
    private void initializedStep(int stepId) throws JSONException {
        //Получаем весь шаг как массив карточек
        JSONArray jsonStep = (JSONArray) stepsArrayList.get(stepId);

        for (int i = 0; i < jsonStep.length(); i++) {
            //Получаем карточку из шага в виде объекта json
            JSONObject jsonCard = null;
            try {
                jsonCard = new JSONObject(jsonStep.getString(i));
                createCardAction(jsonCard, stepId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setStepTitle(stepId);
    }



    private void createCardAction(JSONObject stepCardObject, int stepId) throws JSONException {
        int cardLoop;
        String cardType = stepCardObject.getString("type");

        LinearLayout cardContainer = (LinearLayout) findViewById(R.id.cardContainer);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        switch(cardType) {
            case "text" :
                cardLoop = R.layout.game_card_loop_text;
                break;
            case "action" :
                cardLoop = R.layout.game_card_loop_action;
                break;
            case "decision" :
                cardLoop = R.layout.game_card_loop_img;
                break;
            case "audio" :
                cardLoop = R.layout.game_card_loop_audio;
                break;
            default:
                cardLoop = R.layout.game_card_loop_text;
        }

        View cardActionView = inflater.inflate(cardLoop, cardContainer, false);

        TextView cardTitle = cardActionView.findViewById(R.id.gameCardTitle);
        TextView cardText = cardActionView.findViewById(R.id.gameCardText);

        // set card title
        cardTitle.setText(stepCardObject.getString("title"));
        // set card text
        cardText.setText(Html.fromHtml(stepCardObject.getString("text")));


        switch(cardType) {
            case "action" :
                // set tag with share img ID in database
                String shareImgId = stepCardObject.getString("imgId");
                LinearLayout shareButton = cardActionView.findViewById(R.id.shareButton);
                LinearLayout openButton = cardActionView.findViewById(R.id.openButton);
                shareButton.setTag(shareImgId);
                // если id картинки 0, то к данному шагу нет картинки
                if(shareImgId != null && !shareImgId.trim().isEmpty()){
                    openButton.setTag(shareImgId);
                } else {
                    openButton.setVisibility(View.GONE);
                }
                break;
            case "audio" :
                TextView showMoreLink = cardActionView.findViewById(R.id.showMoreLink);

                MediaPlayer mPlayer;
                LinearLayout playButton = cardActionView.findViewById(R.id.playButton);

                Cursor audioCursor = mDb.rawQuery("SELECT * FROM audio WHERE id = ?", new String[]{stepCardObject.getString("audioId")});
                audioCursor.moveToFirst();

                mPlayer = new MediaPlayer();
                Uri myUri1 = Uri.parse(Data.getSaveDir(this) + audioCursor.getString(1));
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mPlayer.setDataSource(getApplicationContext(), myUri1);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                } catch (SecurityException e) {
                    Toast.makeText(getApplicationContext(), "You might not set the URI   correctly!", Toast.LENGTH_LONG).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), "You might not set the URI  correctly!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    mPlayer.prepare();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), "You might not set the URI  correctly!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "You might not set the URI  correctly!", Toast.LENGTH_LONG).show();
                }


                //mPlayer= MediaPlayer.create(this, Uri.fromFile(getBaseContext().getFileStreamPath("music_954.mp4")));
                Log.d("MyLogs", "mPlayer " + mPlayer);


                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlay(mPlayer, playButton);
                        mPlayers.remove(mPlayer);
                    }
                });

                String audioId = stepCardObject.getString("audioId");
                playButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("qweqwe", "qweqwe" + mPlayers);
                        play(mPlayer, playButton);
                        mPlayers.add(mPlayer);
                    }
                });

                LinearLayout refreshButton = cardActionView.findViewById(R.id.refreshButton);
                refreshButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        stopPlay(mPlayer, playButton);
                        mPlayers.remove(mPlayer);
                    }
                });
                playButton.setTag(audioId);
                break;
        }

        cardContainer.addView(cardActionView);
    }



    private void setStepTitle(int stepId) {
        String title = getString(R.string.step_title_quest) + " " + (stepId+1) +  " " + getString(R.string.step_title_of) + " " + stepQuantity;
        // set title of the category
        TextView stepTitle = (TextView)findViewById(R.id.stepTitle);
        stepTitle.setText(title);
    }

    public void backButtonAction(View view) {
        this.finish();
    }

    // ракрывает-скрывает текст блоке текстовой информации
    public void ShowMoreStepText(View v) {
        TextView tv = (TextView) v;
        ConstraintLayout parentView = (ConstraintLayout) v.getParent();
        TextView textCard = parentView.findViewById(R.id.gameCardText);

        if (tv.getText().toString().equalsIgnoreCase("Показать полностью"))
        {
            textCard.setMaxLines(999);//your TextView
            tv.setText("Свернуть");
        }
        else
        {
            textCard.setMaxLines(3);//your TextView
            tv.setText("Показать полностью");
        }
    }

    // ракрывает-скрывает подсказку
    public void ShowDecision(View v) {
        TextView showButton = (TextView) v;
        ConstraintLayout parentView = (ConstraintLayout) v.getParent();
        parentView.findViewById(R.id.gameCardContent);
        LinearLayout gameCardContent = parentView.findViewById(R.id.gameCardContent);

        if (gameCardContent.getVisibility() == View.VISIBLE) {
            gameCardContent.setVisibility(View.GONE);
            showButton.setText("Показать решение");
        } else {
            gameCardContent.setVisibility(View.VISIBLE);
            showButton.setText("Скрыть решение");
        }


    }

}