#!/bin/sh
set -e
RES_DIR=$(dirname $0)/../android/res
aseprite --batch icon-48.ase --save-as $RES_DIR/drawable-hdpi/ic_launcher.png --scale 2 --save-as $RES_DIR/drawable-mdpi/ic_launcher.png
aseprite --batch icon-72.ase --save-as $RES_DIR/drawable-xhdpi/ic_launcher.png --scale 2 --save-as $RES_DIR/drawable-xxhdpi/ic_launcher.png
