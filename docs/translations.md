# Translations

## Adding a new translation

- Copy `po/messages.pot` to `po/{language}.po` (or `po/{language}_{COUNTRY}.po` if you want to create a country-specific variant).

- Translate the strings in the new .po file using a PO editor ([poedit][], [lokalize][], or even a plain text editor).

- Edit `core/src/com/agateau/pixelwheels/Language.java`: add your translation to the `ALL` array.

- Add your translation to `android/assets/screens/credits.gdxui`.

[poedit]: https://www.poedit.net/

[lokalize]: https://apps.kde.org/lokalize/

## Testing a translation

This requires building the game, see the [building documentation](building.md) for details.

## Updating a translation

If new strings have been added or existing strings have been modified but they do not appear in your .po file, run `make po-update` to refresh .po files.
