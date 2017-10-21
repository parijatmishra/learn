# SAM - Lambda Api GW example

A serverless application where an API Gateway API calls a Lambda function and
returns its response.

# Test Locally
You need the [`aws-sam-local`](https://github.com/awslabs/aws-sam-local) tool.

Start a local Api Gateway:

    sam invoke start-api

This will start an HTTP server listening on [http://localhost:3000/]

Invoke it with:
    
    http://localhost:3000/
    http://localhost:3000/?name=Parijat

# Deploy to AWS Lambda

    aws cloudformation package \
      --s3-bucket <bucket-to-upload-lambda-code-to> \
      --s3-prefix <folder-within-bucket> \
      --template-file template.yml \
      --output-template-file template-out.yml

    aws cloudformation deploy --template-file template-out.yml --stack-name <your-preferred-stack-name>
