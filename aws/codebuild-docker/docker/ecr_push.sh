#!/usr/bin/env bash
source vars.sh
# Needs additional variables to be set
[ -z "$AWS_DEFAULT_REGION" ] && (echo "Environment variable AWS_DEFAULT_REGION is not set" && exit 1)
[ -z "$AWS_ACCOUNT_ID" ] && (echo "Environment variable AWS_ACCOUNT_ID is not set" && exit 2)

$(aws ecr get-login --region ${AWS_DEFAULT_REGION})
docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGETAG}
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}:${IMAGETAG}
