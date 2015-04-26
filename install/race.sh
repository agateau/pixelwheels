#!/bin/sh
set -e
if ! which java > /dev/null 2>&1 ; then
    echo "No java binary found, you may need to install a Java Runtime Environment (JRE)"
    exit 1
fi
java -jar race.jar "$@"
