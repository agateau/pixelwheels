# Pixel Wheels

[![Build Status](https://travis-ci.org/agateau/pixelwheels.svg?branch=master)](https://travis-ci.org/agateau/pixelwheels)

Pixel Wheels is a retro top-down race game.

You can play Pixel Wheels alone or with a friend.

![Screenshot](fastlane/metadata/android/en-US/images/phoneScreenshots/mine-drop.png)

It runs on Linux, macOS, Windows and Android.

## Attention! Warning! Achtung!

This game is far from being finished. Some mechanics of the gameplay are not
there yet, and the game still needs much work in just about every aspects:
graphics, sounds, gameplay...

## I don't care, I want to try it anyway

Great! For Linux, macOS and Windows you can get binaries from [Pixel Wheels
home page][pw].

For Android you can install Pixel Wheels from [F-Droid][fd] or [Google
Play][gplay].

[pw]: http://agateau.com/projects/pixelwheels
[fd]: https://f-droid.org/fr/packages/com.agateau.tinywheels.android/
[gplay]: https://play.google.com/apps/testing/com.agateau.tinywheels.android


## I think it would be much better if it did X, Y or Z! Please make it happen!

I have a reasonably well defined vision of what I want Pixel Wheels to be.
Your (constructive) feedback and suggestions are welcome, and I will look into
it with great attention, but I reserve the right to decide if your ideas fit
with the game I am trying to create.

## What if I want to build it myself?

The [build instructions][build] should have you covered.

[build]: doc/building.md

## License

- The game logic is licensed under GPL 3.0 or later. This is all the code in
  [core/src/com/agateau/pixelwheels](core/src/com/agateau/pixelwheels).
- The rest of the code is licensed under Apache 2.0.
- Assets are licensed under Creative Commons BY-SA 4.0.

The rational behind this setup is to:

- allow reuse of all the code and assets by free software projects.
- allow reuse of utility code in proprietary projects.
- prevent ad-based, proprietary clones of the game.

Put another way, if you are a game developer and find some of the code
interesting, feel free to use it to build your *own* original project. If you
are interested in some of the GPL code, get in touch, I am open to relicensing.

On the other hand, if your plan is to take the game, slap some ads on it, and
release it without releasing the sources of your changes: the license forbids
you to do so, go find another prey.

## Why is the Java package called "tinywheels"

The game used to be called Tiny Wheels, but I found out there is already a Tiny
Wheels game on Steam, so I add to rename it. I did not change the Java package 
name however because Google Play does not allow changing the package name of an
existing app or game.

## Author

Aurélien Gâteau
