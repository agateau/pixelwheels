# Translations

So you want to translate Pixel Wheels to a new language? Awesome! Read on.

## Requirements

## Adding a new translation

At the moment adding a translation requires building the game. This might change in the future, but for now follow the [building documentation](building.md) for details.

- Create a fork of [PixelWheels GitHub project][github] and clone it.

- Copy `android/assets/po/messages.pot` to `android/assets/po/{language}.po` (or `android/assets/po/{language}_{COUNTRY}.po` if you want to create a country-specific variant).

- Translate the strings in the new .po file using a PO editor ([poedit][], [lokalize][], or even a plain text editor).

- Edit `core/src/com/agateau/pixelwheels/Language.java`: add your translation to the `ALL` array.

- Add your translation to the "Translation" section of `android/assets/screens/credits.gdxui`. Run `make po-update` for the new string to appear in your .po file, then translate it.

- Test your translation (see below).

- When you are happy with the result, file a pull request to get your work integrated. You're done!

[poedit]: https://www.poedit.net/

[lokalize]: https://apps.kde.org/lokalize/

[github]: https://github.com/agateau/pixelwheels

## Updating a translation

If new strings have been added or existing strings have been modified but they do not appear in your .po file, run `make po-update` to refresh the .po files, then update your .po file.

## Testing a translation

If you built the game manually run the game with `make run` or from Android Studio.

If you are using a pre-built version of the game, start it with the environment variable `PW_PO_DIR` set to the path to your `android/assets/po` directory. This way Pixel Wheels will load the translation from your directory instead of the ones currently included in the game itself.

If you are running on a machine whose default language is the language you translated Pixel Wheels to, then your translation should already be in use. If not see the next section.

Visit all the screens to catch issues like button text overflowing their buttons.

Go to the settings screen. Check you can switch between your translation and the others.

## Ensuring your translation is automatically picked up

Having the game translated is great, but it's even better if the game automatically starts in the player language without requiring them to go to the settings screen.

To check this works, do the following:

- Edit `~/.config/agateau.com/pixelwheels.conf` and remove the `languageId` line if it exists. Doing this ensures Pixel Wheels is not configured to use a particular language.

- Start the game, it should start using your translation. If it does not, look at the console output: Pixel Wheels logs the name of the translation it is looking for. Rename your .po file to match this name. Rebuild the game and try again.
