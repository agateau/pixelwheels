#!/bin/sh
set -e

cd $(dirname $0)

ANDROID_DIR=$PWD/../android
ASSETS_DIR=$ANDROID_DIR/assets
RES_DIR=$ANDROID_DIR/res

GPLAY_ICON=$PWD/../fastlane/metadata/android/en-US/images/icon.png

BGCOLOR=#5a6988

# mdpi & xhdpi
aseprite --batch icon.ase --slice icon-48 \
    --save-as $RES_DIR/drawable-mdpi/ic_launcher.png \
    --scale 2 --save-as $RES_DIR/drawable-xhdpi/ic_launcher.png

# hdpi & xxhdpi
aseprite --batch icon.ase --slice icon-72 \
    --save-as $RES_DIR/drawable-hdpi/ic_launcher.png \
    --scale 2 --save-as $RES_DIR/drawable-xxhdpi/ic_launcher.png

# Desktop
aseprite --batch icon.ase --slice icon-72 \
    --scale 2 --save-as $ASSETS_DIR/desktop-icon/desktop-icon.png

# Google Play: 512 x 512 RVB, flat
# (Start from the biggest, unscaled version)
convert -scale 512x512 $RES_DIR/drawable-hdpi/ic_launcher.png \
    -background "$BGCOLOR" -flatten $GPLAY_ICON

# Android TV Banner: 320 x 180, xhdpi
aseprite --batch icon.ase --slice tv-banner \
    --save-as $RES_DIR/drawable-mdpi/ic_launcher.png \
    --scale 2 --save-as $RES_DIR/drawable-xhdpi/tv_banner.png
