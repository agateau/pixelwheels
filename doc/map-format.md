A track consists of several layers:

# Tile layers

One or more background layers, named bgN.

Zero or more foreground layers, named fgN, these are drawn on top of the vehicles.

# Borders

A "Borders" object layer containing rectangles, polygons or ellipsis representing the borders of the track.

# Sections

A "Sections" object layer containing segments dividing the track into convex quadrilaterals.

Segments must be named with a number (can be a float) indicating their order. A section quadrilateral is defined by two consecutive segments.

# Waypoints

A "Waypoints" object layer containing ellipsis indicating where AI pilot should go.

# Bonuses

A "BonusSpots" object layer containing ellipsis indicating where bonus should appear.

# Tile properties

## `material`

Enum. Defaults to ROAD. Must be one of the values of the com.agateau.tinywheels.Material enum.

## `start`

Bool. Defaults to false. Set to true for the tile representing the start position.
