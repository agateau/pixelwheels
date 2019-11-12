# Map format

This document describes the layer which a Pixel Wheels map is made of.

Maps are created using [Tiled](http://mapeditor.org).

# Map layers

A map consists of several layers:

## Tile layers

One or more background layers, named bgN.

Zero or more foreground layers, named fgN, these are drawn on top of the vehicles.

## Obstacles

An "Obstacles" object layer containing rectangles, polygons or ellipsis representing the borders of the track as well as movable objects on it.

A border is an object with an empty "Type" property. Its boundaries must match static elements such as building drawn in the bgN layers.

An obstacle can move (for example a tyre). Its "Type" property defines the kind of obstacle. If the object area is large enough, multiple instances of the obstacle will be created.

## Sections

A "Sections" object layer containing segments dividing the track into convex quadrilaterals.

Segments must be named with a number (can be a float) indicating their order. A section quadrilateral is defined by two consecutive segments.

All areas where vehicles can go must be covered by section quadrilaterals.

## Waypoints

A "Waypoints" object layer containing ellipsis indicating where AI pilots should go.

## Bonuses

A "BonusSpots" object layer containing ellipsis indicating where bonuses should appear.

# Tile properties

## `material`

Enum. Defaults to ROAD. Must be one of the values of the com.agateau.pixelwheels.map.Material enum.

## `start`

Bool. Defaults to false. Set to true for the tile representing the start position.
