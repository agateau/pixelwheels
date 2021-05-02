# gdxui

gdxui files are XML files to define a libgdx scene2d scene.

Actors are defined as XML elements.

The root element is a `gdxui` element. It usually contains one single group actor. Often an `AnchorGroup`.

Here is an example:

```xml
<?xml version="1.0"?>
<gdxui>
    <AnchorGroup id="root" gridSize="20">
        <Label id="helloLabel" topCenter="root.topCenter 0g -2g">Hello</Label>
        <Label topCenter="helloLabel.bottomCenter 0g -1g" style="big">World!</Label>

        <TextButton id="closeButton" bottomRight="root.bottomRight -1g -1g" width="6g">Close</TextButton>
    </AnchorGroup>
</gdxui>
```

This defines two labels on top of each other at the top center of the screen and a "Close" button in the bottom right corner.

## Grid and pixels

Attributes referencing sizes and positions can use pixels or grid cells: in this example the `root` AnchorGroup defines a grid size of 20 pixels. The "Close" button width is "6g", so with a grid size of 20 pixels it will end up being 6 * 20 = 120 pixels wide.

To define a size in pixels, suffix it with `px`.

To define a size in grid cells, suffix it with `g` (using no suffix is also accepted, for compatibility reasons).

## Common actor attributes

- id (string, optional): a name for the actor. Used to position other actors relative to it and to get the Actor instance from the code.
- width (dimension, optional)
- height (dimension, optional)

## AnchorGroup related attributes

When a actor is a child of an AnchorGroup it can uses the following attributes to position itself.

- topLeft
- topCenter
- topRight
- centerLeft
- center
- centerRight
- bottomLeft
- bottomCenter
- bottomRight

These attributes are strings following this format: `<actor>.<anchor> [leftMargin] [topMargin]`

## Using a gdxui from the code

To load a gdxui file, one uses the `UiBUilder` class. Typical usage looks like this:

``` java
UiBuilder builder = new UiBuilder(atlas, skin);

// Build the scene, returns the root actor
Actor content = builder.build(fileHandleToAGdxuiFile);

// Retrieve an Actor using its id
TextButton closeButton = builder.getActor("closeButton");
```

## Available actors

### Image
- name (string, optional):
- tiled (boolean, optional):

### AnimatedImage
- name (string, required): Name of the animation in the ui atlas.
- frameDuration (float, optional): Duration of the frame, in seconds. Defaults to 0.1.
- startTime (float, optional): Start time in the animation, useful to offset different instances. Defaults to 0.

### ImageButton
- style (string, optional)
- imageName (string, optional)
- imageColor (string, optional)

### TextButton
- style (string, optional)
Text is defined by the element text.

### Group

### AnchorGroup
- gridSize (dimension, optional)

### Label
- style (string, optional)
- align (alignment, optional)
Text is defined by the element text.

### ScrollPane
- style (string, optional)
Must contains one child actor.

### VerticalGroup
- space (float, optional)

### HorizontalGroup
- space (float, optional)

### CheckBox
- style (string, optional)
Text is defined by the element text.

### Menu
- style (string, optional)
- labelColumnWidth (float, optional)

Must contains a child element called Items. Items must contain one of:
- ButtonMenuItem
    - label (string, optional)
    - text (string, optional)
- LabelMenuItem
    - text (string, optional)

### MenuScrollPane
Must contains a Menu element.

### Table

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
