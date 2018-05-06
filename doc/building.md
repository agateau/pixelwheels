# Building Pixel Wheels

## Dependencies

Building Pixel Wheels requires the following components:

- A JDK
- libgdx: <https://libgdx.badlogicgames.com>
- Aseprite: <https://aseprite.org> (or LibreSprite <https://github.com/LibreSprite/LibreSprite>)
- ImageMagick: <http://imagemagick.org>
- GNU Make: <http://www.gnu.org/software/make/>
- PAFX: <https://github.com/agateau/pafx>
- Pillow: <https:python-pillow.github.io/>

You can install PAFX and Pillow with:

    pip3 install -r ci/requirements.txt

## Assets

Some assets must be generated from work files with:

    make assets

If you want to use LibreSprite instead of Aseprite, generate the assets with
this command instead:

    make assets ASEPRITE=/path/to/libresprite

## Pack images

Once assets have been generated, you can pack them into atlases with:

    make packer

## Build the game

Run:

    make

You can also build and run it with:

    make run
