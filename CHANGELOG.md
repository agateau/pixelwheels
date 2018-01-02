# Changelog

## 0.5.0 - 2018-01-06

### Added
- On Android devices without physical navigation buttons, hide the on-screen
  buttons. They can be revealed by swiping from the edge to the center, like in
  most games.

- The driving buttons have been reworked: they are now larger and look sectors
  in the bottom-left and bottom-right corners of the screen.

### Changed
- The rank now uses a larger font in the HUD, making it easier to read.

- The game UI has been reworked to look nicer and to be usable from the
  keyboard (and soon from gamepads). Two screens remain to be converted: the
  high-score screen and the developer options one.

- Player vehicles now start at the last position. While this sounds unfair,
  it's more fun to pass others than to try to prevent others from passing you.

#### Internals
- Updated to libgdx 1.5.6.

### Fixed

- Fixed recovery helicopter dropping vehicles at the wrong angle.
- Fixed touch buttons to support multitouch, so you can now brake or trigger a
  bonus while turning.
- Made sure AI racers do not pick a vehicle used by a player.

## 0.4.0 - 2017-12-03
### Added
- Added a new vehicle: Santa Truck! This is the first 6-wheeled vehicle of the game.

### Changed
- Added road indicators to all tracks so that you can anticipate turns.
- Redrew snow tiles in "real" pixel-art, spread snow on top of pine-trees.
- Reduced viewport a bit so that vehicles look bigger.
- Made the wood bridge of Tiny-sur-Mer easier to drive by adding shallow water
  around it.
- Made it easier to catchup with AI racers by slowing them down when they are
  ahead of you.
- Made steering more progressive.

#### Internals
- Updated to Gradle 3.3 and Gradle Android plugin 2.3.3.

### Fixed
- Fixed vehicles being stuck spinning infinitely.
- Hopefully fixed camera turning more than one round.
- Made collisions with walls less forgiving.
- Fixed one player mode sometimes starting with more than one game view.

#### Internals
- Lots of linting fixes
- Improved map documentation
- Documented map properties

## 0.3.0 - 2017-11-05
### Changed
- Resurrected project. History needs to be filled.
