# Usage: source vars.sh (in shell or other scripts)
## Edit these to suit your project
PROJECTDIR=spring-boot-rest-greeting
MVN_ARTIFACT_ID=spring-boot-rest-greeting
IMAGE_REPO_NAME=spring-boot-rest-greeting
AWS_DEFAULT_REGION=us-east-1
AWS_ACCOUNT_ID=825739414361


## Don't edit these -- automatically computed
MVN_VERSION=$(cd ${PROJECTDIR} && \
    mvn -q -Dexec.executable='echo' \
        -Dexec.args='${project.version}' \
        --non-recursive \
        exec:exec)
JARFILE=${PROJECTDIR}/target/${MVN_ARTIFACT_ID}-${MVNVERSION}.jar
GITHASH=$(git rev-parse HEAD)
IMAGE_TAG=${MVNVERSION}-${GITHASH}
