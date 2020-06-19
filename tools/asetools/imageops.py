from io import BytesIO
from typing import BinaryIO

from aseprite import Cel, Frame
from PIL import Image

# PIL does not properly handle palette images with alpha, so we use pypng to
# convert the image to RGBA
import png


def save_cel_as_png(cel: Cel, fp: BinaryIO):
    width, height = cel.size
    lines = [cel.pixels[x:x + width] for x in range(0, len(cel.pixels), width)]
    writer = png.Writer(width, height, palette=cel.layer.image.palette, bitdepth=8)
    writer.write(fp, lines)


def pil_image_for_cel(cel: Cel) -> Image:
    fp = BytesIO()
    save_cel_as_png(cel, fp)
    return Image.open(fp).convert("RGBA")


def render_frame(frame: Frame) -> Image:
    cels = [x for x in frame.cels if x.layer.visible and not x.layer.is_group]
    assert cels, "No visible layers!"
    dest_image = Image.new("RGBA", frame.image.size)
    for cel in cels:
        cel_image = pil_image_for_cel(cel)
        dest_image.paste(cel_image, box=cel.position, mask=cel_image)
    return dest_image
