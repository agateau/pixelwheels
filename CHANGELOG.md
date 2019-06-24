# Changelog

## 0.12.0 - 2019-06-17

### For players

#### Added

- 3 new vehicles: Rocket, Harvester and 2CV!

- A locking system has been added: some vehicles and the second championship
  (Pix Cities) must now be unlocked.

#### Changed

- Tracks and championships now have nicer names.

- Multiplayer is now all on one screen: the camera zooms out so that all
  players are visible.

- Tracks are now represented using a hand-made icon, instead of screenshots.
  This looks sharper and makes life easier for F-Droid packagers.

- Selecting keyboard "config" now shows which keys are used.

- Many small improvements on sound, map layouts, animations in the UI...

#### Removed

- The "multiplayer all on one screen" change required removing the "rotate
  screen" option: it does not make sense to rotate screens if it is shared
  between players.

### For developers

#### Added

- Documented how to create vehicles.

- Made it easier to tweak assets: they are now reloaded when when pressing F5.

#### Changed

## 0.11.0 - 2018-12-16

### For players

#### Added

- Pixel Wheels now remembers the best lap and best total time for each track
  and shows you a congratulation message when you reach the top 3 in either
  categories.

- Sounds have been added to the start up countdown.

#### Changed

- The faster the vehicle is driving the more zoomed out the view is, giving you
  more time to anticipate the track.

- The game now shows a blocking message if there aren't enough gamepads to
  start a game, or if a gamepad is disconnected while playing.

- The focus indicator in the game menu has been reimplemented: now each menu
  item has its own, fading, focus indicator. This fixes the focus indicator
  glitches when a screen appeared or when switching between tabs in the
  configuration screen.

- The screens to select vehicles, tracks and championships now show the name of
  the selected element. Furthermore, the track selection screen shows the
  track records.

#### Fixed

- Fixed glitches when changing tabs in the configuration screen.

### For developers

#### Added

- Added secret key shortcut to save a screenshot ('S' for now).

#### Changed

- Switched desktop version to LWJGL3.

- Updated libgdx to 1.9.8.

- Make Google Play happy: bump `targetSdkVersion` to 26.


## 0.10.0 - 2018-09-09

### For players

#### Added

- Added a new bonus: the homing missile.

- Enabled gamepad on Android.

#### Changed

- Camera does not rotate to follow the player vehicle anymore by default.  As
  much as I like this feature, almost every player prefers when the camera does
  not rotate, so let's change the default.
  It's still available as an option.

- Redraw all cars and all bonuses using the edg32 color palette from Endesga.

- Adjusted the design of the police car so that the front and rear are easier
  to distinguish.

- Made touch handler more progressive, similar to the keyboard handler.

- Bonus crates are bigger now, making them easier to grab.

- Turbo and maximum speed have been reduced a bit.

- You can now destroy mines (and missiles!) using the gun. This also means that
  when you picked a mine bonus but you haven't dropped it yet, it can protect
  you from one gun bullet or missile.

- Enabled vsync for less tearing.

- Made gamepad buttons configurable.

#### Fixed

- Fixed HUD buttons being huge on hi-dpi devices.

- Made vehicle collision less strange.

- Fixed detection of gamepad horizontal and vertical axis.

### For developers

- It is now possible to use LibreSprite instead of Aseprite to generate the
  assets, see [building.md](doc/building.md) for details.

## 0.9.0 - 2018-07-04

### For players

#### Added

- You can finally play the game and navigate the user interface using gamepads.

#### Changed

- Steering is more progressive, making it easier to make small direction
  adjustments.

#### Fixed

- Skidmarks disappear more progressively now.

- Fix screen shaking too much when spinning with rotate screen disabled.

## 0.8.0 - 2018-05-07

### For players

#### Added

- Championships! A first rough version of championships has been added: since
  there are 4 tracks in Pixel Wheels at the moment, they have been grouped in 2
  championships of 2 tracks each.

- The volumes of the sound effects have been adjusted to avoid cacophony.

- A fullscreen option has been added.

- There is now a 3 second countdown at the start of each race.

#### Changed

- Moved config file from ~/.prefs/com.agateau.tinywheels to
  ~/.config/agateau.com/pixelwheels.conf.

#### Fixed

- In score tables, player rows now use different colors.

- Mouse cursor is now always hidden.

### For developers

- libgdx has been updated to 1.8.0.

- Package name has been changed from com.agateau.tinywheels to
  com.agateau.pixelwheels (but the Android applicationId remains stays set to
  com.agateau.tinywheels.android so that the game can be updated on Google Play)

## 0.7.0 - 2018-04-02

### For players

#### Added

- There is a new snow race. It's full of icy patches, be careful!

- The rescue helicopter now has a sound.

- If you don't like Pixel Wheels sounds, you can now mute them from the
  Settings screen.

#### Changed

- Bots have finally learned how to use the reverse gear to unblock themselves,
  you won't be able to score easy wins because they got stuck against a wall.
  (Issue #2)

- I made the vehicles a bit slower, they should be easier to handle now,
  especially on Android.

- The Android icon was a bit crude, I redrew it to something nicer.

#### Fixed

- Sometimes skid marks would be kept from one race to another, this is no
  longer the case.

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
