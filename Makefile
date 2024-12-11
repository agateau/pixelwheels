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

# Give the user the opportunity to customize environment variables
-include ./env.mk

DESKTOP_JAR=$(CURDIR)/desktop/build/libs/desktop-1.0.jar
TOOLS_JAR=$(CURDIR)/tools/build/libs/tools-1.0.jar
GRADLEW=./gradlew
ifdef OFFLINE
	GRADLEW=./gradlew --offline
endif

GAME_CP=com.agateau.pixelwheels
EXECUTABLE=pixelwheels

include ./version.properties

DIST_OUT_BASE_DIR=dist-out
DIST_NAME=$(EXECUTABLE)-$(VERSION)
DIST_OUT_DIR=$(DIST_OUT_BASE_DIR)/$(DIST_NAME)

DESKTOP_RUN_DIST_NAME=$(EXECUTABLE)-$(VERSION)-linux64
ANDROID_ITCHIO_RUN_DIST_NAME=$(EXECUTABLE)-itchio-$(VERSION)
ANDROID_GPLAY_RUN_DIST_NAME=$(EXECUTABLE)-gplay-$(VERSION)

ARCHIVE_DIR=$(CURDIR)/archives

ANDROID_PACKAGE_NAME=com.agateau.tinywheels.android

ANDROID_FLAVOR=itchio

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

all: desktop-build

# Clean
.PHONY: clean
clean: clean-assets clean-desktop clean-tools

.PHONY: clean-desktop
clean-desktop:
	rm -f $(DESKTOP_JAR)

.PHONY: clean-tools
clean-tools:
	rm -f $(TOOLS_JAR)

# Build
$(TOOLS_JAR):
	${GRADLEW} tools:dist

.PHONY: tools
tools: $(TOOLS_JAR)

.PHONY: desktop-build
desktop-build:
	${GRADLEW} desktop:dist

.PHONY: android-build
android-build:
	$(GRADLEW) android:build

# Run
.PHONY: run
run: desktop-run

.PHONY: desktop-run
desktop-run: desktop-build
	cd android/assets && java -jar $(DESKTOP_JAR)

.PHONY: android-run
android-run: android-build
	adb uninstall $(ANDROID_PACKAGE_NAME) || true
	adb install -f android/build/outputs/apk/$(ANDROID_FLAVOR)/debug/android-$(ANDROID_FLAVOR)-debug.apk
	adb shell am start -n $(ANDROID_PACKAGE_NAME)/com.agateau.pixelwheels.android.AndroidLauncher

# Classic Unix `make install` target
.PHONY: install
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
.PHONY: packer
packer: tools assets
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.Packer

.PHONY: mapscreenshotgenerator
mapscreenshotgenerator: tools
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.MapScreenshotGenerator

.PHONY: vehiclethumbnailgenerator
vehiclethumbnailgenerator: tools
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.VehicleThumbnailGenerator tmp

.PHONY: assets
assets:
	$(MAKE) -C core/assets-src

.PHONY: clean-assets
clean-assets:
	$(MAKE) -C core/assets-src clean

# Automatically regenerates assets when ase files are modified (requires the
# `entr` command)
.PHONY: auto-assets
auto-assets:
	find core/assets-src -name '*.ase' | entr $(MAKE) assets packer

