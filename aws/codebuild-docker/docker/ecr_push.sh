#!/usr/bin/env bash
VARS="$(dirname $0)/_vars.sh"
if [ ! -f "${VARS}" ]; then
    echo  "I need a \"${VARS}\" to run"
    exit 1
fi
source "${VARS}"
[ -z "$IMAGE_REPO_NAME" ] && (echo "Environment variable IMAGE_REPO_NAME is not set" && exit 1)
[ -z "$IMAGE_TAG" ] && (echo "Environment variable IMAGE_TAG is not set" && exit 1)
[ -z "$AWS_DEFAULT_REGION" ] && (echo "Environment variable AWS_DEFAULT_REGION is not set" && exit 1)
[ -z "$AWS_ACCOUNT_ID" ] && (echo "Environment variable AWS_ACCOUNT_ID is not set" && exit 2)

$(aws ecr get-login --no-include-email --region ${AWS_DEFAULT_REGION})
echo "Tagging and pushing ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGE_TAG} ..."
docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGE_TAG}
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGE_TAG}
echo "Done"
