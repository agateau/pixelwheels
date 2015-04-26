DESKTOP_JAR=$(CURDIR)/desktop/build/libs/desktop-1.0.jar
GRADLEW=./gradlew --offline
RACE_CP=com.greenyetilab.race
all: build

clean: clean-packr

$(DESKTOP_JAR):
	${GRADLEW} desktop:dist

build: $(DESKTOP_JAR)

run: build
	cd android/assets && java -jar $(DESKTOP_JAR)

packer: build
	java -cp $(DESKTOP_JAR) $(RACE_CP).desktop.Packer $(CURDIR)

mappacker: build
	java -cp $(DESKTOP_JAR) $(RACE_CP).desktop.MapPacker core/assets/maps android/assets/maps
