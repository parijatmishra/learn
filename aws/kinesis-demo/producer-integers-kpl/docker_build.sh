#!/usr/bin/env bash
# Build Java project, and then create a dist/ folder with everything required
# to build a Docker image and push to ECR
# 
# Include codebuild buildspec required to build Docker image and push to ECR
#
## Edit these to suit your project
set -evx

export AWS_DEFAULT_REGION=us-east-1
export AWS_REGION=${AWS_REGION:-$AWS_DEFAULT_REGION}
export AWS_ACCOUNT_ID=`aws sts get-caller-identity --query Account --output text`

export IMAGE_REGISTRY=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

export PROJECTDIR=.
export MVN_ARTIFACT_ID=$(cd "${PROJECTDIR}" && \
    mvn -q -Dexec.executable='echo' \
        -Dexec.args='${project.artifactId}' \
        --non-recursive \
        exec:exec)
export MVN_VERSION=$(cd "${PROJECTDIR}" && \
    mvn -q -Dexec.executable='echo' \
        -Dexec.args='${project.version}' \
        --non-recursive \
        exec:exec)
export GITHASH=$(git rev-parse HEAD)
export IMAGE_NAME=${MVN_ARTIFACT_ID}
export IMAGE_REPO_NAME=${IMAGE_NAME}
export IMAGE_TAG=${MVN_VERSION}-${GITHASH}

export JARFILE=producer-integers-kpl.jar # We overrode the shaded jar name in pom.xml
# export JARFILE=${MVN_ARTIFACT_ID}-${MVN_VERSION}.jar ## Normally this would be name of the shaded jar
export JARPATH=${PROJECTDIR}/target/${JARFILE}

## Don't edit these -- automatically computed
pushd .
cd $PROJECTDIR
mvn package
if [ $? -ne 0 ]; then
    echo "Maven build error"
    exit 3
fi
popd
docker build -t "${IMAGE_REGISTRY}/${IMAGE_REPO_NAME}:${IMAGE_TAG}" -t "${IMAGE_REGISTRY}/${IMAGE_REPO_NAME}:LATEST" .

DOCKER_MAJOR_VERSION=$(docker --version | cut -f 3 -d ' ' | cut -f 1 -d '.')
if [ ${DOCKER_MAJOR_VERSION} -ge 17 ]; then
    # Create and execute CLI for docker version 17.x and higher
    $(aws ecr get-login --no-include-email --region ${AWS_DEFAULT_REGION})
else
    # Create and execute CLI for docker version 1.12.x
    # Note: we do not explicitly check the version -- we are blindly assuming that the version is 1.12.x
    $(aws ecr get-login --region ${AWS_DEFAULT_REGION})
fi
docker push "${IMAGE_REGISTRY}/${IMAGE_REPO_NAME}:${IMAGE_TAG}"
docker push "${IMAGE_REGISTRY}/${IMAGE_REPO_NAME}:LATEST"

