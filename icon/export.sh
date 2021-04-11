#!/bin/bash
set -euo pipefail

cd $(dirname $0)

ANDROID_DIR=$PWD/../android
ASSETS_DIR=$ANDROID_DIR/assets
RES_DIR=$ANDROID_DIR/res

GPLAY_ICON=$PWD/../fastlane/metadata/android/en-US/images/icon.png

MACOS_ICON=$PWD/../tools/installer-data/pixelwheels.icns

BGCOLOR=#5a6988

DESKTOP_ICON_SIZES="16 32 48"

SCALED_REFERENCE_SIZE=64
SCALED_DESKTOP_ICON_SIZES="128 256 512 1024"

# Requires png2icns from https://icns.sourceforge.io/ (icnsutils package on
# Debian/Ubuntu)
generate_macos_icon() {
    for size in $DESKTOP_ICON_SIZES ; do
        aseprite --batch icon.ase --slice icon-$size \
            --save-as work/icon-$size.png
    done

    for size in $SCALED_DESKTOP_ICON_SIZES ; do
        scale=$((size / $SCALED_REFERENCE_SIZE))
        aseprite --batch icon.ase --slice icon-$SCALED_REFERENCE_SIZE \
            --scale $scale \
            --save-as work/icon-$size.png
    done

    png2icns $MACOS_ICON work/*.png
}

generate_android_icons() {
    # mdpi & xhdpi
    aseprite --batch icon.ase --slice icon-48 \
        --save-as $RES_DIR/drawable-mdpi/ic_launcher.png \
        --scale 2 --save-as $RES_DIR/drawable-xhdpi/ic_launcher.png

    # hdpi & xxhdpi
    aseprite --batch icon.ase --slice icon-72 \
        --save-as $RES_DIR/drawable-hdpi/ic_launcher.png \
        --scale 2 --save-as $RES_DIR/drawable-xxhdpi/ic_launcher.png

    # Android TV Banner: 320 x 180, xhdpi
    aseprite --batch icon.ase --slice tv-banner \
        --save-as $RES_DIR/drawable-mdpi/ic_launcher.png \
        --scale 2 --save-as $RES_DIR/drawable-xhdpi/tv_banner.png

    # Desktop
    aseprite --batch icon.ase --slice icon-72 \
        --scale 2 --save-as $ASSETS_DIR/desktop-icon/desktop-icon.png
}

# Google Play: 512 x 512 RVB, flat
# (Start from the biggest, unscaled version)
generate_google_play_icon() {
    convert -scale 512x512 $RES_DIR/drawable-hdpi/ic_launcher.png \
        -background "$BGCOLOR" -flatten $GPLAY_ICON
}

rm -rf work
mkdir work

generate_android_icons
generate_google_play_icon
generate_macos_icon

rm -rf work
