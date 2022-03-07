# Content

This directory contains tools used to build or work on Pixel Wheels.

## asetools/

Command-line tools to turn Aseprite images into PNG usable by the game.

More details in [asetools/README.md](asetools/README.md).

## fonts/

Scripts to process fonts shipped with the game, to reduce their size.

## gettext/

Defines gettext ITS rules.

More details in [gettext/README.md](gettext/README.md).

## packaging/

Contains the `create-archives` script and its data files. This script creates the final zip files for each supported desktop platform. The zip files include a JRE so players do not have to install Java themselves.

## src/

Java source code for:

- Packer: Turns individual PNG files into atlases. Used when building the game.
- LapPositionTableGenerator: Loads a TMX file and generates a PNG of the various track sections. Helper tool to find problems when creating tracks.
- MapScreenshotGenerator: Loads a TMX file and creates a small PNG file of it. The created PNG can be used as a starting point to create the track icon.
- TrackEditor: Tool to edit some aspects of a track more easily than with Tiled. More details in [src/com/agateau/pixelwheels/tools/trackeditor/README.md](src/com/agateau/pixelwheels/tools/trackeditor/README.md).

## apply-codingstyle

Downloads Google Java formatter and format the source code with it. Can also be used in check mode.

## pad-map-tiles

Add padding around tiles used by tracks to avoid texture bleeding.

## po-update

Update the translation files or checks they are up-to-date.
