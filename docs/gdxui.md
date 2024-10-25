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
- x (dimension, optional)
- y (dimension, optional)
- width (dimension, optional)
- height (dimension, optional)
- originX (dimension, optional)
- originY (dimension, optional)
- visible (boolean, optional)
- color (string, optional): tint the actor.
- debug (enum("false", "true", "all"), optional): if set to "true", calls `actor.setDebug(true)`. If set to "all" on a group, calls `Group.debugAll()`.

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

## Using a gdxui file from the code

To load a gdxui file, one uses the `UiBUilder` class. Typical usage looks like this:

``` java
UiBuilder builder = new UiBuilder(atlas, skin);

// Build the scene, returns the root actor
Actor content = builder.build(fileHandleToAGdxuiFile);

// Retrieve an Actor using its id
TextButton closeButton = builder.getActor("closeButton");
```

Extra `TextureAtlas` instances can be registered using `addAtlas(atlasName, atlas)`.

## Available actors

### Image

- name (string, optional): Name of the image in the atlas.
- tiled (boolean, optional): Set to `true` to make the image repeat itself. Defaults to `false`.
- atlas (string, optional): In which atlas to look for the image. Must have been registered with `UiBuilder.addAtlas`.

### AnimatedImage

- name (string, required): Name of the animation in the atlas.
- frameDuration (float, optional): Duration of the frame, in seconds. Defaults to 0.1.
- startTime (float, optional): Start time in the animation, useful to offset different instances. Defaults to 0.
- atlas (string, optional): In which atlas to look for the image. Must have been registered with `UiBuilder.addAtlas`.

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
- wrap (boolean, optional): If set to true and width is defined, the Label height is adjusted to fit the Label text in the defined width

Text is defined by the element text. You can insert multiple lines using `\n`.

### ScrollPane

- style (string, optional)
Must contains one child actor.

### VerticalGroup

- spacing (float, optional)

### HorizontalGroup

- spacing (float, optional)

### CheckBox

- style (string, optional)
Text is defined by the element text.

### Menu

- style (string, optional)
- labelColumnWidth (float, optional)

Must contain a child element called Items. Items must contain one of:

- ButtonMenuItem
    - label (string, optional): Label to show to the left of the button. No label if not set.
    - text (string, optional): Text of the button.
    - parentWidthRatio (float, optional): Ratio of the button vs the menu. Default to 1.0.
- LabelMenuItem
    - text (string, required): Text of the button.
- ImageMenuItem
    - name (string, required): Name of the image in the atlas.
- SpacerMenuItem
    - height (dimension, optional)

Example:

```
<Menu>
    <Items>
        <ButtonMenuItem id="helloButton" text="HELLO!"/>
        <ButtonMenuItem id="goButton" text="GO"/>
    </Items>
</Menu>
```

### MenuScrollPane

Must contains a Menu element.

### Table

## Attribute types

### align

One of:

- center
- centerLeft
- centerRight
- topLeft
- topCenter
- topRight
- bottomLeft
- bottomCenter
- bottomRight

## Configuration values

It's sometimes useful to expose configuration values through a gdxui file, to fine-tune other parameters without restarting the game. This can be done with `ConfigItem` elements.

The syntax looks like this:

```xml
<gdxui>
    <Config>
        <ConfigItem id="aKey">theValue</ConfigItem>
        <ConfigItem id="fooSpeed">0.2</ConfigItem>
        ...
    </Config>
```

The configuration values can be accessed from the code using one of these methods:

- `UiBuilder.getInttConfigValue()`
- `UiBuilder.getFloatConfigValue()`
- `UiBuilder.getStringConfigValue()`
- `UiBuilder.getAnimScriptConfigValue()`

## Actor actions

An actor can contain one or more `Action` element. The text of this element defines the actions to create for this actor, using [animscript](animscript.md).

Alternatively you can define the `Action` element can define the actor it applies to by setting the actor ID in the optional `actor` attribute.

This is useful when the actor element contains text, for example for Labels, because if an Action is defined inside the Label, then gettext won't pick up the text for translations.

## Ifdef / Else

You can create conditional blocks with `Ifdef` and `Else` blocks, like this:

```xml
<Ifdef var="foo">
    <Label>foo is defined</Label>
</Ifdef>
<Else>
    <Label>foo is not defined</Label>
</Else>
```

The variable can be defined using `UiBuilder.defineVariable("foo")`.