# Dist
.PHONY: desktop-archives
desktop-archives:
	@rm -rf $(DIST_OUT_BASE_DIR)
	@mkdir -p $(DIST_OUT_BASE_DIR)

	@echo Creating desktop archives
	@tools/packaging/create-archives $(VERSION)

	@echo Moving desktop archives
	@mkdir -p $(ARCHIVE_DIR)
	mv -v $(DIST_OUT_BASE_DIR)/*.zip $(ARCHIVE_DIR)

.PHONY: apk-archives
apk-archives:
	@echo Creating apk files
	@$(GRADLEW) android:assembleRelease
	@echo Moving apk files
	@mkdir -p $(ARCHIVE_DIR)
	@for store in itchio gplay ; do \
		mv android/build/outputs/apk/$$store/release/android-$$store-release.apk $(ARCHIVE_DIR)/$(EXECUTABLE)-$$store-$(VERSION).apk ; \
	done

.PHONY: aab-archives
aab-archives:
	@echo Creating aab
	@$(GRADLEW) bundleGPlayRelease
	@echo Moving aab file
	mv android/build/outputs/bundle/gplayRelease/android-gplay-release.aab $(ARCHIVE_DIR)/$(ANDROID_GPLAY_RUN_DIST_NAME).aab

.PHONY: desktop-dist
desktop-dist: assets packer check desktop-build desktop-archives

.PHONY: clean-desktop-dist
clean-desktop-dist: clean desktop-dist

.PHONY: dist
dist: desktop-dist apk-archives aab-archives

.PHONY: clean-dist
clean-dist: clean dist

.PHONY: desktop-run-from-dist
desktop-run-from-dist:
	@echo "This target only works on Linux right now"
	rm -rf tmp
	mkdir -p tmp
	unzip $(ARCHIVE_DIR)/$(DESKTOP_RUN_DIST_NAME).zip -d tmp
	tmp/$(DESKTOP_RUN_DIST_NAME)/pixelwheels

.PHONY: android-run-from-dist
android-run-from-dist:
	# uninstall any existing version in case we have an unsigned version installed
	adb uninstall $(ANDROID_PACKAGE_NAME) || true
	adb install -f $(ARCHIVE_DIR)/$(ANDROID_ITCHIO_RUN_DIST_NAME).apk
	adb shell am start -n $(ANDROID_PACKAGE_NAME)/com.agateau.pixelwheels.android.AndroidLauncher

# coding style
.PHONY: codingstyle-check
codingstyle-check:
	tools/apply-codingstyle --check

.PHONY: codingstyle-apply
codingstyle-apply:
	tools/apply-codingstyle

# Tag
.PHONY: tag
tag:
	git tag -f -m "Pixel Wheels $(VERSION)" $(VERSION)

.PHONY: tagpush
tagpush: tag
	git push
	git push --tags

# Uploading
.PHONY: fastlane-beta
fastlane-beta:
	fastlane supply --track beta --aab $(ARCHIVE_DIR)/$(ANDROID_GPLAY_RUN_DIST_NAME).aab

.PHONY: upload
upload:
	ci/upload-build pixelwheels \
		$(ARCHIVE_DIR)/$(DIST_NAME)-*.zip \
		$(ARCHIVE_DIR)/$(ANDROID_GPLAY_RUN_DIST_NAME).apk \
		$(ARCHIVE_DIR)/$(ANDROID_ITCHIO_RUN_DIST_NAME).apk \

.PHONY: butler-upload
butler-upload:
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(DIST_NAME)-linux* agateau/pixelwheels:linux-stable
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(DIST_NAME)-mac* agateau/pixelwheels:macos-stable
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(DIST_NAME)-windows* agateau/pixelwheels:windows-stable
	butler push --userversion $(VERSION) $(ARCHIVE_DIR)/$(ANDROID_ITCHIO_RUN_DIST_NAME).apk agateau/pixelwheels:android-stable

.PHONY: gh-upload
gh-upload:
	gh release create ${VERSION} \
		-F .changes/${VERSION}.md \
		${ARCHIVE_DIR}/${DIST_NAME}-linux* \
		${ARCHIVE_DIR}/${DIST_NAME}-mac* \
		${ARCHIVE_DIR}/${DIST_NAME}-windows* \
		${ARCHIVE_DIR}/${ANDROID_ITCHIO_RUN_DIST_NAME}.apk

# Cleaning conf
.PHONY: backup-desktop-conf
backup-desktop-conf:
	mkdir -p $(CONF_BACKUP_DIR)
	cp ~/.config/agateau.com/pixelwheels.conf $(CONF_BACKUP_DIR) || true
	cp -R ~/.local/share/pixelwheels $(CONF_BACKUP_DIR)

.PHONY: restore-desktop-conf
restore-desktop-conf:
	@if ! [ -d "$(CONF_BACKUP_DIR)" ] ; then echo "$(CONF_BACKUP_DIR) does not exist. No backup to restore."; exit 1; fi
	rm ~/.config/agateau.com/pixelwheels.conf
	rm -rf ~/.local/share/pixelwheels
	cp $(CONF_BACKUP_DIR)/pixelwheels.conf ~/.config/agateau.com || true
	cp -R $(CONF_BACKUP_DIR)/pixelwheels ~/.local/share

.PHONY: clean-desktop-conf
clean-desktop-conf:
	rm -f ~/.config/agateau.com/pixelwheels.conf
	rm -rf ~/.local/share/pixelwheels

.PHONY: clean-android-conf
clean-android-conf:
	adb shell "pm clear $(ANDROID_PACKAGE_NAME)"

# tests
.PHONY: check
check: codingstyle-check po-check font-check
	@$(GRADLEW) check
	@$(GRADLEW) test

.PHONY: smoke-tests
smoke-tests: smoke-tests-from-dist smoke-tests-from-install

.PHONY: smoke-tests-from-dist
smoke-tests-from-dist:
	rm -rf tmp
	mkdir -p tmp
	unzip $(ARCHIVE_DIR)/$(DESKTOP_RUN_DIST_NAME).zip -d tmp
	tools/smoke-test tmp/$(DESKTOP_RUN_DIST_NAME)/pixelwheels

.PHONY: smoke-tests-from-install
smoke-tests-from-install:
	rm -rf tmp
	mkdir -p tmp
	$(MAKE) install DESTDIR=$(CURDIR)/tmp
	tools/smoke-test tmp/usr/local/bin/pixelwheels

# Translations
.PHONY: po-update
po-update:
	tools/po-update

.PHONY: po-check
po-check:
	tools/po-update --check

.PHONY: font-update
font-update:
	tools/fonts/font-update

.PHONY: font-check
font-check:
	tools/fonts/font-update --check
