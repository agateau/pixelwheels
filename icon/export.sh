#!/bin/sh
set -e

cd $(dirname $0)

ANDROID_DIR=$PWD/../android
ASSETS_DIR=$ANDROID_DIR/assets
RES_DIR=$ANDROID_DIR/res

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
