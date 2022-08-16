# Championships and tracks

This document describes how to create championships and tracks (aka maps) for Pixel Wheels.

## Tools

Track files are created using [Tiled](http://mapeditor.org).

## Files

### Championships

Tracks are grouped in championships. Championships are defined as XML files in the `android/assets/championships` directory.

Championship files are named `<N>.xml` with N starting from 0.

A championship file follows the following format:

```xml
<?xml version="1.0"?>
<!--
- `id` is the internal id of the championship. It must be made of [-a-z0-9] characters
- `name` is the name of the championship, shown in the game
-->
<championship id="..." name="...">
    <!--
    - `id` is the internal id of the track. It must match the filenames for the track (see below)
    - `name` is the name of the track, shown in the game

    Add a `track` element for each track of the championship
    -->
    <track id="..." name="...">
        <!--
        Default best lap and total time records.
        The `value` attribute is the record time, in seconds.
        There must be 3 entries for each record type.
        -->
        <lapRecords>
            <record value="..."/>
            <record value="..."/>
            <record value="..."/>
        </lapRecords>
        <totalRecords>
            <record value="..."/>
            <record value="..."/>
            <record value="..."/>
        </totalRecords>
    </track>
</championship>
```

Note: championship files are named `<N>.xml` and not `<championship-id>.xml` because libgdx cannot list the content of assets directory (Android limitation), so we load files from 0.xml to N.xml until we hit a value of N for which there is no N.xml file.

### Tracks

Tracks are defined in the `android/assets/maps` directory.

For each track there is a `<id>.tmx` file, where `<id>` is the internal ID defined in the championship file.

TMX files require a `<championship-id>.png` image storing the tiles, and a matching `<championship-id>.tsx` file. TMX and TSX files are created using Tiled.

## Map layers

A map consists of several layers:

### Tile layers

One or more background layers, named `bg<N>`: "bg1", "bg2"...

Zero or more foreground layers, named `fg<N>`: "fg1", "fg2"... These are drawn on top of the vehicles.

### Obstacles

An "Obstacles" object layer containing rectangles, polygons or ellipsis representing the borders of the track as well as movable objects on it.

A border is an object with an empty "Type" property. Its boundaries must match static elements such as building drawn in the bgN layers.

An obstacle can move (for example a tire). Its "Type" property defines the kind of obstacle. If the object area is large enough, multiple instances of the obstacle will be created.

### Sections

A "Sections" object layer containing segments dividing the track into *convex* quadrilaterals.

Segments are created as 2-point polygons in Tiled.

Segments must be named with a number (can be a float) indicating their order. A section quadrilateral is defined by two consecutive segments.

All areas where vehicles can go must be covered by section quadrilaterals.

The first segment must be aligned with the *beginning* of the finish line.

### Waypoints

A "Waypoints" object layer containing a polyline indicating where AI pilots should go.

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
