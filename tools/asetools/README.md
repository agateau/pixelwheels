# asetools

This directory contains tools to work with ase images, created using [Aseprite][]. Unfortunately, Aseprite license is not OSI compliant, even if the source code is available, making it complicated to rely on the tool being available everywhere it's needed. For example on CI servers.

To solve this, Pixel Wheels build system uses the `asesplit` tool from this directory to turn ase images into png suitable for usage in the game.

Another tool: `aseinfo` gives you information about the content of an ase file.

## Warning

You are welcome to use these tools in your project, but note that support for ase files is not complete: it only support the subset used for Pixel Wheels assets.

[Aseprite]: https://aseprite.org
