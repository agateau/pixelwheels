# Pixel Wheels

![Build Status](https://github.com/agateau/pixelwheels/workflows/main/badge.svg)

Pixel Wheels is a retro top-down race game for Linux, macOS, Windows and Android.

![Screenshot](fastlane/metadata/android/en-US/images/phoneScreenshots/4-gun.png)

It features multiple tracks and vehicles. And various bonus and weapons to help you get to the finish line first!

On PC, you can play Pixel Wheels alone or with a friend.

## I want to try it!

Great! For Linux, macOS and Windows you can get binaries from [Pixel Wheels page on itch.io][itch].

_ARM-based Mac users: Note that Pixel Wheels does not run on ARM-based Macs for now. This will be added when the game is updated to use [libGDX 1.11.0][libgdx-arm-mac]._

[libgdx-arm-mac]: https://github.com/libgdx/libgdx/releases/tag/1.11.0

For Android you can install Pixel Wheels from [F-Droid][fd], [Google Play][gplay] or from [itch.io][itch] too.

[itch]: https://agateau.itch.io/pixelwheels
[fd]: https://f-droid.org/packages/com.agateau.tinywheels.android/
[gplay]: https://play.google.com/store/apps/details?id=com.agateau.tinywheels.android

### master builds

If you feel adventurous, you can also try binaries from the master branch. These are available from [builds.agateau.com][builds-agateau].

[builds-agateau]: https://builds.agateau.com/pixelwheels

## I think it would be much better if it did X, Y or Z...

I have a reasonably well defined vision of what I want Pixel Wheels to be. Your feedback and suggestions are welcome, and I will look into them, but I reserve the right to decide if your ideas fit with the game I am trying to create.

## Contributing

The [docs](docs/) directory contains documentation to work on Pixel Wheels.

In particular:

- [Adding or updating a translation](docs/translations.md)
- [Building instructions](docs/building.md)

[build]: docs/building.md

## License

- The game logic is licensed under GPL 3.0 or later. This is all the code in [core/src/com/agateau/pixelwheels](core/src/com/agateau/pixelwheels) directory.
- The rest of the code is licensed under Apache 2.0.
- Assets are licensed under Creative Commons BY-SA 4.0.

The rationale behind this combination of licenses is to:

- Allow reuse of all the code and assets by free software projects.
- Allow reuse of utility code in proprietary projects.
- Prevent appearance of ad-based, malicious, proprietary clones of the game.

Put another way, if you are a game developer and find some of the code interesting, feel free to use it to build your *own* original project. If you are interested in some of the GPL code, get in touch, I am open to relicensing.

On the other hand, if your plan is to take the game, slap some ads on it, and release it without releasing the sources of your changes: the license forbids you to do so, go find another prey.

## Why is the Java package called "tinywheels"?

The game used to be called Tiny Wheels, but I found out there is already a Tiny Wheels game on Steam, so I add to rename it. I did not change the Java package name however because Google Play does not allow changing the package name of an existing app or game.

## Support development of the game

Want to support the development of the game? Awesome! My [support page][support] is waiting for you :)

[support]: https://agateau.com/support/
