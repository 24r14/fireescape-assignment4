#!/bin/sh
# Minimal POSIX launcher for the bundled Gradle wrapper.
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd) || exit 1
if [ -n "$JAVA_HOME" ]; then
    JAVA_EXE="$JAVA_HOME/bin/java"
else
    JAVA_EXE=java
fi
exec "$JAVA_EXE" -Dfile.encoding=UTF-8 -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
