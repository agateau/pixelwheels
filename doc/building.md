# Building Tiny Wheels

## Assets

Some assets must be generated from work files with:

    make -C core/assets-src

## Map screenshots

The map screenshots are generated using the MapScreenshotGenerator tool.

Attention! This tool *requires a graphical interface* because it uses LibGDX
to do the rendering.

You can run it with:

    make mapscreenshotgenerator

## Pack images

Once assets and screenshots have been generated, you can pack them into atlases
with:

    make packer

## Pack maps

Maps also needs to be packed. This is done with:

    make mappacker

## Build the game

Build the game
