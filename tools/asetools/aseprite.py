#!/usr/bin/env python3
"""
Load an Aseprite file
"""
import zlib

from io import BytesIO
from struct import unpack
from typing import List

# Aseprite file format doc:
# https://github.com/aseprite/aseprite/blob/master/docs/ase-file-specs.md


MAGIC = 0xA5E0
FRAME_MAGIC = 0xF1FA

LAYER_CHUNK = 0x2004
CEL_CHUNK = 0x2005
PALETTE_CHUNK = 0x2019
SLICE_CHUNK = 0x2022

LINKED_CEL_TYPE = 1
COMPRESSED_IMAGE_CEL_TYPE = 2


class NotAsepriteFile(Exception):
    pass


class NotSupported(Exception):
    pass


class Layer:
    def __init__(self, image: "AsepriteImage", name: str):
        self.image = image
        self.visible = True
        self.is_group = False
        self.name = name


class Cel:
    def __init__(self, layer: Layer):
        self.layer = layer
        self.position = [0, 0]
        self.size = [0, 0]
        self.pixels = []


class Slice:
    def __init__(self, name: str, pos, size):
        self.name = name
        self.position = pos
        self.size = size


class Frame:
    def __init__(self, image: "AsepriteImage"):
        self.image = image
        self.cels = []

    def append_cel(self, layer: Layer):
        self.cels.append(Cel(layer))


class AsepriteImage:
    def __init__(self, filename: str):
        self.palette = []
        self.size = [0, 0]
        self.frame_count = 0
        self.transparent_color = 0
        self.depth = 0
        self.color_count = 0
        self.layers = []
        self.frames = []
        self.slices = []

        with open(str(filename), "rb") as fp:
            self.read_header(fp)
            for _ in range(self.frame_count):
                self.read_frame(fp)

    def read_header(self, fp):
        data = fp.read(44)
        file_size, magic, self.frame_count, self.size[0], self.size[1], \
            self.depth, flags, speed, zero1, zero2, \
            self.transparent_color, self.color_count, \
            px_width, px_height, grid_x, grid_y, grid_width, grid_height \
            = unpack("<LHHHHHLHLLBxxxHBBhhHH", data)
        if magic != MAGIC:
            raise NotAsepriteFile()
        if zero1 != 0 or zero2 != 0:
            raise NotAsepriteFile()
        # Skip padding
        fp.seek(128)

    def read_frame(self, fp):
        data = fp.read(16)
        frame_size, magic, old_chunks, duration, new_chunks \
            = unpack("<LHHHxxL", data)
        if magic != FRAME_MAGIC:
            raise NotAsepriteFile("Invalid frame magic ({})".format(magic))
        chunk_count = old_chunks if old_chunks < 0xffff else new_chunks

        frame = Frame(self)
        self.frames.append(frame)
        if len(self.frames) > 1:
            for layer in self.layers:
                frame.append_cel(layer)
        for _ in range(chunk_count):
            self.read_chunk(fp)

    def read_chunk(self, fp):
        chunk_size, chunk_type = unpack("<LH", fp.read(6))
        data = fp.read(chunk_size - 6)
        chunk_fp = BytesIO(data)
        if chunk_type == LAYER_CHUNK:
            self.read_layer_chunk(chunk_fp)
        elif chunk_type == CEL_CHUNK:
            self.read_cel_chunk(chunk_fp)
        elif chunk_type == PALETTE_CHUNK:
            self.read_palette_chunk(chunk_fp)
        elif chunk_type == SLICE_CHUNK:
            self.read_slice_chunk(chunk_fp)

    def read_layer_chunk(self, fp):
        flags, layer_type, child_level, blend_mode, opacity, layer_name_length \
            = unpack("<HHHxxxxHbxxxH", fp.read(18))
        name = str(fp.read(), "utf-8")
        layer = Layer(self, name)
        layer.visible = bool(flags & 1)
        layer.is_group = layer_type == 1
        self.layers.append(layer)
        # Create a matching cel in the first frame, so that read_cel_chunk has
        # a place to write
        self.frames[0].append_cel(layer)

    def read_cel_chunk(self, fp):
        index, pos_x, pos_y, opacity, cel_type = unpack("<HhhBH", fp.read(9))
        fp.read(7)
        if cel_type == LINKED_CEL_TYPE:
            linked_frame_index = unpack("<H", fp.read(2))[0]
            linked_cel = self.frames[linked_frame_index].cels[index]
            self.frames[-1].cels[index] = linked_cel
        elif cel_type == COMPRESSED_IMAGE_CEL_TYPE:
            cel = self.frames[-1].cels[index]
            cel.position = [pos_x, pos_y]
            cel.size = unpack("<HH", fp.read(4))
            cel.pixels = zlib.decompress(fp.read())
        else:
            raise NotSupported("Unsupported cel_type {}".format(cel_type))

    def read_palette_chunk(self, fp):
        self.palette = [(0, 0, 0, 0)] * self.color_count
        size, first, last = unpack("<LLL", fp.read(12))
        fp.read(8)
        for idx in range(first, last + 1):
            flags, red, green, blue, alpha = unpack("<HBBBB", fp.read(6))
            if flags != 0:
                raise NotSupported("Named colors in palette")
            self.palette[idx] = (red, green, blue, alpha)
        self.palette[self.transparent_color] = (0, 0, 0, 0)

    def read_slice_chunk(self, fp):
        count, flags, name_length = unpack("<LLxxxxH", fp.read(14))
        if count > 1:
            raise NotSupported("Multi-key slices")
        if flags != 0:
            raise NotSupported("Slice flags {}".format(flags))
        name = str(fp.read(name_length), "utf-8")

        frame_number, x, y, width, height = unpack("<LllLL", fp.read(20))
        self.slices.append(Slice(name, (x, y), (width, height)))
# vi: ts=4 sw=4 et
