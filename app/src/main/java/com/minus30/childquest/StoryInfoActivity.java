package com.minus30.childquest;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minus30.childquest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class StoryInfoActivity extends MenuActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    TextView showMoreLink;
    int storyId;

    String propLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_info);

        showMoreLink = (TextView)findViewById(R.id.showMoreLink);

        int defaultKeyValue = 0;
        storyId = getIntent().getIntExtra("storyId", defaultKeyValue);

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
        TextView scenarioDuration = (TextView)findViewById(R.id.scenarioDuration);
        TextView scenarioRating = (TextView)findViewById(R.id.scenarioRating);
        ImageView scenarioImage = (ImageView)findViewById(R.id.scenarioImage);

        scenarioName.setText(cursor.getString(1));
        scenarioDuration.setText(cursor.getString(6));
        scenarioRating.setText(cursor.getString(4));

        propLink = cursor.getString(8);

        byte[] byteArray = Base64.decode(cursor.getBlob(3), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
        scenarioImage.setImageBitmap(bm);

        cursor.close();

        addBottomMenu("game");
    }

    // onclick function for go to the story
    public void goToGame(View v) {
        Intent intent;
        intent = new Intent(this, GameStepsActivity.class);
        intent.putExtra("storyId", storyId);
        startActivity(intent);
    }

    public void showRules(View v) throws JSONException {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.setContentView(R.layout.modal_instructions);

        // get categories and send stories to different rv
        Cursor cursor = mDb.rawQuery("SELECT * FROM stories WHERE id = ?", new String[]{String.valueOf(storyId)});
        cursor.moveToFirst();


        String instructions = cursor.getString(9);
        String link = cursor.getString(11);
        cursor.close();

        // create json array from string
        JSONArray mJsonArray = null;
        try {
            mJsonArray = new JSONArray(instructions);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int stepQuantity = mJsonArray.length();

        // convert json to array list of JSONObjects
        for (int i = 0; i < stepQuantity; i++) {
            JSONObject mJsonObjectStep = null;
            try {
                mJsonObjectStep = mJsonArray.getJSONObject(i);
                String title = mJsonObjectStep.getString("title");
                String text = mJsonObjectStep.getString("text");
                Log.d("MyLogs", "" + title);
                addInstructionView(dialog, title, text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // вставляем ссылку на видео-инструкцию
        addVideoView(dialog, link);

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

    public void addInstructionView(Dialog dialog, String title, String text) {
        LinearLayout cardContainer = dialog.findViewById(R.id.cardContainer);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        View cardActionView = inflater.inflate(R.layout.modal_instructions_card, cardContainer, false);
        cardContainer.addView(cardActionView);

        TextView instructionsTitle = cardActionView.findViewById(R.id.instructionsTitle);
        TextView instructionsText = cardActionView.findViewById(R.id.instructionsText);

        // set card title
        instructionsTitle.setText(title);
        // set card text
        instructionsText.setText(Html.fromHtml(text));
    }

    // добавляем ссылку на видео-инструкцию
    public void addVideoView(Dialog dialog, String link) {
        LinearLayout cardContainer = dialog.findViewById(R.id.cardContainer);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        View cardActionView = inflater.inflate(R.layout.link_to_video, cardContainer, false);
        cardContainer.addView(cardActionView);

        Button copyLink = cardActionView.findViewById(R.id.copyLink);

        copyLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }
        });
    }



    public void showScenario(View v) {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.setContentView(R.layout.modal_props);

        // click copy button
        Button copyLink = dialog.findViewById(R.id.copyLink);
        copyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text",Html.fromHtml(propLink));
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getApplicationContext(),"Текст скопирован",Toast.LENGTH_SHORT).show();
            }
        });

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




    public void backButtonAction(View view) {
        supportFinishAfterTransition();
    }
}
