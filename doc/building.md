# Building Pixel Wheels

## Dependencies

Building Pixel Wheels requires the following components:

- A JDK
- libgdx: <https://libgdx.badlogicgames.com>
- pafx: <https://github.com/agateau/pafx>
- aseprite: <https://aseprite.org>
- imagemagick: <http://imagemagick.org>
- GNU Make: <http://www.gnu.org/software/make/>

## Assets

Some assets must be generated from work files with:

    make assets

## Map screenshots

The map screenshots are generated using the MapScreenshotGenerator tool, which
is part of the source code.

Attention! *This tool requires a graphical interface* because it uses LibGDX
to do the rendering.

You can build and run it with:

    make mapscreenshotgenerator

## Pack images

Once assets and screenshots have been generated, you can pack them into atlases
with:

    make packer

## Pack maps

Maps also needs to be packed. This is done with:

    make mappacker

## Build the game

Run:

    make

You can also build and run it with:

    make run
