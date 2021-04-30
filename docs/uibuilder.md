## Available actors

AnimatedImage
- name (string, required): Name of the animation in the ui atlas.
- frameDuration (float, optional): Duration of the frame, in seconds. Defaults to 0.1.
- startTime (float, optional): Start time in the animation, useful to offset different instances. Defaults to 0.

TODO

## Config items

It's sometimes useful to expose configuration values through a gdxui file, to fine tune other parameters without restarting the game. This can be done with `ConfigItem` elements.

The syntax looks like this:

```xml
<gdxui>
    <Config>
        <ConfigItem id="aKey">theValue</ConfigItem>
        <ConfigItem id="fooSpeed">0.2</ConfigItem>
        ...
    <Config>
```

The config values can be accessed from the code using one of these methods:

- `UiBuilder.getFloatConfigValue()`
- `UiBuilder.getStringConfigValue()`
- `UiBuilder.getAnimScriptConfigValue()`
