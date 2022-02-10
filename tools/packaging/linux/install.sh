#!/bin/bash
set -euo pipefail

cd $(dirname $0)

if [ "$UID" -eq 0 ] ; then
    DEFAULT_INSTALL_DIR=/opt/pixelwheels
else
    DEFAULT_INSTALL_DIR=$HOME/.local/lib/pixelwheels
fi

DESKTOP_FILE=com.agateau.PixelWheels.desktop
DESKTOP_FILE_TMPL=$DESKTOP_FILE.in

GAME_FILES="jre
pixelwheels
pixelwheels.jar
pixelwheels.json"

echo "Where should the game be installed? [$DEFAULT_INSTALL_DIR]"

read install_dir

if [ -z "$install_dir" ] ; then
    install_dir=$DEFAULT_INSTALL_DIR
fi

mkdir -p "$install_dir"

echo "Copying game files"
for file in $GAME_FILES ; do
    cp -a "$file" "$install_dir"
done

echo "Installing icons"
for icon in icons/*-*.png ; do
    size=$(basename $icon | sed 's/-.*//')

    xdg-icon-resource install \
        --novendor \
        --noupdate \
        --context apps \
        --size $size \
        $icon \
        com.agateau.PixelWheels
done
xdg-icon-resource forceupdate

echo "Installing launcher menu entry"
sed "s,^Exec=.*,Exec=$install_dir/pixelwheels," \
    $DESKTOP_FILE_TMPL > $DESKTOP_FILE

xdg-desktop-menu install --novendor $DESKTOP_FILE
