# Map format

This document describes how to create a map for Pixel Wheels.

## Tools

Maps are created using [Tiled](http://mapeditor.org).

## Map layers

A map consists of several layers:

### Tile layers

One or more background layers, named `bg<N>`: "bg1", "bg2"...

Zero or more foreground layers, named `fg<N>`: "fg1", "fg2"... These are drawn on top of the vehicles.

### Obstacles

An "Obstacles" object layer containing rectangles, polygons or ellipsis representing the borders of the track as well as movable objects on it.

A border is an object with an empty "Type" property. Its boundaries must match static elements such as building drawn in the bgN layers.

An obstacle can move (for example a tyre). Its "Type" property defines the kind of obstacle. If the object area is large enough, multiple instances of the obstacle will be created.

### Sections

A "Sections" object layer containing segments dividing the track into *convex* quadrilaterals.

Segments are created as 2-point polygons in Tiled.

Segments must be named with a number (can be a float) indicating their order. A section quadrilateral is defined by two consecutive segments.

All areas where vehicles can go must be covered by section quadrilaterals.

The first segment must be aligned with the *beginning* of the finish line.

### Waypoints

A "Waypoints" object layer containing ellipsis indicating where AI pilots should go.

### Bonuses

A "BonusSpots" object layer containing ellipsis indicating where bonuses should appear.

## Tilesets

Tilesets are defined in .tsx files, not bundled in the .tmx files. There is one .tsx file per championship. When creating a new map, make sure to load the right .tsx file.

## Tile properties

### `material`

Enum. Defaults to ROAD. Must be one of the values of the com.agateau.pixelwheels.map.Material enum.

### `start`

String. Defaults to `false`. Set to `true` for the tile representing the start position.

## Map icon

The map must have an icon to show in the game user interface.

Its largest dimension must be 150 pixels.

The path to the icon is `core/assets-src/ui/map-icons/<map-name>.ase`.

## Adding the map to the game

Edit the `Assets` class:

- Add the map to the `tracks` array.
- Add the map to the relevant championship in `initChampionships()`.
