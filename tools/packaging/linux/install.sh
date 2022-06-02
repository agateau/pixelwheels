#!/bin/bash
set -euo pipefail

cd $(dirname $0)

if [ "$UID" -eq 0 ] ; then
    DEFAULT_INSTALL_DIR=/usr/local
else
    DEFAULT_INSTALL_DIR=$HOME/.local
fi

DESKTOP_FILE=com.agateau.PixelWheels.desktop

GAME_FILES="jre
pixelwheels
pixelwheels.jar
pixelwheels.json"

echo -n "Where should the game be installed? [$DEFAULT_INSTALL_DIR] "

read install_dir

if [ -z "$install_dir" ] ; then
    install_dir=$DEFAULT_INSTALL_DIR
fi
lib_dir=$install_dir/lib/pixelwheels
bin_dir=$install_dir/bin

mkdir -p "$install_dir"
mkdir -p "$lib_dir"
mkdir -p "$bin_dir"

echo "Copying game files"
for file in $GAME_FILES ; do
    cp -a "$file" "$lib_dir"
done

echo "Creating entry in $bin_dir"
rm -f $bin_dir/pixelwheels
ln -s $lib_dir/pixelwheels $bin_dir/pixelwheels

echo "Installing metadata"
cp -a share "$install_dir"

# $bin_dir might not be in $PATH, set the Exec part of the .desktop to the full path of the executable
sed -i "s,Exec=.*,Exec=$bin_dir/pixelwheels," "$install_dir/share/applications/$DESKTOP_FILE"

xdg-icon-resource forceupdate
