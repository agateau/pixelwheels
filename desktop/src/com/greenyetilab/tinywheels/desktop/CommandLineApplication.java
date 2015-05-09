package com.greenyetilab.tinywheels.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Convenience abstract class to create command line apps while getting access to all Gdx.* services
 */
public abstract class CommandLineApplication extends LwjglApplication {
    private static class AppAdapter extends ApplicationAdapter {
        CommandLineApplication mApp;
        String[] mArguments;

        @Override
        public void create() {
            System.exit(mApp.run(mArguments));
        }
    }

    public CommandLineApplication(String title, String[] arguments) {
        super(new AppAdapter(), createConfig(title));
        AppAdapter appAdapter = (AppAdapter)getApplicationListener();
        appAdapter.mApp = this;
        appAdapter.mArguments = arguments;
    }

    private static LwjglApplicationConfiguration createConfig(String title) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        LwjglApplicationConfiguration.disableAudio = true;
        config.allowSoftwareMode = true;
        config.forceExit = false;
        config.width = 100;
        config.height = 50;
        config.title = title;
        return config;
    }

    abstract int run(String[] arguments);
}
