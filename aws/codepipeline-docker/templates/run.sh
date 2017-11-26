aws cloudformation package --template-file infrastructure.template.yml --s3-bucket code-us-east-1-82573941361 --s3-prefix prod/templates/ --output-template-file packaged-infrastructure.template.json
aws cloudformation deploy --template-file /Users/parijat/workspace/learn/aws/codepipeline-docker/templates/packaged-infrastructure.template.json --stack-name infrastructure --parameter-overrides KeyPairName=general1 --capabilities CAPABILITY_IAM