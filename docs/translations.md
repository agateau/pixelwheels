# Translations

## Adding a new translation

Copy `po/messages.pot` to `po/your_LOCALE.po`

Translate the strings in the new .po file using a PO editor ([poedit][], [lokalize][], or even a plain text editor)

[poedit]: https://www.poedit.net/

[lokalize]: https://apps.kde.org/lokalize/

## Testing a translation

This requires building the game, see the [building documentation](building.md) for details.

## Updating a translation

If new strings have been added or existing strings have been modified but they do not appear in your .po file, run `make po-update` to refresh .po files.
