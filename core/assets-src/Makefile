ASSETS_DIR=../assets

VEHICLES_SUBDIR=sprites/vehicles

$(ASSETS_DIR)/$(VEHICLES_SUBDIR)/splash_0.png,$(ASSETS_DIR)/$(VEHICLES_SUBDIR)/splash_1.png: $(VEHICLES_SUBDIR)/splash.ase
	aseprite --batch $< --save-as $(ASSETS_DIR)/$(VEHICLES_SUBDIR)/splash_.png
	cd $(ASSETS_DIR)/$(VEHICLES_SUBDIR) && for x in 0 1 ; do \
		mv splash_$$((x+1)).png splash_$$x.png ; \
	done
