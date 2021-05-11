## Actions

    moveTo x y [duration [interpolation]]
    moveToAligned x y alignment [duration [interpolation]]
    moveBy dx dy [duration [interpolation]]
    rotateTo angle [duration [interpolation]]
    rotateBy angle [duration [interpolation]]
    scaleTo zoomX zoomY [duration [interpolation]]
    sizeTo width height [duration [interpolation]]
    alpha alpha [duration [interpolation]]
    delay duration

## interpolation argument

The `interpolation` argument defaults to `linear`, but can be one of:

- `bounce`
- `bounceIn`
- `bounceOut`
- `circle`
- `circleIn`
- `circleOut`
- `elastic`
- `elasticIn`
- `elasticOut`
- `exp10`
- `exp10In`
- `exp10Out`
- `exp5`
- `exp5In`
- `exp5Out`
- `fade`
- `linear`
- `pow2`
- `pow2In`
- `pow2Out`
- `pow3`
- `pow3In`
- `pow3Out`
- `pow4`
- `pow4In`
- `pow4Out`
- `pow5`
- `pow5In`
- `pow5Out`
- `sine`
- `sineIn`
- `sineOut`
- `swing`
- `swingIn`
- `swingOut`

## Flow control

    parallel
        action1
        action2
        ...
    end

    repeat [count]
        action1
        action2
        ...
    end

## Misc

Comments are `//`.
