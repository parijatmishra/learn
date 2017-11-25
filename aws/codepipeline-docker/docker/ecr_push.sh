#!/usr/bin/env bash
set -evx
VARS="$(dirname $0)/_vars.sh"
if [ ! -f "${VARS}" ]; then
    exit 1
fi
source "${VARS}"
[ -z "$IMAGE_REPO_NAME" ] && (echo "Environment variable IMAGE_REPO_NAME is not set" && exit 1)
[ -z "$IMAGE_TAG" ] && (echo "Environment variable IMAGE_TAG is not set" && exit 1)
[ -z "$AWS_DEFAULT_REGION" ] && (echo "Environment variable AWS_DEFAULT_REGION is not set" && exit 1)
[ -z "$AWS_ACCOUNT_ID" ] && (echo "Environment variable AWS_ACCOUNT_ID is not set" && exit 2)

DOCKER_MAJOR_VERSION=$(docker --version | cut -f 3 -d ' ' | cut -f 1 -d '.')
if [ ${DOCKER_MAJOR_VERSION} -ge 17 ]; then
    # Create and execute CLI for docker version 17.x and higher
    $(aws ecr get-login --no-include-email --region ${AWS_DEFAULT_REGION})
else
    # Create and execute CLI for docker version 1.12.x
    # Note: we do not explicitly check the version -- we are blindly assuming that the version is 1.12.x
    $(aws ecr get-login --region ${AWS_DEFAULT_REGION})
fi
docker tag "${IMAGE_REPO_NAME}:${IMAGE_TAG}" "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGE_TAG}"
docker push "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGE_TAG}"
