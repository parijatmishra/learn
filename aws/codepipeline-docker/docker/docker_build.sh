#!/usr/bin/env bash
set -evx
VARS="$(dirname $0)/_vars.sh"
if [ ! -f "${VARS}" ]; then
    exit 1
fi
source "${VARS}"
[ -z "$IMAGE_REPO_NAME" ] && echo "Environment variable IMAGE_REPO_NAME is not set" && exit 1
[ -z "$IMAGE_TAG" ] && echo "Environment variable IMAGE_TAG is not set" && exit 1

TMPDIR="docker_build_$$"
mkdir -p $TMPDIR
cp $(dirname $0)/app.jar $TMPDIR/app.jar
cp $(dirname $0)/Dockerfile $TMPDIR/
docker build --force-rm \
    -t ${IMAGE_REPO_NAME}:${IMAGE_TAG} \
    $TMPDIR/
rm -rf $TMPDIR
