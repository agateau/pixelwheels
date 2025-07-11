# Translations

So you want to translate Pixel Wheels to a new language? Awesome! Read on.

The simplest way is to use [Toolate](https://toolate.othing.xyz/projects/pixel-wheels/), a web-based translation tool.

<a href="https://toolate.othing.xyz/projects/pixel-wheels/">
<img alt="Translation status" src="https://toolate.othing.xyz/widget/pixel-wheels/multi-auto.svg"/>
</a>

Alternatively you can translate manually.

## Requirements

To translate Pixel Wheels you need:

- A pre-compiled version of the game `master` branch (you can find them here: <https://builds.agateau.com/pixelwheels>) or a self-built version.
- A PO editor like [poedit][] or [lokalize][], or a plain text editor.
- To be familiar with Git and GitHub.

## Adding a new translation

- Create a fork of [PixelWheels GitHub project][github] and clone it.

- Copy `android/assets/po/messages.pot` to `android/assets/po/{language}.po` (or `android/assets/po/{language}_{COUNTRY}.po` if you want to create a country-specific variant).

- Translate the strings in the new .po file using your PO editor of choice.

- Edit `android/assets/ui/languages.xml`: add a new `Language` element for your translation.

- Add your translation to the "Translation" section of `android/assets/screens/credits.gdxui`. Run `make po-update` for the new string to appear in your .po file, then translate it.

- Test your translation (see below).

- When you are happy with the result, file a pull request to get your work integrated. You're done!

[poedit]: https://www.poedit.net/

[lokalize]: https://apps.kde.org/lokalize/

[github]: https://github.com/agateau/pixelwheels

## Updating a translation

If new strings have been added or existing strings have been modified but do not appear in your .po file, run `make po-update` to get these new strings in the .po files. You can now translate the new strings.

## Testing a translation

If you built the game manually, you can run the game with `make run` or from Android Studio.

If you are using a pre-built version of the game, start it with the `PW_ASSETS_DIR` environment variable set to the path of your `android/assets` directory. This way Pixel Wheels will load the translation from your directory instead of the ones currently included in the game itself.

If you are running on a machine whose default language is the language you translated Pixel Wheels to, then your translation should already be in use. If not see the next section.

Visit all the screens to catch issues like button texts overflowing button frames or overlapping texts.

Go to the settings screen. Check you can switch between your translation and the others.

## Ensuring your translation is automatically picked up

Having the game translated is great, but it's even better if the game automatically starts in the player language without requiring them to go to the settings screen.

To check this works, do the following:

- Edit `~/.config/agateau.com/pixelwheels.conf` and remove the `languageId` line if it exists. Doing this ensures Pixel Wheels is not configured to use a particular language.

- Start the game, it should start using your translation. If it does not, look at the console output: Pixel Wheels logs the name of the translation it is looking for. Rename your .po file to match this name. Rebuild the game and try again.

## Translating track, championship and vehicle names

Track, championship and vehicle names can be translated, but it's up to you to decide if it makes sense to do so for the language you are translating to. Some names can benefit from being translated, others can stay in English.
