# Building Pixel Wheels

The Docker-based build is simpler but the manual build gives you more control.

## Docker-based build

### Setup

Make sure Docker is setup on your machine.

To run your newly-built game you will at least need a Java Runtime Environment (JRE) installed.

Run the `ci/build-docker-image` script.

### Building

Run the `ci/docker-build-game` script.

### Running the game

```
cd android/assets
java -jar ../../desktop/build/libs/desktop-1.0.jar
```

## Manual build

### Dependencies

Building Pixel Wheels requires the following components:

- A JDK, version 11
- libgdx: <https://libgdx.badlogicgames.com>
- ImageMagick: <http://imagemagick.org>
- GNU Make: <http://www.gnu.org/software/make/>
- Some Python packages:
    - PAFX: <https://github.com/agateau/pafx>
    - Pillow: <https://python-pillow.github.io>
    - pypng: <https://github.com/drj11/pypng>

#### Python packages

You can install the required Python packages with:

    pip3 install -r requirements.txt

### Assets

Some assets must be generated from work files with:

    make assets

### Pack images

Once assets have been generated, you can pack them into atlases with:

    make packer

### Build translations

Translations files (.po) must be turned into Java files with:

    make po-compile

### Build the game

Run:

    make

You can also build and run it with:

    make run
