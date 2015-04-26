DESKTOP_JAR=$(CURDIR)/desktop/build/libs/desktop-1.0.jar
GRADLEW=./gradlew --offline
RACE_CP=com.greenyetilab.race
EXECUTABLE=race

PACKR=tools/packr.jar
PACKR_OUT_DIR=packr-out

TIMESTAMP=`date +%y%m%d-%H%M`

DIST_OUT_BASE_DIR=dist-out
DIST_NAME=race-$(TIMESTAMP)
DIST_OUT_DIR=$(DIST_OUT_BASE_DIR)/$(DIST_NAME)

JDK_LINUX64_URL=https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u60-unofficial-linux-amd64-image.zip
JDK_LINUX64_ZIP=openjdk-linux64.zip

all: build

clean: clean-packr
	rm -f $(DESKTOP_JAR)

$(DESKTOP_JAR):
	${GRADLEW} desktop:dist

build: $(DESKTOP_JAR)

run: build
	cd android/assets && java -jar $(DESKTOP_JAR)

packer: build
	java -cp $(DESKTOP_JAR) $(RACE_CP).desktop.Packer $(CURDIR)

mappacker: build
	java -cp $(DESKTOP_JAR) $(RACE_CP).desktop.MapPacker core/assets/maps android/assets/maps

# Packr
$(JDK_LINUX64_ZIP):
	wget $(JDK_LINUX64_URL) -O $(JDK_LINUX64_ZIP)

$(PACKR_OUT_DIR)/$(EXECUTABLE): $(JDK_LINUX64_ZIP) $(DESKTOP_JAR)
	java -jar $(PACKR) \
		-platform linux64 \
		-jdk $(JDK_LINUX64_ZIP) \
		-executable $(EXECUTABLE) \
		-appjar $(DESKTOP_JAR) \
		-mainclass com/greenyetilab/race/desktop/DesktopLauncher \
		-outdir $(PACKR_OUT_DIR) \
		-minimizejre soft
	cd android/assets && cp -r maps screens ui race.atlas race.png uiskin.atlas $(PACKR_OUT_DIR)

packr: $(PACKR_OUT_DIR)/$(EXECUTABLE)

clean-packr:
	rm -rf $(PACKR_OUT_DIR)

dist: $(DESKTOP_JAR)
	rm -rf $(DIST_OUT_DIR)
	mkdir -p $(DIST_OUT_DIR)
	cd android/assets && cp -r maps screens ui race.atlas race.png uiskin.atlas $(CURDIR)/$(DIST_OUT_DIR)/
	cp $(DESKTOP_JAR) $(DIST_OUT_DIR)/race.jar
	cp install/race.sh $(DIST_OUT_DIR)/
	chmod +x $(DIST_OUT_DIR)/race.sh
	cd $(DIST_OUT_BASE_DIR) && tar cf $(DIST_NAME).tar $(DIST_NAME)
	bzip2 -9 $(DIST_OUT_DIR).tar
	rm -rf $(DIST_OUT_DIR)
