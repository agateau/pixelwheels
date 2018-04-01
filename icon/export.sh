#!/bin/sh
set -e
RES_DIR=$(dirname $0)/../android/res

# mdpi & xhdpi
aseprite --batch icon.ase --slice icon-48 \
    --save-as $RES_DIR/drawable-mdpi/ic_launcher.png \
    --scale 2 --save-as $RES_DIR/drawable-xhdpi/ic_launcher.png

# hdpi & xxhdpi
aseprite --batch icon.ase --slice icon-72 \
    --save-as $RES_DIR/drawable-hdpi/ic_launcher.png \
    --scale 2 --save-as $RES_DIR/drawable-xxhdpi/ic_launcher.png
