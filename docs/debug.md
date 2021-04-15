# Debugging facilities

## PW_DEBUG_SCREEN environment variable

This environment variable can be set to one of the following values to test a
specific screen:

- `ChampionshipFinished:podium` or `ChampionshipFinished:nopodium`:
  championship finished
- `UnlockedVehicle:<id>`: unlocked screen for vehicle `<id>`
- `UnlockedChampionship:<id>`: unlocked screen for championship `<id>`
- `FinishedOverlay`: show the FinishedOverlay

## AGC_UI_TYPE

Overrides the UI type. Can be one of:
- BUTTONS: Desktop
- TOUCH: Default for smart phones
