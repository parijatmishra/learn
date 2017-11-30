#!/bin/bash
set -evx
cfn-lint validate infrastructure.template.yml --parameters KeyPairName=general1 --no-guess-parameters

aws cloudformation package --template-file infrastructure.template.yml --s3-bucket code-us-east-1-82573941361 --s3-prefix prod/templates/ --output-template-file out-infrastructure.template.yml
aws cloudformation deploy --template-file out-infrastructure.template.yml --stack-name infrastructure --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    KeyPairName=general1 
