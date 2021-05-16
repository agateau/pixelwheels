# asetools

[Aseprite[[] is a wonderful pixelart tool. Unfortunately its license is not OSI compliant even if the source code is available, making it complicated to rely on the tool being available everywhere it's needed. This is a problem for CI servers or open-source application stores like F-Droid.

This directory contains open-source command-line tools to work with Aseprite images.

## Tools

### asesplit

The `asesplit` tool turn ase images into pngs. It can extract individual layers and/or slices, trim and rotate them. Check `aseplit --help` for more details.

### aseinfo

The `aseinfo` gives you information about the content of an ase file.

## Tests

You can run tests using `pytest`. Just run `pytest` in this directory.

## Warning

You are welcome to use these tools in your project, but note that support for ase files is not complete: it only support the subset used for Pixel Wheels assets.

[Aseprite]: https://aseprite.org
