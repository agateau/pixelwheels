DESKTOP_JAR=$(CURDIR)/desktop/build/libs/desktop-1.0.jar
GRADLEW=./gradlew --offline
RACE_CP=com.greenyetilab.race

$(DESKTOP_JAR):
	${GRADLEW} desktop:dist

desktop_jar: $(DESKTOP_JAR)

run: desktop_jar
	cd android/assets && java -jar $(DESKTOP_JAR)

packer: desktop_jar
	java -cp $(DESKTOP_JAR) $(RACE_CP).desktop.Packer $(CURDIR)

mappacker: desktop_jar
	java -cp $(DESKTOP_JAR) $(RACE_CP).desktop.MapPacker core/assets/maps android/assets/maps
