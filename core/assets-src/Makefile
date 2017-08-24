export PATH := $(PWD)/../../bin:$(PATH)

OUT_DIR ?= ../assets

VEHICLES_SUBDIR := sprites/vehicles

all: $(OUT_DIR) hud anims

$(OUT_DIR):
	mkdir -p $(OUT_DIR)/$(VEHICLES_SUBDIR)

clean: clean-anims

#- Hud ------------------------------------------------------------------------
# Just enough files to trigger the target
HUD_FILES := $(OUT_DIR)/sprites/hud-action.png $(OUT_DIR)/sprites/hud-left.png

hud: $(HUD_FILES)

$(HUD_FILES): sprites/hud-action.png sprites/hud-left.png sprites/hud.py
	sprites/hud.py $(OUT_DIR)/sprites

#- Anims ----------------------------------------------------------------------
ANIM_IMAGES := $(wildcard sprites/*.anim.ase)
OUT_ANIM_FIRST_FRAMES := $(ANIM_IMAGES:%.anim.ase=$(OUT_DIR)/%_0.png)

anims: $(OUT_ANIM_FIRST_FRAMES)

clean-anims:
	rm $(OUT_ANIM_FIRST_FRAMES)

$(OUT_DIR)/sprites/%_0.png: sprites/%.anim.ase
	aseprite --batch $< --save-as $(patsubst %_0.png,%.png,$@)
	rename-aseprite-frames $(patsubst %_0.png,%,$@)
