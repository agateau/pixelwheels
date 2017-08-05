A track consists of several layers:

# Tile layers

One or more background layers, named bgN.

Zero or more foreground layers, named fgN, these are drawn on top of the vehicles.

# Borders

A "Borders" object layer containing boxes representing the obstacles of the track.

# Sections

A "Sections" object layer containing segments dividing the track into convex quadrilaterals.

Segments must be named with a number (can be a float) indicating their order. A section quadrilateral is defined by two consecutive segments.

# Waypoints

A "Waypoints" object layer containing ellipsis indicating where AI pilot should go.

# Bonuses

A "BonusSpots" object layer containing ellipsis indicating where bonus should appear.
