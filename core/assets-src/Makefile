export PATH := $(PWD)/../../bin:$(PATH)

ASSETS_DIR=../assets

VEHICLES_SUBDIR=sprites/vehicles

all: $(ASSETS_DIR) hud anims

$(ASSETS_DIR):
	mkdir -p $(ASSETS_DIR)/$(VEHICLES_SUBDIR)

clean: clean-anims

hud:
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
