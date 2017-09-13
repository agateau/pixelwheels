export PATH := $(PWD)/../../bin:$(PATH)

OUT_DIR ?= ../assets

VEHICLES_SUBDIR := sprites/vehicles

all: hud anims stills vehicles tiles

clean: clean-anims clean-stills clean-vehicles clean-tiles

#- Hud ------------------------------------------------------------------------
# Just enough files to trigger the target
HUD_FILES := $(OUT_DIR)/sprites/hud-action.png $(OUT_DIR)/sprites/hud-left.png

hud: $(HUD_FILES)

$(HUD_FILES): sprites/hud-action.png sprites/hud-left.png sprites/hud.py
	mkdir -p $(OUT_DIR)/sprites
	sprites/hud.py $(OUT_DIR)/sprites

#- Still images ---------------------------------------------------------------
STILL_IMAGES := $(wildcard sprites/*.still.ase)
OUT_STILL_IMAGES := $(STILL_IMAGES:%.still.ase=$(OUT_DIR)/%.png)
stills: $(OUT_STILL_IMAGES)

$(OUT_DIR)/sprites/%.png: sprites/%.still.ase
	mkdir -p $(OUT_DIR)/sprites
	aseprite --batch $< --save-as $@

clean-stills:
	rm -f $(OUT_STILL_IMAGES)

#- Anims ----------------------------------------------------------------------
ANIM_IMAGES := $(wildcard sprites/*.anim.ase)
OUT_ANIM_FIRST_FRAMES := $(ANIM_IMAGES:%.anim.ase=$(OUT_DIR)/%_0.png)

anims: $(OUT_ANIM_FIRST_FRAMES)

clean-anims:
	rm -f $(OUT_ANIM_FIRST_FRAMES)

$(OUT_DIR)/sprites/%_0.png: sprites/%.anim.ase
	mkdir -p $(OUT_DIR)/sprites
	aseprite --batch $< --save-as $(patsubst %_0.png,%.png,$@)
	rename-aseprite-frames $(patsubst %_0.png,%,$@)

#- Vehicles -------------------------------------------------------------------
VEHICLE_IMAGES := $(wildcard sprites/vehicles/*.ase)
OUT_VEHICLE_IMAGES := $(VEHICLE_IMAGES:%.ase=$(OUT_DIR)/%.png)

vehicles: $(OUT_VEHICLE_IMAGES)

$(OUT_DIR)/sprites/vehicles/%.png: TMP_PNG = $(@:%.png=%-tmp.png)
$(OUT_DIR)/sprites/vehicles/%.png: sprites/vehicles/%.ase
	mkdir -p $(OUT_DIR)/sprites/vehicles
	aseprite --batch $< --save-as $(TMP_PNG)
	convert -rotate 90 $(TMP_PNG) $@
	rm $(TMP_PNG)

clean-vehicles:
	rm -f $(OUT_VEHICLE_IMAGES)

#- Tiles ----------------------------------------------------------------------
TILE_IMAGES := $(wildcard maps/*.ase)
OUT_TILE_IMAGES := $(TILE_IMAGES:%.ase=$(OUT_DIR)/%.png)

tiles: $(OUT_TILE_IMAGES)

$(OUT_DIR)/maps/%.png: TMP_PNG = $(@:%.png=%-tmp.png)
$(OUT_DIR)/maps/%.png: maps/%.ase
	mkdir -p $(OUT_DIR)/maps
	aseprite --batch $< --save-as $(TMP_PNG)
	convert -alpha set -channel RGBA \
		-fill '#22203460' -opaque '#ff00ff' \
		-fill '#ffffff20' -opaque '#00ffff' \
		$(TMP_PNG) $@
	rm $(TMP_PNG)

clean-tiles:
	rm -f $(OUT_TILE_IMAGES)
