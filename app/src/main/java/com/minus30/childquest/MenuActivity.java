package com.minus30.childquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ImageViewCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minus30.childquest.R;

public class MenuActivity extends AppCompatActivity {
    LinearLayout bottomButton1, bottomButton2, bottomButton3/*, bottomButton4*/;
    int curMenuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void bottomMenuIntent(View v) {
        if(curMenuId == v.getId()){
            return;
        }

        Intent intent;

        switch (v.getId()) {
            case R.id.bottomButton1:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.bottomButton2:
                intent = new Intent(this, ShopActivity.class);
                startActivity(intent);
                break;
            case R.id.bottomButton3:
                intent = new Intent(this, BlogPageActivity.class);
                startActivity(intent);
                break;
            /*case R.id.bottomButton4:
                intent = new Intent(this, BlogPostPageActivity.class);
                startActivity(intent);
                break;*/
            default:
                break;
        }
    }

    // add bottom menu
    public void addBottomMenu(String pageName) {
        LinearLayout currentMenu;

        FrameLayout bottomMenuContainer = (FrameLayout) findViewById(R.id.bottomMenuContainer);

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View bottomMenu = inflater.inflate(R.layout.bottom_menu, null);

        bottomMenuContainer.addView(bottomMenu);

        bottomButton1 = (LinearLayout) findViewById(R.id.bottomButton1);
        bottomButton2 = (LinearLayout) findViewById(R.id.bottomButton2);
        bottomButton3 = (LinearLayout) findViewById(R.id.bottomButton3);



        switch (pageName){
            case "game":
                currentMenu = bottomButton1;
                curMenuId = R.id.bottomButton1;
                break;
            case "shop":
                currentMenu = bottomButton2;
                curMenuId = R.id.bottomButton2;
                break;
            case "blog":
                currentMenu = bottomButton3;
                curMenuId = R.id.bottomButton3;
                break;
            default:
                currentMenu = bottomButton1;
                curMenuId = R.id.bottomButton1;
                break;
        }

        ImageView menuIcon = (ImageView) currentMenu.findViewWithTag("menuIcon");
        menuIcon.setBackgroundResource(R.drawable.menu_active);
        ImageViewCompat.setImageTintList(menuIcon, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));



        TextView menuText = (TextView) currentMenu.findViewWithTag("menuText");
        menuText.setTextColor(getResources().getColor(R.color.black));


        Typeface typeface = ResourcesCompat.getFont(this, R.font.rubik_medium);
        menuText.setTypeface(typeface);

    }
}