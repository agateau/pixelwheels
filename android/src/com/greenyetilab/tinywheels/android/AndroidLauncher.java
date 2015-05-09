package com.greenyetilab.tinywheels.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.greenyetilab.tinywheels.RaceGame;
import com.greenyetilab.utils.FileUtils;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        FileUtils.appName = "tinywheels";
        initialize(new RaceGame(), config);
    }
}
