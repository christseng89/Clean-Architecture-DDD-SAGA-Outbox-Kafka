#!/usr/bin/env sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROJECT_DIR="$SCRIPT_DIR/food-ordering-system"

if [ ! -f "$PROJECT_DIR/pom.xml" ]; then
  echo "Could not find Maven project at: $PROJECT_DIR" >&2
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Java is required but was not found on PATH." >&2
  echo "Set JAVA_HOME and add \$JAVA_HOME/bin to PATH, then rerun this script." >&2
  exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven is required but was not found on PATH." >&2
  echo "Install Maven or add mvn to PATH, then rerun this script." >&2
  exit 1
fi

cd "$PROJECT_DIR"

echo "Running all unit tests for food-ordering-system..."
echo "Project: $PROJECT_DIR"

mvn -B clean test "$@"
