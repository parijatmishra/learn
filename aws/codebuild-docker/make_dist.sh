#!/usr/bin/env bash
# Build Java project, and then create a dist/ folder with everything required
# to build a Docker image and push to ECR
# 
# Include codebuild buildspec required to build Docker image and push to ECR
#
## Edit these to suit your project
export PROJECTDIR=spring-boot-rest-greeting
export MVN_ARTIFACT_ID=spring-boot-rest-greeting
export IMAGE_REPO_NAME=spring-boot-rest-greeting
export AWS_DEFAULT_REGION=us-east-1
export AWS_ACCOUNT_ID=825739414361

## Don't edit these -- automatically computed
export GITHASH=$(git rev-parse HEAD)
if [ ! -d ${PROJECTDIR} ]; then
    echo "Directory ${PROJECTDIR} not found.  PWD=`pwd`."
    exit 2
fi

export MVN_VERSION=$(cd ${PROJECTDIR} && \
    mvn -q -Dexec.executable='echo' \
        -Dexec.args='${project.version}' \
        --non-recursive \
        exec:exec)
export JARFILE=${PROJECTDIR}/target/${MVN_ARTIFACT_ID}-${MVN_VERSION}.jar
export IMAGE_TAG=${MVN_VERSION}-${GITHASH}

pushd .
cd $PROJECTDIR
mvn package
if [ $? -ne 0 ]; then
    echo "Maven build error"
    exit 3
fi
popd

echo
echo "************"
echo "Copying distribution files to dist/"
rm -rf dist/
mkdir -p dist
cp -a docker dist/
cp -a codebuild dist/
cp ${JARFILE} dist/docker/app.jar
cat > dist/docker/_vars.sh <<EOF
export IMAGE_REPO_NAME=${IMAGE_REPO_NAME}
export IMAGE_TAG=${IMAGE_TAG}
export AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION}
export AWS_ACCOUNT_ID=${AWS_ACCOUNT_ID}
EOF
echo
echo "************"
