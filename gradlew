#!/usr/bin/env sh
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_EXE="${JAVA_HOME:+$JAVA_HOME/bin/java}"
JAVA_EXE="${JAVA_EXE:-java}"
exec "$JAVA_EXE" -classpath "$DIR/gradle/wrapper/*" org.gradle.wrapper.GradleWrapperMain "$@"
