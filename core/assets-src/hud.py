#!/usr/bin/env python3
import os
import sys

from os.path import join

from PIL import Image

import pafx


DST_DIR = '../assets/sprites'
DEPTH_COLOR = '#595652'
OUTLINE_COLOR = 'black'

NORMAL_DEPTH = 2
DOWN_DEPTH = 1
SIZE = (48, 48)


os.chdir(os.path.dirname(sys.argv[0]))


def create_button(src, down=False):
    img = pafx.clone_format(src, SIZE)
    depth = DOWN_DEPTH if down else NORMAL_DEPTH
    pafx.paste(img, src, top_offset=-depth)

    for x in range(depth):
        img = pafx.add_depth(img, color=DEPTH_COLOR)
    return pafx.add_outline(img, color=OUTLINE_COLOR)


def create_buttons(src, name):
    create_button(src).save(join(DST_DIR, name + '.png'))
    create_button(src, down=True).save(join(DST_DIR, name + '-down.png'))


triangles = {}
triangles['left'] = Image.open('hud-left.png')
triangles['right'] = triangles['left'].transpose(Image.FLIP_LEFT_RIGHT)
triangles['back'] = triangles['left'].transpose(Image.ROTATE_90)

for name, triangle in triangles.items():
    create_buttons(triangle, 'hud-' + name)

action = Image.open('hud-action.png')
create_buttons(action, 'hud-action')
