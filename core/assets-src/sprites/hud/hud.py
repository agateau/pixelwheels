#!/usr/bin/env python3
import os
import sys
from collections import namedtuple
from os.path import join

import pafx
from PIL import Image

Button = namedtuple("Button", ("name", "anchor", "size"))

DST_DIR = os.path.abspath(sys.argv[1])
DEPTH_COLOR = "#595652"
OUTLINE_COLOR = "black"

NORMAL_DEPTH = 2
DOWN_DEPTH = 1

OUTLINE_SIZE = 1

PIE_FINAL_SIZE = (132 + 2 * OUTLINE_SIZE, 132 + NORMAL_DEPTH)

SIDES_FINAL_SIZE = (160 + 2 * OUTLINE_SIZE, 132 + NORMAL_DEPTH)

PAUSE_SIZE = (80, 80 + NORMAL_DEPTH)

BUTTONS = [
    Button("pie-action", pafx.TOP_RIGHT, PIE_FINAL_SIZE),
    Button("pie-brake", pafx.BOTTOM_LEFT, PIE_FINAL_SIZE),
    Button("pie-left", pafx.TOP_LEFT, PIE_FINAL_SIZE),
    Button("pie-right", pafx.BOTTOM_RIGHT, PIE_FINAL_SIZE),
    Button("sides-left", pafx.BOTTOM_LEFT, SIDES_FINAL_SIZE),
    Button("sides-right", pafx.BOTTOM_RIGHT, SIDES_FINAL_SIZE),
    Button("sides-action", pafx.CENTER_RIGHT, SIDES_FINAL_SIZE),
    Button("pause", pafx.CENTER, PAUSE_SIZE),
]


def create_button(src, down=False):
    size = (
        src.size[0] + OUTLINE_SIZE * 2,
        src.size[1] + OUTLINE_SIZE * 2 + NORMAL_DEPTH,
    )
    img = pafx.clone_format(src, size)

    depth = DOWN_DEPTH if down else NORMAL_DEPTH

    pafx.paste(
        img,
        src,
        src_anchor=pafx.BOTTOM_CENTER,
        dst_anchor=pafx.BOTTOM_CENTER,
        top_offset=-(depth + OUTLINE_SIZE),
    )

    for x in range(depth):
        img = pafx.add_depth(img, color=DEPTH_COLOR)
    return pafx.add_outline(img, color=OUTLINE_COLOR)


def create_buttons(src, name, anchor=pafx.CENTER, final_size=None):
    imgs = ((create_button(src), ""), (create_button(src, down=True), "-down"))
    for img, suffix in imgs:
        if final_size:
            img2 = pafx.clone_format(img, final_size)
            pafx.paste(img2, img, dst_anchor=anchor, src_anchor=anchor)
            img = img2
        img.save(join(DST_DIR, name + suffix + ".png"))


def main():
    os.chdir(os.path.dirname(sys.argv[0]))

    Image.open("hud-sides-left.png").transpose(Image.FLIP_LEFT_RIGHT).save(
        "hud-sides-right.png"
    )

    for button in BUTTONS:
        print('Processing "{}"'.format(button.name))
        image = Image.open("hud-{}.png".format(button.name))

        create_buttons(
            image,
            "hud-{}".format(button.name),
            anchor=button.anchor,
            final_size=button.size,
        )


if __name__ == "__main__":
    main()
