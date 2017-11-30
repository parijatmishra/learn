#!/bin/bash
set -evx

. validate-app-template.sh
. validate-pipeline-template.sh

CodePipelineS3BucketName=code-us-east-1-82573941361
CodeCommitRepositoryName=sbrg
ECRRepositoryName=sbrg

aws cloudformation package --template-file pipeline.template.yml --s3-bucket code-us-east-1-82573941361 --s3-prefix prod/templates --output-template-file out-pipeline.template.yml
aws cloudformation deploy --template-file out-pipeline.template.yml --stack-name sbrg-pipeline --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    CodePipelineS3BucketName=${CodePipelineS3BucketName} \
    CodeCommitRepositoryName=${CodeCommitRepositoryName} \
    ECRRepositoryName=${ECRRepositoryName} 
