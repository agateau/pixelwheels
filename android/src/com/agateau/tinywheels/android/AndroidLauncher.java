package com.agateau.tinywheels.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.agateau.tinywheels.TwGame;
import com.agateau.utils.FileUtils;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        FileUtils.appName = "tinywheels";
        initialize(new TwGame(), config);
    }
}
