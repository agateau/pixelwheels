# Changelog

## 1.0.0-rc.1 - 2025-07-14

### Added

- There are now 3 difficulty levels: Casual, Pro and Legendary.

- Added a new vehicle: the C15!

### Changed

- More vehicles now need to be unlocked: Ant On-1, Pickup, 2-deuch and Roadster.

- Tweaked steering, acceleration and turbo settings to make vehicles easier to control.

- Improved country tracks.

- Screenshots are now stored in a "screenshots" subdirectory of the local config directory.

### Fixed

- Fixed the button to open the shop or the support pages not working on recent Android versions.

## 0.26.0 - 2024-09-03

### Added

- The Pixel City championship got its 3rd track: The Island.

- The "rotate camera" option is back! Thanks to Compl Yue for this work!

- Pixel Wheels now speaks Galician, thanks to Ninjum!

### Changed

- In the menus, the "back" and "next" buttons are now reachable via keyboard navigation.

- The AI pilot got a bit smarter: it knows about the different ground materials and tries to overtake vehicles instead of bumping into them.

- The Spanish translation is now maintained by Victor Hck.

- The behavior when hit by a bullet, missile or when triggering a mine is now less punishing. Instead of doing a 360 and getting pushed away, the vehicle motor temporarily shuts down and emits smoke (#418).

- The support part of the configuration screen has been moved to a nicer, dedicated "SUPPORT" screen. This screen provide links to Pixel Wheels shop and to the support page (on builds where it is authorized).

- The BRGP42 and Rocket vehicles are now a bit faster.

- Steering is now smoother, especially on Android.

### Removed

- Desktop: moving mouse over a menu item no longer makes it the current item.

### Fixed

- Helicopter behavior has been improved: there should no longer be stuck helicopters, or continuously playing helicopter sounds.

## 0.25.1 - 2023-09-17

### Fixed

- Fixed an issue where the game would complain about a missing controller when started on Android (#398).

- In Esperanto (and potentially other languages), the score table no longer overflows to the right of the screen (#397).

## 0.25.0 - 2023-08-11

### Added

- Pixel Wheels now speaks:
    - Italian, thanks to Dario Canossi!
    - Dutch, thanks to Heimen Stoffels!

- A new vehicle has been added: the BRGP42, an old F1 inspired from the Lola Mk4.

### Changed

- Multi-player has been changed to use a split-screen again and supports up to 4 players (#308).

- Support for hardware keyboards on Android has been improved (#343).

- The screenshot key is now `F9`, not `S`, and is configurable. Not using `S` makes it possible to configure the game to use `WASD` keys.

### Fixed

- Fixed a crash which would happen when configuring one of Player 1 keys to Up (#326).

- Fixed a hole in the tire barrier in the "Flood" track which could be used to cut a large part of the track (#336).

- After changing from language A to language B and then back to language A, when returning to the main menu the screen would be empty (#339).

- In the "Up, up, up and down" track, there were holes between trees and rocks which made it possible to cut large parts of the track. New trees have grown there, blocking the holes (#337, #345, #346).

- The text explaining how to unlock vehicles is now correctly updated when the language changes (#344).

- Disabled the language selector while playing to avoid returning to a menu filled with black rectangles (#348).

- Gamepad now has a dedicated "Pause" button. This allows player to use the "Back" button to brake.

- Fixed two areas in "Up, up, up and down!" which would hide vehicles (#363).

- Missiles are no longer drawn over walls or bridges (#58, #362).

- Obstacle shadows are now drawn on the bottom-right of the obstacle, like the rest of the game objects.

- Vehicles are no longer drawn slightly rotated on the start line, making them look better.

## 0.24.2 - 2023-02-02

### Added

- Pixel Wheels is now available in:
    - Hungarian, thanks to Balázs Úr!
    - Esperanto, thanks to Jorge Maldonado Ventura!
    - Turkish, thanks to nxjosephofficial!

### Fixed

- The display would sometimes stutter during races. That should no longer happen.

- In championships, the player rank could sometimes be wrong in the 2nd or 3rd race.

### Developers

- Logging system has been reworked: it no longer goes through Gdx logging system. This fixes log messages arriving out of order.

## 0.24.1 - 2022-12-13

### Fixed

- Fix Chinese translation looking wrong because of missing glyphs in the font (#297).

- On devices with a screen ratio different from 16:9 the vehicle selection screen was stretched and made the buttons non responsive (#296).

- Fixed a bug in the rescue copter mechanism: on the River track, after being dropped by the rescue copter on the bridge, the rescued vehicle would sometimes go through the bridge wall and fall on the other side (#302).

- On the River track, fixed wrong river edge tiles on the right side of the map.

## 0.24.0 - 2022-11-22

### Added

- There is a new track in the Square Mountains championship. In this new track you race in the snowy forest. Be careful not to hit a tree! (#182).

- A new vehicle has been added: the Broster Truck!

- Pixel Wheels is now translated in Swedish, thanks to Sanchez.

- On Desktop, Pixel Wheels now honors the XDG Base Directory environment variables (`XDG_CONFIG_HOME`, `XDG_DATA_HOME`, `XDG_CACHE_HOME`). The configuration file has moved from `.config/agateau.com/pixelwheels.conf` to `$XDG_CONFIG_HOME/pixelwheels/pixelwheels.conf`. It should be automatically migrated.

### Changed

- The way vehicles are drawn has been improved: vehicle shadows now include shadow for the tires, and when a vehicle goes underwater, its wheels are still drawn.

- Spanish translation received some improvements (Victorhck, #262).

- Driving behavior has been tweaked to be more pleasant.

- It is now possible to pause the game using the 2nd gamepad button.

### Fixed

- On Android TV, Pixel Wheels is now listed in games, not in apps (#276).

- Text no longer overflows its button in the gamepad configuration screen (#224).

### Developers

- Enabling some gameplay-affecting debug settings no longer prevents using locked tracks or vehicles.

- Updated Android SDK to 31, Android Gradle Plugin to 7.2.2, Gradle to 7.3.3 and JDK to 11 (#275).

## 0.23.0 - 2022-07-03

### Added

- The Country championship has a new track: Flood (#181).

- Pixel Wheels is now translated in:
    - Basque, thanks to Josu Igoa.
    - German, thanks to Christian Schrötter.

- Added a new vehicle: the Miramar.

- Championships have their own icon now, instead of using the icon of their first track.

- Linux: the archive now includes an `install.sh` script. This script can be used to install the game and setup a launcher icon for it. It is still possible to run the game without installation, though.

- Linux: It's now possible to `make install` the game.

### Changed

- Languages are now selected from a dedicated screen instead of in a selector menu. This makes it easier to see all available languages, and faster to select one.

- The vehicle selection screen now looks nicer.

### Fixed

- Mines no longer look like they are floating when dropped in water.

- Skid-marks are no longer drawn on top of water.

- Fixed Polish translation not loading.

- Fixed crash when there is only one racer (only possible using developer options for now) (#209).

- It is now possible to translate championship, track and vehicle names (#168).

- The "Open log file folder" now works on Windows (#225).

### Developers

- Started using Changie to manage the CHANGELOG <https://changie.dev/guide/changelog/>.

- Track waypoints are now defined using a polyline instead of independent points.

- GdxPrinter logs now include a timestamp.

## 0.22.0 - 2022-01-30

### Added

- Five new translations:
    - Bengali by Oymate.
    - Chinese by Lu Xu.
    - Polish by PandaCoderPL.
    - Russian by Nickoriginal.
    - Spanish by Clara Gâteau.

- Keyboard keys are now configurable (#129).

### Fixed

- Reworked Rocket car so that it cannot get stuck on thin walls (#174).

- The ambiguous "You broke a record" message has been turned into a more generic one, which shows when the player ranks 3rd or better (#139).

- On Android, Pixel Wheels now uses an adaptive icon (#83).

- Added a workaround for the Android 11 status bar not disappearing when switching between apps (#140).

### Developers

- It is now possible to define the PW_ASSETS_DIR environment variable to make Pixel Wheels uses assets directly from there instead of using the bundled assets.

- Bumped Android target SDK version to 30.

## 0.21.0 - 2021-10-04

### Added

- Pixel Wheels is now translatable. The game is available in French and English (#57).

- There is now a button to help reporting a bug.

### Changed

- The Configuration screen has been reorganized.

- Multiple performance improvements.

### Fixed

- On the finished overlay, the car congratulating you when breaking a best lap or total time record no longer overlaps with the menu buttons on wide screens (#126).

- On Android, in the "championship finished" screen, the "→" button works again (#119).

- Fix finished overlay ignoring CPU times when showing medals (#131).

- Android 10: fix general slowdown caused by a bug in Android sound libraries (#130).

- Do not record scores when developer settings affecting gameplay are changed. For real this time (#35)...

- Prevent missile farming: do not count missile hits on vehicles which have finished the race (#128).

### Developers

- Added new Makefile targets: backup-desktop-conf and restore-desktop-conf.

- Make GitHub CI build and upload apks in addition to desktop zip files (#105).

- Log files are now rotated.

## 0.20.0 - 2021-06-26

### Added

- All tracks now have predefined best lap and best total time records. This way the first time a player races on a track they are less likely to break "best lap" and "best total time" records (#116).

### Changed

- Reworked the way the game tells the player when they broke a personal record. In the race time table screen, a car drives in congratulating the player, then record indicators arrive next to the matching time in the time table (#111).

- New menu music: Lightspeed by Thomas Tripon.

- Reworked Antonin car design: looks like a surf minibus now. Renamed it to something a bit more obscure (#122).

### Fixed

- Select track screen: hide record tables for locked tracks so that they no longer overlap with the unlock text (#110).

- Select championship screen: make championship track list visible again.

- Do not record scores when developer settings affecting gameplay are changed (#35).

- Fixed gamepad support on Android (#117).

- Fixed crash when saving screenshot on Windows (#120).

- Fixed crash when trying to skip the "championship finished" animation (#118).

- Reduced volume of countdown sounds so they feels less over-saturated on Android.

- Made sure best lap time is never more than `total_time / lap_count` (#106).

### Developers

- Documented gdxui and AnimScript formats.

- asetools: if layer group is invisible, consider all its layers invisible.

- Updated to libgdx 1.9.1.

- Updated to gdx-controllers 2.2.0.

- Championships and tracks are now defined in XML files, making it possible to create or modify championships and tracks without changing the code.

## 0.19.1 - 2021-04-17

### Added

- Provide standalone archives to install Pixel Wheels on Linux, macOS and Windows. It's no longer necessary to install a JRE (Java Runtime Environment) to run the game (#99).

### Fixed

- Do not allow pressing the Next button when the current vehicle/track/championship is locked (#108).

- Made Android TV launcher icon more readable (#102).

### Changed

- Improved archive README: fixed outdated info and broken link, removed "Starting the game" section.

- Created a Google Play flavor of the game, which does not mention donations in the configuration screen, to comply with Google Play terms of service.

### Developers

- Documented the `AGC_UI_TYPE` environment variable.

- Updated Gradle to 6.5 and Android Gradle plugin to 4.1.3.

## 0.19.0 - 2021-02-24

### Added

- Added musics. Thanks a ton to FoxSynergy for publishing these great pieces on opengameart (#98)!

### Changed

- Many small animations have been added: the police car lights flash, the rank change indicators in the score table have changing colors, the flags behind the planes in the championship finished screen move.

- Split the "mute" option into "mute music" and "mute sound".

### Fixed

- Bonus balance has been adjusted: it is now impossible to get a turbo when ranked first, to avoid making best scores too random (#46).

- Fixed credits text being cropped at the bottom.

- Font authors were missing from credits, added them.

- Replaced the Aero font with Kwajong because Aero license made it non-suitable for FOSS games.

- Menu items now do not autofocus themselves on mouse-over unless the mouse cursor is visible (#103).

- Do not show the pause overlay when pressing the pause shortcut after the end of the race (#97).

### For developers

- asesplit: Added support for linked cells and implemented sprite rotation.

- Added support for animated images.

- Sometimes the reward unlocked screen appeared for already unlocked rewards. It only happened after a refresh (using F5 to debug a screen) (#100).

- The  CI now runs the tests (was about time!).

## 0.18.0 - 2020-12-20

### Added

- Added new track: "River". Now all championships have 2 tracks (#96).

- Added Android TV support (#89).

- Added "Total time" column to "championship finished" screens (#42).

### Changed

- Do not show points in quick race finish screen (#43).

- Improved plane animations in "championship finished" screen.

- Improved championship ranking table: added point increase "animation", show milliseconds in race times, use images to show rank changes.

- Use edg32 palette for the country tileset.

### Fixed

- Fixed locked vehicles being selectable (#86).

- Brought back lap records in the track selection screen (#90).

- When finishing a race in championship mode, show race and then championship ranking tables instead of only the championship one (#41).

- Made water borders thicker to ensure players do not feel their vehicle got "stuck" under water (#95).

- Improved rocks appearance in city tileset so that they do not look like they float anymore and added an obstacle object where relevant so vehicles cannot go under rocks (#93).

- Normalized position of the finish lines (#92).

### For developers

- Bumped targetSdkVersion to 29. 29 is required by Google since November 2nd.

- Extracted tilesets to tsx files: no more duplication of tile properties.

- Made track creation easier: improved documentation and error messages.

- Added an `auto-assets` target to automatically regenerate assets.

- Added a way to read configuration values from .gdxui files.

- Added a way to quickly debug FinishedOverlay.

## 0.17.0 - 2020-08-23

### For players

#### Added

- Added a new vehicle: the Jeep.

- Added an "About" tab to the configuration screen.

#### Changed

- Did a lot of work on the graphics: darker roads, new trees, new city roofs, textured sidewalks, nicer bridge borders, rounder sand tiles.

- For consistency, made all menu buttons ALL CAPS.

- Show "RESTART" button first in quick race mode.

#### Fixed

- Fixed noise on map tiles by enabling linear filtering on map textures.

### For developers

- Made asetools work with Python 3.5. This was required for F-Droid build infrastructure.
- Documented Ifdef/Else UiBuilder tags.

#### Changed

## 0.16.1 - 2020-06-27

### For developers

#### Changed

- Replaced usage of Aseprite in batch mode with a Python-based tool to generate the png from .ase files. Aseprite is no longer a build dependency for the game.

## 0.16.0 - 2020-04-19

### For players

#### Added

- Added a new championship called "Country Life". Contains only one race for now: "Welcome".

- In quick race mode, added a "Restart" button after finishing a race (#27).

- Added a new car: the Dark M.

#### Fixed

- Android: It is now possible to move the game to the SD card (#82).

### For developers

- Added support for defining menu items inside .gdxui files instead of defining them in code. Most menus are now defined this way, making it possible to adjust them without restarting the game.

- Updated libgdx to 1.9.10.

- Updated Pillow to 7.1.1.

## 0.15.0 - 2020-02-09

### For players

#### Added

- Added moving and still obstacles to the tracks. For now the only obstacles are tyres (movable) and concrete blocks (still, replace the existing blocks).

#### Changed

- Made AI smarter: AI vehicles now get stuck less often and try to avoid mines.

- Improved Rocket axle configuration to make it is easier to get away from a wall after hitting it.

- Made missiles smarter: take obstacles into account when deciding if a target can be reached (#39).

#### Fixed

- Fixed helicopter being stuck on the map (#80).

- Made hardware keyboard work on Android (#20).

- Made sure one cannot get out of the map through the sea.

- Do not play collision sound when the vehicle has the missile bonus on its roof (#81).

- Retina screens: fixed rendering filling only 1/4 of the screen.

- Fixed Pause button not working on mobile (#79).

- Documented how to start the game on macOS (#78).

### For developers

#### Added

- Added a debug mine dropper to place mines by clicking on the map.

- Added debug option to control the camera using keyboard.

## 0.14.1 - 2019-10-28

### For players

#### Fixed

- Fixed crash when clicking on a label item (found out by Google Play automated monkey testing!)

## 0.14.0 - 2019-10-28

### For players

#### Added

- [Mobile] A new touch input mode has been added: "Side buttons". It shows left and right steering buttons in the bottom corners of the screen and a trigger bonus button in the middle right of the screen. Hold left and right simultaneously to go backward.

- [Desktop] The mouse cursor is no longer invisible. It auto-hides after a few seconds (#29).

- [Desktop] The main menu now contains a Quit button.

- There is now a sound when driving in water.

- The road sign in the bottom-right corner of the main menu now shows Pixel Wheels version number.

- Tiny-sur-mer track: Several holes have been plugged and missing fences have been added (#48).

#### Changed

- Startup count down is now slightly faster.

- Adjusted the probabilities of bonuses so that all bonuses have a chance to be selected. You still have more chance to get a mine than a gun or missile when your rank is high (#56, by Tim Schumacher).

- Touching or clicking a selected image validates the user choice (By Julien Bolard).

- [Desktop] UI elements now gets focus when hovering over them with the mouse (By Tim Schumacher).

#### Fixed

- Fixed some HUD input buttons not showing until one second on screen.

- Fixed sound not coming back after pressing restart (#22).

- Use the vehicle name instead of the vehicle id in the championship finished screen (#51).

- Do not show negative times in finished overlay (#38).

- Fixed vehicles not slowing down when driving backwards in deep water (#54).

- Activating a bonus requires a button press instead of holding the button (#56, by Tim Schumacher).

- The back button in the game mode selection screen is now clickable (by Tim Schumacher).

- Fixed crash when pressing up/down in the keyboard config screen (by Tim Schumacher).

- Fixed Continue buttons in track and vehicle selection screens not working (by Tim Schumacher).

### For developers

- Added doc explaining how to setup Android Studio run targets.

- Centralized definition of version number in a single file.

- Setup Travis CI to build Pixel Wheels.

- Added scripts to download and build a headless version of Aseprite, usable to render assets.

- Decided to use the AOSP coding style, and set up google-java-format to enforce this.

## 0.13.0 - 2019-08-07

### For players

#### Added

- A nice animation has been added when you finish a championship in the top 3.

- On Desktop, Pixel Wheels window now has a proper icon.

#### Changed

- The Harvester has been tuned to be easier to drive.

- The unlocked reward animation has been improved. Vehicles and championships
  now use different animations.

- It's no longer possible to get an impossibly short best lap time by
  backtracking after crossing the finish line (Bug #21)

- If a vehicle crosses deep water but has enough momentum to reach the other
  side, it no longer sinks.

- In championship mode the pause overlay does not contain a "Restart" button
  anymore.

- Corners of the Android launcher icon are now rounded.

- Fixed the bug which caused white vertical lines to show up from time to time
  on the road.

### For developers

#### Added

- Added a way to quickly test ChampionshipFinishedScreen and
  UnlockedRewardScreen (see docs/debug.md)

- Started using Fastlane to update Google Play and F-Droid pages.

- Added a `check` Makefile target to run tests and linters. Made it part of the
  release check list to avoid shipping with tests which do not even compile...

#### Changed

- A bunch of updates have been done: Java 1.8, Gradle 4.10.2 (5.1 breaks too
  many things for now), targetSdkVersion 28

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
  assets, see [building.md](docs/building.md) for details.

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
