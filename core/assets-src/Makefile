export PATH := $(PWD)/../../bin:$(PATH)

ASSETS_DIR=../assets

VEHICLES_SUBDIR=sprites/vehicles

all: $(ASSETS_DIR) hud anims

$(ASSETS_DIR):
	mkdir -p $(ASSETS_DIR)/$(VEHICLES_SUBDIR)

clean: clean-anims

# Hud
# Just enough files to trigger the target
HUD_FILES = $(ASSETS_DIR)/sprites/hud-action.png $(ASSETS_DIR)/sprites/hud-left.png

hud: $(HUD_FILES)

$(HUD_FILES): sprites/hud-action.png sprites/hud-left.png sprites/hud.py
	sprites/hud.py $(ASSETS_DIR)/sprites

# Anims
ANIM_PREFIXES = $(patsubst %.anim.ase,%,$(wildcard sprites/*.anim.ase))
ANIM_FIRST_FRAMES = $(addprefix $(ASSETS_DIR)/,$(addsuffix _0.png,$(ANIM_PREFIXES)))

anims: $(ANIM_FIRST_FRAMES)

clean-anims:
	rm $(ANIM_FIRST_FRAMES)

$(ASSETS_DIR)/sprites/%_0.png: sprites/%.anim.ase
	aseprite --batch $< --save-as $(patsubst %_0.png,%.png,$@)
	rename-aseprite-frames $(patsubst %_0.png,%,$@)
