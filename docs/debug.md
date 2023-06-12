# Debugging facilities

## PW_DEBUG_SCREEN environment variable

This environment variable can be set to one of the following values to test a
specific screen:

- `ChampionshipFinished:podium` or `ChampionshipFinished:nopodium`:
  championship finished
- `UnlockedVehicle:<id>`: unlocked screen for vehicle `<id>`
- `UnlockedChampionship:<id>`: unlocked screen for championship `<id>`
- `FinishedOverlay:<best-lap-rank-record>:<total-time-rank-record>`: show the FinishedOverlay.
    - If `<best-lap-rank-record>` is a number between 0 and 2 it indicates a new rank in the best lap record
    - If `<total-time-rank-record>` is a number between 0 and 2 it indicates a new rank in the total time record

## PW_ASSETS_DIR environment variable

When set to an existing directory, Pixel Wheels looks into this directory first when loading assets. If an asset is not found, the game falls back to the default directory.

## AGC_UI_TYPE environment variable

Overrides the UI type. Can be one of:

- BUTTONS: Desktop
- TOUCH: Default for smart phones
