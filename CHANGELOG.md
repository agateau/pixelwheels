# Changelog

## 0.7.0 - 2018-04-01

### For players

#### Added

- There is a new snow race. It's full of icy patches, be careful!

- The rescue helicopter now has a sound.

- If you don't like PixelWheels sounds, you can now mute them from the Settings
  screen.

#### Changed

- Bots have finally learned how to use the reverse gear to unblock themselves,
  you won't be able to score easy wins because they got stuck against a wall.
  (Issue #2)

- The Android icon was a bit crude, I redrew it to something nicer.

#### Fixed

- Sometimes skid marks would be kept from one race to another, this is no longer
  the case.

- There was a few holes around bridges on the "Tiny sur Mer" map. They have
  been closed (Issue #16)

### For developers

- libgdx has been updated to 1.7.2.

- Code is now grouped into packages, making it a little less messy!

- The update to libgdx 1.7.2 required an update of my fork of MapPacker, but
  instead I decided to use the maps directly instead of pre-processing them to
  load them through AtlasTmxMapLoader. This means there is no more MapPacker
  fork in the code and no need to run it before testing a map, just save from
  Tiled and run the game.

## 0.6.0 - 2018-02-03

### Added
- Added sounds for vehicle engines, collision, drifting, picking bonuses, and
  bonuses themselves.

### Changed
- Renamed the game to Pixel Wheels, to avoid confusion with the already
  existing Tiny Wheels game.

- Finished porting the last screens to the menu systems. Pixel Wheels is now
  entirely controllable from the keyboard.

#### Internals
- Updated to libgdx 1.6.5.

### Fixed
- Make Android Back button toggle pause while playing.

## 0.5.0 - 2018-01-06

### Added
- On Android devices without physical navigation buttons, hide the on-screen
  buttons. They can be revealed by swiping from the edge to the center, like in
  most games.

- The driving buttons have been reworked: they are now larger and look like
  sectors in the bottom-left and bottom-right corners of the screen.

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
