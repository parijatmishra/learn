#!/bin/bash
set -evx

. validate-app-template.sh
. validate-pipeline-template.sh

CodePipelineS3BucketName=code-us-east-1-82573941361
AppName=sbrg

aws cloudformation package --template-file pipeline.template.yml --s3-bucket ${CodePipelineS3BucketName} --s3-prefix prod/templates/${AppName}-pipeline --output-template-file out-pipeline.template.yml
aws cloudformation deploy --template-file out-pipeline.template.yml --stack-name ${AppName}-pipeline --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    CodePipelineS3BucketName=${CodePipelineS3BucketName} \
    AppName=${AppName}
