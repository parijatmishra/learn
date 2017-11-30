#!/usr/bin/env bash
set -evx
VARS="$(dirname $0)/_vars.sh"
if [ ! -f "${VARS}" ]; then
    exit 1
fi
source "${VARS}"
[ -z "$IMAGE_TAG" ] && (echo "Environment variable IMAGE_TAG is not set" && exit 1)

cat > output.json <<EOF
{"tag": "${IMAGE_TAG}"}
EOF
