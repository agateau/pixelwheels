export PATH := $(PWD)/../../bin:$(PATH)

OUT_DIR ?= ../assets

TOOLS_DIR ?= ../../tools

FINAL_OUT_DIR ?= ../../android/assets

all: hud anims stills vehicles tiles helicopter ui map-icons championship-icons input-icons

clean:
	rm -rf $(OUT_DIR)

#- sprites/ -------------------------------------------------------------------
# Still images
STILL_IMAGES := $(wildcard sprites/*.still.ase sprites/tires/*.still.ase)
OUT_STILL_IMAGES := $(STILL_IMAGES:%.still.ase=$(OUT_DIR)/%.png)
stills: $(OUT_STILL_IMAGES)

$(OUT_DIR)/sprites/%.png: sprites/%.still.ase
	mkdir -p $(OUT_DIR)/sprites
	mkdir -p $(OUT_DIR)/sprites/tires
	asesplit $< $@

# Anims
ANIM_IMAGES := $(wildcard sprites/*.anim.ase sprites/tires/*.anim.ase)
OUT_ANIM_FIRST_FRAMES := $(ANIM_IMAGES:%.anim.ase=$(OUT_DIR)/%_0.png)

anims: $(OUT_ANIM_FIRST_FRAMES)

$(OUT_DIR)/sprites/%_0.png: sprites/%.anim.ase
	mkdir -p $(OUT_DIR)/sprites
	mkdir -p $(OUT_DIR)/sprites/tires
	# $(@D) is the directory part of the target
	# $(*F) is the % part of the target
	asesplit $< "$(@D)/$(*F)_{frame}.png"

# Helicopter
helicopter: $(OUT_DIR)/sprites/helicopter-body.png

$(OUT_DIR)/sprites/helicopter-body.png: sprites/helicopter.ase
	mkdir -p $(OUT_DIR)/sprites
	asesplit --split-layers --trim $< "$(OUT_DIR)/sprites/helicopter-{layer}.png"

# hud-*
# Just enough to trigger the target
hud: $(OUT_DIR)/sprites/hud-pause.png $(OUT_DIR)/sprites/lap-icon.png

$(OUT_DIR)/sprites/hud-pause.png: sprites/hud/hud-pie-buttons.ase sprites/hud/hud-sides-buttons.ase sprites/hud/hud.py
	mkdir -p $(OUT_DIR)/sprites

	asesplit --split-layers --trim sprites/hud/hud-pie-buttons.ase \
		"sprites/hud/hud-{layer}.png"

	asesplit --split-slices sprites/hud/hud-sides-buttons.ase \
		"sprites/hud/{slice}.png"

	sprites/hud/hud.py $(OUT_DIR)/sprites

$(OUT_DIR)/sprites/lap-icon.png: sprites/hud/lap-icon.ase
	asesplit $< $@

#- sprites/vehicles/ ----------------------------------------------------------
VEHICLE_IMAGES := $(wildcard sprites/vehicles/*.ase)
OUT_VEHICLE_IMAGES := $(VEHICLE_IMAGES:%.ase=$(OUT_DIR)/%_0.png)

vehicles: $(OUT_VEHICLE_IMAGES)

$(OUT_DIR)/sprites/vehicles/%_0.png: sprites/vehicles/%.ase
	mkdir -p $(OUT_DIR)/sprites/vehicles
	asesplit --rotate -90 $< "$(OUT_DIR)/sprites/vehicles/$(*F)_{frame}.png"

#- maps/ ----------------------------------------------------------------------
TILE_IMAGES := $(wildcard maps/*.ase)
OUT_TILE_IMAGES := $(TILE_IMAGES:%.ase=$(FINAL_OUT_DIR)/%.png)

tiles: $(OUT_TILE_IMAGES)

$(FINAL_OUT_DIR)/maps/%.png: TMP_PNG = $(@:%.png=%-tmp.png)
$(FINAL_OUT_DIR)/maps/%.png: maps/%.ase
	mkdir -p $(FINAL_OUT_DIR)/maps
	asesplit $< $(TMP_PNG)
	convert -alpha set -channel RGBA \
		-fill '#22203460' -opaque '#ff00ff' \
		-fill '#ffffff20' -opaque '#00ffff' \
		$(TMP_PNG) - \
	| $(TOOLS_DIR)/pad-map-tiles --padding 2 --tilesize 64 --output $@
	rm $(TMP_PNG)

#- ui/ ------------------------------------------------------------------------
# Still images
UI_STILL_IMAGES := $(wildcard ui/*.still.ase)
OUT_UI_STILL_IMAGES := $(UI_STILL_IMAGES:%.still.ase=$(OUT_DIR)/%.png)

$(OUT_DIR)/ui/%.png: ui/%.still.ase
	mkdir -p $(OUT_DIR)/ui
	asesplit $< $@

# Anims
UI_ANIM_IMAGES := $(wildcard ui/*.anim.ase)
OUT_UI_ANIM_FIRST_FRAMES := $(UI_ANIM_IMAGES:%.anim.ase=$(OUT_DIR)/%_0.png)

$(OUT_DIR)/ui/%_0.png: ui/%.anim.ase
	mkdir -p $(OUT_DIR)/ui
	# $(*F) is the % part of the target
	asesplit $< "$(OUT_DIR)/ui/$(*F)_{frame}.png"

# Slices. We don't know the name of the slices, so use a "timestamp" file to
# track the target status
UI_SLICES_IMAGES := $(wildcard ui/*.slices.ase)
OUT_UI_SLICES_IMAGES := $(UI_SLICES_IMAGES:%.slices.ase=$(OUT_DIR)/%.slices.timestamp)

$(OUT_DIR)/ui/%.slices.timestamp: ui/%.slices.ase
	mkdir -p $(OUT_DIR)/ui
	asesplit --split-slices $< "$(OUT_DIR)/ui/{slice}.png"
	touch $@

ui: $(OUT_UI_STILL_IMAGES) $(OUT_UI_ANIM_FIRST_FRAMES) $(OUT_UI_SLICES_IMAGES)

#- ui/map-icons/ --------------------------------------------------------------
MAP_ICONS := $(wildcard ui/map-icons/*.ase)
OUT_MAP_ICONS := $(MAP_ICONS:%.ase=$(OUT_DIR)/%.png)

map-icons: $(OUT_MAP_ICONS)

$(OUT_DIR)/ui/map-icons/%.png: ui/map-icons/%.ase
	mkdir -p $(OUT_DIR)/ui/map-icons
	asesplit $< $@

#- ui/championship-icons/ -----------------------------------------------------
CHAMPIONSHIP_ICONS := $(wildcard ui/championship-icons/*.ase)
OUT_CHAMPIONSHIP_ICONS := $(CHAMPIONSHIP_ICONS:%.ase=$(OUT_DIR)/%.png)

championship-icons: $(OUT_CHAMPIONSHIP_ICONS)

$(OUT_DIR)/ui/championship-icons/%.png: ui/championship-icons/%.ase
	mkdir -p $(OUT_DIR)/ui/championship-icons
	asesplit $< $@

#- ui/input-icons/ ------------------------------------------------------------
INPUT_ICONS := ui/input-icons/input-icons.ase
OUT_INPUT_ICONS_DIR := $(OUT_DIR)/ui/input-icons

input-icons: $(OUT_INPUT_ICONS_DIR)/sides.png

$(OUT_INPUT_ICONS_DIR)/sides.png: $(INPUT_ICONS)
	mkdir -p $(OUT_INPUT_ICONS_DIR)

	asesplit --split-slices $< "$(OUT_INPUT_ICONS_DIR)/{slice}.png"
