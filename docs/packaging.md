# Packaging

## Desktop: all-in-one zip files

The `desktop-archives` Makefile target generates self-contained zip files for all desktop platforms. These zip files contain a reduced version of the JRE, so the player does not have to install Java separately.

## Linux Desktop: classic install

On Linux, if you build the game yourself, you can use the `install` Makefile target to install it locally. It supports the classic `DESTDIR` and `prefix` variables. This should provide a good basis to anyone looking into building a distribution package for the game.

## Linux Desktop: Flathub

The game is packaged for Flathub. Its Flathub repository is [flathub/com.agateau.PixelWheels](https://github.com/flathub/com.agateau.PixelWheels).

## Android

The `apk-archives` Makefile target generates signed Android APK files. Generating these requires a valid `android/signing.gradle` file.

This file is confidential, so you need to create your own version of it. Have a look at [android/signing-sample.gradle](android/signing-sample.gradle) to see what it should look like.

At the moment there are two APK flavors: Google and itchio. The Google one does not contain the support links, because those are against Google Play policies.
