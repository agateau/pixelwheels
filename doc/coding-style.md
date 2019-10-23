# Pixel Wheels coding style

## Base

Pixel Wheels is based on the AOSP variant (4 space indents) of [Google Java
coding style][coding-style].

[coding-style]: https://google.github.io/styleguide/javaguide.html

## Extra rules

### `if` always uses curly braces

Good:

```
if (something) {
    return;
}
```

Bad:

```
if (something) return;

if (something)
    return;
```

### No boolean parameters

Boolean parameters makes callers hard to read. Instead of:

```
void drawRect(int x, bool y, int width, int height, boolean fill);

// Calling code
drawRect(12, 34, 100, 200, true);
```

Either use a boolean:

```
enum FillMode {
    FILLED,
    EMPTY
}

void drawRect(int x, bool y, int width, int height, FillMode fillMode);

drawRect(12, 34, 100, 200, FillMode.FILLED);
```

Or split the function into two:

```
void drawFilledRect(int x, bool y, int width, int height);
void drawEmptyRect(int x, bool y, int width, int height);

drawFilledRect(12, 34, 100, 200);
```

## Applying the coding style

You can run `tools/apply-codingstyle` to apply most of the rules to the code in
your checkout. This tool downloads [google-java-format][gjf] in `$HOME/.cache`
and runs it on all the Java files of the project.

Note that the CI builder checks for coding style conformance.

[gjf]: https://github.com/google/google-java-format
