#!/usr/bin/env python3
import os
import sys

from os.path import join

from PIL import Image

import pafx


DST_DIR = os.path.abspath(sys.argv[1])
DEPTH_COLOR = '#595652'
OUTLINE_COLOR = 'black'

NORMAL_DEPTH = 2
DOWN_DEPTH = 1

BUTTONS = ['action', 'brake', 'left', 'right']

LEFT_BUTTONS = ['left', 'right']

PADDING = 8

FINAL_SIZE = (132, 132)


def create_button(src, down=False):
    size = [x + PADDING for x in src.size]
    img = pafx.clone_format(src, size)
    depth = DOWN_DEPTH if down else NORMAL_DEPTH
    pafx.paste(img, src, top_offset=-depth)

    for x in range(depth):
        img = pafx.add_depth(img, color=DEPTH_COLOR)
    return pafx.add_outline(img, color=OUTLINE_COLOR)


def create_buttons(src, name, anchor=pafx.CENTER, final_size=None):
    imgs = (
        (create_button(src), ''),
        (create_button(src, down=True), '-down')
    )
    for img, suffix in imgs:
        if final_size:
            img2 = pafx.clone_format(img, final_size)
            pafx.paste(img2, img, dst_anchor=anchor, src_anchor=anchor)
            img = img2
        img.save(join(DST_DIR, name + suffix + '.png'))


def main():
    os.chdir(os.path.dirname(sys.argv[0]))

    for button in BUTTONS:
        print('Processing "{}"'.format(button))
        image = Image.open('hud-{}.png'.format(button))

        anchor = pafx.BOTTOM_LEFT if button in LEFT_BUTTONS else pafx.BOTTOM_RIGHT
        create_buttons(image, 'hud-{}'.format(button), anchor=anchor, final_size=FINAL_SIZE)

    print('Processing "round-button"')
    image = Image.open('hud-round-button.png')
    create_buttons(image, 'hud-round-button')


if __name__ == '__main__':
    main()
