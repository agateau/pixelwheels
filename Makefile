DESKTOP_JAR=$(CURDIR)/desktop/build/libs/desktop-1.0.jar
TOOLS_JAR=$(CURDIR)/tools/build/libs/tools-1.0.jar
GRADLEW=./gradlew --offline
GAME_CP=com.greenyetilab.tinywheels
EXECUTABLE=tinywheels

PACKR=tools/packr.jar
PACKR_OUT_DIR=packr-out

VERSION=0.2

DIST_OUT_BASE_DIR=dist-out
DIST_NAME=$(EXECUTABLE)-$(VERSION)
DIST_OUT_DIR=$(DIST_OUT_BASE_DIR)/$(DIST_NAME)

JDK_LINUX64_URL=https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u60-unofficial-linux-amd64-image.zip
JDK_LINUX64_ZIP=openjdk-linux64.zip

ARCHIVE_DIR=$(CURDIR)/archives

ifdef SNAPSHOT
	VERSION:=$(VERSION).$(shell date +%y%m%d-%H%M)
endif

all: build

clean: clean-packr
	rm -f $(DESKTOP_JAR) $(TOOLS_JAR)

$(TOOLS_JAR):
	${GRADLEW} tools:dist

build:
	${GRADLEW} desktop:dist

tools: $(TOOLS_JAR)

run: build
	cd android/assets && java -jar $(DESKTOP_JAR)

packer: tools
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.Packer

mapscreenshotgenerator: tools
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.MapScreenshotGenerator

mappacker: tools
	java -cp $(TOOLS_JAR) $(GAME_CP).tools.MapPacker core/assets/maps android/assets/maps


# Packr
$(JDK_LINUX64_ZIP):
	wget $(JDK_LINUX64_URL) -O $(JDK_LINUX64_ZIP)

$(PACKR_OUT_DIR)/$(EXECUTABLE): $(JDK_LINUX64_ZIP) build
	java -jar $(PACKR) \
		-platform linux64 \
		-jdk $(JDK_LINUX64_ZIP) \
		-executable $(EXECUTABLE) \
		-appjar $(DESKTOP_JAR) \
		-mainclass com/greenyetilab/tinywheels/desktop/DesktopLauncher \
		-outdir $(PACKR_OUT_DIR) \
		-minimizejre soft
	cd android/assets && cp -r maps screens ui sprites.atlas sprites.png uiskin.atlas $(PACKR_OUT_DIR)

packr: $(PACKR_OUT_DIR)/$(EXECUTABLE)

clean-packr:
	rm -rf $(PACKR_OUT_DIR)

# Dist
dist: build
	@rm -rf $(DIST_OUT_DIR)
	@mkdir -p $(DIST_OUT_DIR)

	@echo Copying files
	@cp $(DESKTOP_JAR) $(DIST_OUT_DIR)/$(EXECUTABLE).jar
	@cp install/tinywheels.sh $(DIST_OUT_DIR)/
	@chmod +x $(DIST_OUT_DIR)/tinywheels.sh

	@echo Creating tarball
	@cd $(DIST_OUT_BASE_DIR) && tar cf $(DIST_NAME).tar $(DIST_NAME)
	@bzip2 -9 $(DIST_OUT_DIR).tar
	@rm -rf $(DIST_OUT_DIR)

	@echo Moving tarball
	@mkdir -p $(ARCHIVE_DIR)
	@mv $(DIST_OUT_DIR).tar.bz2 $(ARCHIVE_DIR)

# apk
apk:
	@echo Creating .apk
	@$(GRADLEW) android:assembleRelease
	@echo Moving .apk
	@mkdir -p $(ARCHIVE_DIR)
	@mv android/build/outputs/apk/android-release.apk $(ARCHIVE_DIR)/$(EXECUTABLE)-$(VERSION).apk

tag:
	git tag -f -m "Release Tiny Wheels $(VERSION)" $(VERSION)

tagpush: tag
	git push
	git push --tags

.PHONY: tools build
