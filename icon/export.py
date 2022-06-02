#!/usr/bin/env python3
"""
Generate all required icon variants from icon.ase
"""
import argparse
import shlex
import shutil
import subprocess
import sys

from pathlib import Path

ICON_DIR = Path(__file__).resolve().parent
REPO_DIR = ICON_DIR.parent
WORK_DIR = ICON_DIR / "work"

ANDROID_DIR = REPO_DIR / "android"
ASSETS_DIR = ANDROID_DIR / "assets"
RES_DIR = ANDROID_DIR / "res"

GPLAY_ICON = REPO_DIR / "fastlane/metadata/android/en-US/images/icon.png"

MACOS_ICON = REPO_DIR / "tools/packaging/macos/pixelwheels.icns"
LINUX_ICON_DIR = REPO_DIR / "tools/packaging/linux/share/icons/hicolor"

APPWINDOW_ICON = ASSETS_DIR / "desktop-icon/desktop-icon.png"

BGCOLOR = "#5a6988"

SLICE_ICON_SIZES = [16, 32, 48, 64]

SCALED_REFERENCE_SIZE = 64
SCALED_ICON_SIZES = [128, 256, 512, 1024]

# Note: no 64x64 in the macOS icon
MACOS_ICON_SIZES = [16, 32, 48, 128, 256, 512, 1024]

LINUX_ICON_SIZES = SLICE_ICON_SIZES + SCALED_ICON_SIZES


def run(*args):
    cmd = [str(x) for x in args]
    cmd_str = " \\\n  ".join(shlex.quote(x) for x in cmd)
    res = subprocess.run(cmd)
    if res.returncode != 0:
        print(f"The following command failed with exit code {res.returncode}:")
        print(cmd_str)
        sys.exit(1)


def aseprite(*args):
    cmd = ["aseprite", "--batch", ICON_DIR / "icon.ase"] + list(args)
    run(*cmd)


def work_icon(size):
    return WORK_DIR / f"icon-{size}.png"


def _generate_work_icons():
    print("Generating work icons")
    for size in SLICE_ICON_SIZES:
        aseprite("--slice", f"icon-{size}",
                 "--save-as", work_icon(size))

    for size in SCALED_ICON_SIZES:
        scale = int(size / SCALED_REFERENCE_SIZE)
        aseprite("--slice", f"icon-{SCALED_REFERENCE_SIZE}",
                 "--scale", scale,
                 "--save-as", work_icon(size))


def generate_macos_icons():
    pngs = [work_icon(x) for x in MACOS_ICON_SIZES]
    run("png2icns", MACOS_ICON, *pngs)


def generate_android_icons():
    """TODO: port a fixed version once the adaptive issue is sorted out.

    See https://github.com/agateau/pixelwheels/issues/198

    Original code:

    # mdpi & xhdpi
    aseprite --batch icon.ase --slice icon-48 \
        --save-as $RES_DIR/drawable-mdpi/ic_launcher.png \
        --scale 2 --save-as $RES_DIR/drawable-xhdpi/ic_launcher.png

    # hdpi & xxhdpi
    aseprite --batch icon.ase --slice icon-72 \
        --save-as $RES_DIR/drawable-hdpi/ic_launcher.png \
        --scale 2 --save-as $RES_DIR/drawable-xxhdpi/ic_launcher.png
    """
    pass


def generate_android_tv_banner():
    aseprite("--slice", "tv-banner",
             "--scale", 2,
             "--save-as", RES_DIR / "drawable-xhdpi/tv_banner.png")


def generate_linux_icons():
    for size in LINUX_ICON_SIZES:
        dst = LINUX_ICON_DIR / f"{size}x{size}/apps/com.agateau.PixelWheels.png"
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy(work_icon(size), dst)


def generate_appwindow_icon():
    shutil.copy(work_icon(128), APPWINDOW_ICON)


def generate_gplay_icon():
    # Google Play: 512 x 512 RVB, flat
    run("convert", work_icon(512),
        "-background", BGCOLOR, "-flatten", GPLAY_ICON)


def main():
    parser = argparse.ArgumentParser(
        formatter_class=argparse.RawDescriptionHelpFormatter,
        description=__doc__)

    parser.add_argument("-k", "--keep", action="store_true",
                        help="Keep work dir")

    parser.add_argument("targets", nargs="*")

    args = parser.parse_args()

    if WORK_DIR.exists():
        shutil.rmtree(WORK_DIR)
    WORK_DIR.mkdir()

    try:
        if args.targets:
            targets = args.targets
        else:
            prefix = "generate_"
            targets = [k[len(prefix):] for k in globals() if k.startswith(prefix)]

        _generate_work_icons()

        for target in targets:
            print(f"== {target} ==")
            target_fn = eval(f"generate_{target}")
            target_fn()
    finally:
        if not args.keep:
            shutil.rmtree(WORK_DIR)

    return 0


if __name__ == "__main__":
    sys.exit(main())
# vi: ts=4 sw=4 et
