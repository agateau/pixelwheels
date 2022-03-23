# Adding a new vehicle

## Steps

- Create the vehicle image in core/assets-src/sprites/vehicles
- Create the XML description in android/assets/vehicles
- Add the vehicle id to `Assets.VEHICLE_IDS`
- Add the rules for the vehicle to `RewardManagerSetup`
- `make assets packer run`

## vehicle XML format

- `<vehicle>`
    - `name`: vehicle name, visible in the UI.
    - `speed`: speed adjustment (1 for default).
    - `width`, `height`: vehicle size in pixels.
    - `<main>`
        - `image`: image name, without extension.
    - `<shapes>`: the vehicle shapes. Can contain multiple children.
        - `<octogon>`
            - `width`, `height`: the octagon dimensions.
            - `x`, `y`: the shape bottom-left corner. Defaults to centered if not set.
            - `corner`: size of the octagon corners. Defaults to 0 (making the shape a rectangle).
        - `<trapezoid>`: an horizontal trapezoid.
            - `bottomWidth`, `topWidth`, `height`: the trapezoid dimensions.
            - `x`, `y`: the shape bottom-left corner. Defaults to centered if not set.
    - `<axle>`: An axle linking two wheels. Can (should!) appear multiple times.
        - `y`: y position.
        - `width`: width.
        - `steer`: a float indicating if the wheels can be used to steer. 0 means no steering. 1 means steering in the selected direction, -1 means steering in the opposite direction (useful for steering rear wheels). Defaults to 0.
