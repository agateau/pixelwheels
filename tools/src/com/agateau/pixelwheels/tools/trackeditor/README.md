# TrackEditor

This tool makes it easier to edit some aspects of a track. For now it only supports editing sections.

Usage:

    tools/trackeditor <path-to-tmx-file>

It is a keyboard-driven application. Here are the keyboard shortcuts:

- zoom in, zoom out: =/+, -
- select previous, next section line: Shift+Tab, Tab
- insert section line: i
- move selection: arrows
- remove section: Suppr
- scroll: h, j, k, l
- select only first point: F1
- select only second point: F2
- select both points: F3
- save: Control + S
- undo, redo: Control + Z, Control + Shift + Z

To move or scroll faster, hold Shift.

The tool automatically saves changes every 10 seconds.
