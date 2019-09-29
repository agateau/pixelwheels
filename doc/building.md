# Building Pixel Wheels

## Dependencies

Building Pixel Wheels requires the following components:

- A JDK
- libgdx: <https://libgdx.badlogicgames.com>
- Aseprite: <https://aseprite.org>
- ImageMagick: <http://imagemagick.org>
- GNU Make: <http://www.gnu.org/software/make/>
- PAFX: <https://github.com/agateau/pafx>
- Pillow: <https:python-pillow.github.io/>

### PAFX and Pillow

You can install PAFX and Pillow with:

    pip3 install -r requirements.txt

### Aseprite

If you do not have a copy of Aseprite, follow the instructions from
[tools/aseprite][tools_aseprite] to install a headless version. The headless
version is enough to build Pixel Wheels assets.

[tools_aseprite]: ../tools/aseprite/README.md

## Assets

Some assets must be generated from work files with:

    make assets

## Pack images

Once assets have been generated, you can pack them into atlases with:

    make packer

## Build the game

Run:

    make

You can also build and run it with:

    make run
