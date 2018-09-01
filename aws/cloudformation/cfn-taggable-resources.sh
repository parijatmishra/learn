#!/bin/bash
# Create a JSON file with all AWS CloudFormation resource types
# that have a Tags property.
#

# curl --silent \
#     --output CloudFormationResourceSpecification.json \
#     https://d1uauaxba7bl26.cloudfront.net/latest/gzip/CloudFormationResourceSpecification.json

cat CloudFormationResourceSpecification.json | jq --raw-output '.ResourceTypes | to_entries | map(select(.value.Properties | has("Tags"))) | .[].key' | sort > AWS_taggable_resources.txt
cat CloudFormationResourceSpecification.json | jq --raw-output '.ResourceTypes | to_entries | map(select(.value.Properties | has("Tags") | not)) | .[].key' | sort > AWS_non_taggable_resources.txt