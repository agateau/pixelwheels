# Aseprite

This directory contains scripts to download and build a headless version of
[Aseprite][], suitable to build assets.

[Aseprite]: https://www.aseprite.org

## Installing build dependencies

The `install-aseprite-dependencies` can be used to install build dependencies.
It has been tested on Ubuntu 18.04.

## Building

To download the source code in `/tmp/aseprite`:

    download-aseprite /tmp/aseprite

To build and install to /usr/local

    build-aseprite /tmp/aseprite /usr/local
