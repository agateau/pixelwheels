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

## create-archives and installer-data/

`create-archives` creates the final zip files for each supported desktop platform. The zip files include a JRE so players do not have to install Java.

It uses files from the `installer-data` directory.

## src/

Java source code for:

- Packer: Turns individual PNG files into atlases. Used when building the game.
- LapPositionTableGenerator: Loads a TMX file and generates a PNG of the various track sections. Helper tool to find problems when creating tracks.
- MapScreenshotGenerator: Loads a TMX file and creates a small PNG file of it. The created PNG can be used as a starting point to create the track icon.

## apply-codingstyle

Downloads Google Java formatter and format the source code with it. Can also be used in check mode.

## pad-map-tiles

Add padding around tiles used by tracks to avoid texture bleeding.

## po-update

Update the translation files or checks they are up-to-date.
