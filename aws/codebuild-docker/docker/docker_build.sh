#!/usr/bin/env bash
source vars.sh
[ -z "$PROJECTDIR" ] && echo "Environment variable PROJECTDIR is not set." && exit 1
[ -z "$MVN_ARTIFACT_ID" ] && echo "Environment variable MVN_ARTIFACT_ID is not set" && exit 1
[ -z "$IMAGE_REPO_NAME" ] && echo "Environment variable IMAGE_REPO_NAME is not set" && exit 1
[ -z "$IMAGE_TAG" ] && echo "Environment variable IMAGE_TAG is not set" && exit 1
[ -z "$JARFILE" ] && echo "Environment variable JARFILE is not set" && exit 1

[ ! -e "$JARFILE" ] && (echo "Could not find jar $JARFILE.  Did you run mvn package?" && exit 2)

cp ${JARFILE} docker/app.jar
cd docker/
docker build --force-rm \
    -t ${IMAGE_REPO_NAME}:${IMAGE_TAG} \
    .
rm app.jar
cd ..
