#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
VERSION=3.9.11
MVN="$ROOT/.mvn/wrapper/dists/apache-maven-$VERSION/bin/mvn"
if [ ! -x "$MVN" ]; then
  mkdir -p "$ROOT/.mvn/wrapper/dists"
  ZIP="$ROOT/.mvn/wrapper/dists/apache-maven-$VERSION-bin.zip"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$VERSION/apache-maven-$VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl --fail --location --silent --show-error "$URL" -o "$ZIP"
  else
    wget -q "$URL" -O "$ZIP"
  fi
  unzip -q -o "$ZIP" -d "$ROOT/.mvn/wrapper/dists"
  rm -f "$ZIP"
fi
exec "$MVN" -f "$ROOT/pom.xml" "$@"
