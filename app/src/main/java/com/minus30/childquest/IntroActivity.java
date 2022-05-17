package com.minus30.childquest;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroPageTransformerType;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class IntroActivity extends AppIntro2 {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_1));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_3));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_4));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_2));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_5));


        setImmersive(true);

        setWizardMode(true);

        setIndicatorColor(getColor(R.color.orange), getColor(R.color.grey));

        setTransformer(AppIntroPageTransformerType.Depth.INSTANCE);

        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                4, true);
    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        final String FIRST_START = "";
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putBoolean(FIRST_START, false);
        e.apply();
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        final String FIRST_START = "";
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putBoolean(FIRST_START, false);
        e.apply();
        super.onDonePressed(currentFragment);
        finish();
    }
}