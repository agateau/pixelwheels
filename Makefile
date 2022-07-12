# Shell to use, stop on errors, stop on undefined variables, report errors
# if a command in a pipe fails (not just the last)
SHELL := bash
.SHELLFLAGS := -euo pipefail -c

# Do not start a new shell for each command of a target
# Makes it possible to have `cd foo` on its own line. Be sure to configure the
# shell to stop on errors though (the -e in .SHELLFLAGS)
.ONESHELL:

MAKEFLAGS += --warn-undefined-variables
MAKEFLAGS += --no-builtin-rules

DESKTOP_JAR=$(CURDIR)/desktop/build/libs/desktop-1.0.jar
TOOLS_JAR=$(CURDIR)/tools/build/libs/tools-1.0.jar
GRADLEW=./gradlew
ifdef OFFLINE
	GRADLEW=./gradlew --offline
endif

GAME_CP=com.agateau.pixelwheels
EXECUTABLE=pixelwheels

include version.properties

DIST_OUT_BASE_DIR=dist-out
DIST_NAME=$(EXECUTABLE)-$(VERSION)
DIST_OUT_DIR=$(DIST_OUT_BASE_DIR)/$(DIST_NAME)

DESKTOP_RUN_DIST_NAME=$(EXECUTABLE)-$(VERSION)-linux64
ANDROID_ITCHIO_RUN_DIST_NAME=$(EXECUTABLE)-itchio-$(VERSION)
ANDROID_GPLAY_RUN_DIST_NAME=$(EXECUTABLE)-gplay-$(VERSION)

ARCHIVE_DIR=$(CURDIR)/archives

ANDROID_PACKAGE_NAME=com.agateau.tinywheels.android

CONF_BACKUP_DIR=$(CURDIR)/.conf-backup

# Install variables
## Standard directory variables
prefix = /usr/local
exec_prefix = $(prefix)
bindir = $(exec_prefix)/bin
datadir = $(prefix)/share
libdir = $(exec_prefix)/lib

## Handy shortcuts
INSTALL_BIN_DIR=$(DESTDIR)$(bindir)
INSTALL_JAR_DIR=$(DESTDIR)$(libdir)/pixelwheels
INSTALL_SHARE_DIR=$(DESTDIR)$(datadir)

INSTALL_ASSETS_DIR=$(INSTALL_SHARE_DIR)/pixelwheels

INSTALL_BIN_PATH=$(INSTALL_BIN_DIR)/pixelwheels
INSTALL_JAR_PATH=$(INSTALL_JAR_DIR)/$(GAME_CP).jar

# Update VERSION variable for snapshots
ifdef SNAPSHOT
	BRANCH:=$(shell git rev-parse --abbrev-ref HEAD | sed s,/,-,g)
	VERSION:=$(VERSION)+$(BRANCH)-$(shell git show --no-patch --format="%cd-%h" --date=format:%Y%m%dT%H%M%S)
endif

all: build

clean: clean-assets clean-desktop clean-tools

clean-desktop:
	rm -f $(DESKTOP_JAR)

clean-tools:
	rm -f $(TOOLS_JAR)

$(TOOLS_JAR):
	${GRADLEW} tools:dist

build:
	${GRADLEW} desktop:dist

tools: $(TOOLS_JAR)

run: build
	cd android/assets && java -jar $(DESKTOP_JAR)

