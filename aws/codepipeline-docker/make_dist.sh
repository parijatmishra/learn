#!/usr/bin/env bash
# Build Java project, and then create a dist/ folder with everything required
# to build a Docker image and push to ECR
# 
# Include codebuild buildspec required to build Docker image and push to ECR
#
set -evx
export IMAGE_REPO_NAME=$1

## Edit these to suit your project
export APPDIR=app
export MVN_ARTIFACT_ID=spring-boot-rest-greeting
export AWS_DEFAULT_REGION=us-east-1
export AWS_ACCOUNT_ID=825739414361

## Don't edit these -- automatically computed
if [ -z "$CODEBUILD_RESOLVED_SOURCE_VERSION" ]; then
    export GITHASH=$(git rev-parse HEAD)
else
    export GITHASH=$CODEBUILD_RESOLVED_SOURCE_VERSION
fi
if [ ! -d "${APPDIR}" ]; then
    echo "Directory ${APPDIR} not found.  PWD=`pwd`."
    exit 2
fi

export MVN_VERSION=$(cd "${APPDIR}" && \
    mvn -q -Dexec.executable='echo' \
        -Dexec.args='${project.version}' \
        --non-recursive \
        exec:exec)
export JARFILE=${APPDIR}/target/${MVN_ARTIFACT_ID}-${MVN_VERSION}.jar
export IMAGE_TAG=${MVN_VERSION}-${GITHASH}

pushd .
cd $APPDIR
mvn -B package
if [ $? -ne 0 ]; then
    echo "Maven build error"
    exit 3
fi
popd

rm -rf dist/
mkdir -p dist
cp -a docker dist/
cp ${JARFILE} dist/docker/app.jar
cat > dist/docker/_vars.sh <<EOF
export IMAGE_REPO_NAME=${IMAGE_REPO_NAME}
export IMAGE_TAG=${IMAGE_TAG}
export AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION}
export AWS_ACCOUNT_ID=${AWS_ACCOUNT_ID}
EOF
