- Download SDK
- Download JDK

- setup env
    export JAVA_HOME=/path/to/jdk
    export ANDROID_HOME=/path/to/sdk
    export PATH=$JAVA_HOME/bin:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH

- Download Android things

    android update sdk --no-ui --filter platform-tools,android-21,build-tools-21.1.1

- Build

    git clone tinywheels
    cd tinywheels
    ./gradlew build
