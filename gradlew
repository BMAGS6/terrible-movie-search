#!/bin/sh
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/v8.10.2/gradle/wrapper/gradle-wrapper.jar"
EXPECTED_SHA="2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading the official Gradle 8.10.2 wrapper JAR..." >&2
    TEMP_JAR="$WRAPPER_JAR.download"
    trap 'rm -f "$TEMP_JAR"' EXIT HUP INT TERM
    if command -v curl >/dev/null 2>&1; then
        curl --fail --location --silent --show-error "$WRAPPER_URL" --output "$TEMP_JAR"
    elif command -v wget >/dev/null 2>&1; then
        wget --quiet "$WRAPPER_URL" -O "$TEMP_JAR"
    else
        echo "Install curl or wget to download the Gradle wrapper JAR." >&2
        exit 1
    fi
    ACTUAL_SHA=$(sha256sum "$TEMP_JAR" | cut -d ' ' -f 1)
    if [ "$ACTUAL_SHA" != "$EXPECTED_SHA" ]; then
        echo "Gradle wrapper JAR checksum mismatch; refusing to run it." >&2
        exit 1
    fi
    mv "$TEMP_JAR" "$WRAPPER_JAR"
    trap - EXIT HUP INT TERM
fi

if [ -n "${JAVA_HOME:-}" ]; then
    JAVA_EXE="$JAVA_HOME/bin/java"
else
    JAVA_EXE=java
fi
exec "$JAVA_EXE" -Dorg.gradle.appname=gradlew -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