# Classic Unix `make install` target
install:
	mkdir -p $(INSTALL_JAR_DIR)
	mkdir -p $(INSTALL_BIN_DIR)
	mkdir -p $(INSTALL_ASSETS_DIR)

	cp $(DESKTOP_JAR) $(INSTALL_JAR_PATH)
	cp -a android/assets/* $(INSTALL_ASSETS_DIR)

	echo -e "#!/bin/bash\ncd $(INSTALL_ASSETS_DIR)\nexec java -jar $(INSTALL_JAR_PATH)" > $(INSTALL_BIN_PATH)
	chmod +x $(INSTALL_BIN_PATH)

	cp -a tools/packaging/linux/share/* $(INSTALL_SHARE_DIR)

# Assets
packer: tools assets
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.Packer

mapscreenshotgenerator: tools
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.MapScreenshotGenerator

assets:
	$(MAKE) -C core/assets-src

clean-assets:
	$(MAKE) -C core/assets-src clean

# Automatically regenerates assets when ase files are modified (requires the
# `entr` command)
auto-assets:
	find core/assets-src -name '*.ase' | entr $(MAKE) assets packer

# Dist
desktop-archives:
	@rm -rf $(DIST_OUT_BASE_DIR)
	@mkdir -p $(DIST_OUT_BASE_DIR)

	@echo Creating desktop archives
	@tools/packaging/create-archives $(VERSION)

	@echo Moving desktop archives
	@mkdir -p $(ARCHIVE_DIR)
	mv -v $(DIST_OUT_BASE_DIR)/*.zip $(ARCHIVE_DIR)

apk-archives:
	@echo Creating apk files
	@$(GRADLEW) android:assembleRelease
	@echo Moving apk files
	@mkdir -p $(ARCHIVE_DIR)
	@for store in itchio gplay ; do \
		mv android/build/outputs/apk/$$store/release/android-$$store-release.apk $(ARCHIVE_DIR)/$(EXECUTABLE)-$$store-$(VERSION).apk ; \
	done


dist: assets packer check build desktop-archives apk-archives

desktop-dist: assets packer check build desktop-archives

clean-desktop-dist: clean desktop-dist

clean-dist: clean dist

desktop-run-from-dist:
	@echo "This target only works on Linux right now"
	rm -rf tmp
	mkdir -p tmp
	unzip $(ARCHIVE_DIR)/$(DESKTOP_RUN_DIST_NAME).zip -d tmp
	tmp/$(DESKTOP_RUN_DIST_NAME)/pixelwheels

android-run-from-dist:
	# uninstall any existing version in case we have an unsigned version installed
	adb uninstall $(ANDROID_PACKAGE_NAME) || true
	adb install -f $(ARCHIVE_DIR)/$(ANDROID_ITCHIO_RUN_DIST_NAME).apk
	adb shell am start -n $(ANDROID_PACKAGE_NAME)/com.agateau.pixelwheels.android.AndroidLauncher

# coding style
codingstyle-check:
	tools/apply-codingstyle --check

codingstyle-apply:
	tools/apply-codingstyle

# Tag
tag:
	git tag -f -m "Pixel Wheels $(VERSION)" $(VERSION)

tagpush: tag
	git push
	git push --tags

# Uploading
fastlane-beta:
	fastlane supply --track beta --apk $(ARCHIVE_DIR)/$(ANDROID_GPLAY_RUN_DIST_NAME).apk

upload:
	ci/upload-build pixelwheels \
		$(ARCHIVE_DIR)/$(DIST_NAME)-*.zip \
		$(ARCHIVE_DIR)/$(ANDROID_GPLAY_RUN_DIST_NAME).apk \
		$(ARCHIVE_DIR)/$(ANDROID_ITCHIO_RUN_DIST_NAME).apk \

butler-upload:
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(DIST_NAME)-linux* agateau/pixelwheels:linux-stable
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(DIST_NAME)-mac* agateau/pixelwheels:macos-stable
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(DIST_NAME)-windows* agateau/pixelwheels:windows-stable
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(ANDROID_ITCHIO_RUN_DIST_NAME).apk agateau/pixelwheels:android-stable

gh-upload:
	gh release create ${VERSION} \
		-F .changes/${VERSION}.md \
		${ARCHIVE_DIR}/${DIST_NAME}-linux* \
		${ARCHIVE_DIR}/${DIST_NAME}-mac* \
		${ARCHIVE_DIR}/${DIST_NAME}-windows* \
		${ARCHIVE_DIR}/${ANDROID_ITCHIO_RUN_DIST_NAME}.apk

# Cleaning conf
backup-desktop-conf:
	mkdir -p $(CONF_BACKUP_DIR)
	cp ~/.config/agateau.com/pixelwheels.conf $(CONF_BACKUP_DIR) || true
	cp -R ~/.local/share/pixelwheels $(CONF_BACKUP_DIR)

restore-desktop-conf:
	@if ! [ -d "$(CONF_BACKUP_DIR)" ] ; then echo "$(CONF_BACKUP_DIR) does not exist. No backup to restore."; exit 1; fi
	rm ~/.config/agateau.com/pixelwheels.conf
	rm -rf ~/.local/share/pixelwheels
	cp $(CONF_BACKUP_DIR)/pixelwheels.conf ~/.config/agateau.com || true
	cp -R $(CONF_BACKUP_DIR)/pixelwheels ~/.local/share

clean-desktop-conf:
	rm -f ~/.config/agateau.com/pixelwheels.conf
	rm -rf ~/.local/share/pixelwheels

clean-android-conf:
	adb shell "pm clear $(ANDROID_PACKAGE_NAME)"

# tests
check: codingstyle-check po-check
	@$(GRADLEW) check
	@$(GRADLEW) test

smoke-tests: smoke-tests-from-dist smoke-tests-from-install

smoke-tests-from-dist:
	rm -rf tmp
	mkdir -p tmp
	unzip $(ARCHIVE_DIR)/$(DESKTOP_RUN_DIST_NAME).zip -d tmp
	tools/smoke-test tmp/$(DESKTOP_RUN_DIST_NAME)/pixelwheels

smoke-tests-from-install:
	rm -rf tmp
	mkdir -p tmp
	$(MAKE) install DESTDIR=$(CURDIR)/tmp
	tools/smoke-test tmp/usr/local/bin/pixelwheels

# Translations
po-update:
	tools/po-update

po-check:
	tools/po-update --check

.PHONY: desktop-dist apk-dist dist clean-dist tag tagpush fastlane-beta check tools build release-archives
