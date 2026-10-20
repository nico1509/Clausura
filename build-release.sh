#!/usr/bin/env bash
# Builds a signed release APK inside Docker.
#
#   ./build-release.sh /path/to/keystore.jks <key-alias>
#
# Store and key password are prompted for. Output: app/build/outputs/apk/release/
set -euo pipefail

if [ $# -ne 2 ]; then
    echo "Usage: $0 <keystore-file> <key-alias>" >&2
    exit 1
fi

KEYSTORE=$(realpath "$1")
KEY_ALIAS=$2
PROJECT_DIR=$(cd "$(dirname "$0")" && pwd)
IMAGE=clausura-build

[ -f "$KEYSTORE" ] || { echo "Keystore not found: $KEYSTORE" >&2; exit 1; }

read -rsp "Keystore password: " STORE_PASSWORD; echo
read -rsp "Key password (empty = same as keystore): " KEY_PASSWORD; echo
KEY_PASSWORD=${KEY_PASSWORD:-$STORE_PASSWORD}

docker build -t "$IMAGE" "$PROJECT_DIR/docker"

mkdir -p "$PROJECT_DIR/.docker-cache"

# Passwords go in via env (ORG_GRADLE_PROJECT_* become Gradle properties),
# so they never end up in a file or on the command line.
export ORG_GRADLE_PROJECT_RELEASE_STORE_FILE=/keystore/release.keystore
export ORG_GRADLE_PROJECT_RELEASE_STORE_PASSWORD=$STORE_PASSWORD
export ORG_GRADLE_PROJECT_RELEASE_KEY_ALIAS=$KEY_ALIAS
export ORG_GRADLE_PROJECT_RELEASE_KEY_PASSWORD=$KEY_PASSWORD

docker run --rm \
    --user "$(id -u):$(id -g)" \
    -v "$PROJECT_DIR:/project" \
    -v "$KEYSTORE:/keystore/release.keystore:ro" \
    -e ORG_GRADLE_PROJECT_RELEASE_STORE_FILE \
    -e ORG_GRADLE_PROJECT_RELEASE_STORE_PASSWORD \
    -e ORG_GRADLE_PROJECT_RELEASE_KEY_ALIAS \
    -e ORG_GRADLE_PROJECT_RELEASE_KEY_PASSWORD \
    "$IMAGE" \
    bash ./gradlew --no-daemon clean assembleRelease

ls -l "$PROJECT_DIR/app/build/outputs/apk/release/"
