# AWS SAM - Lambda Simple
A simple Hello World serverless application.

The application consists of a sinlge lambda function writting in JS, using the
NodeJS 6.10 runtime.  The function consumes an event and return a response.

The event should be a dict with one key named "name".  It's value should be a
string.  The response is a string: "Hello " + `event.name`.  See
[event.json](event.json) for an example.

# Test Locally
You need the [`aws-sam-local`](https://github.com/awslabs/aws-sam-local) tool.

Invoke function using sample event from file:

    sam invoke local "Hello" -e event.json

Invoke function with event via `stdin`:

    echo '{"name": "Bob"}' | sam local invoke "Hello"

# Deploy to AWS Lambda

    aws cloudformation package \
      --s3-bucket <bucket-to-upload-lambda-code-to> \
      --s3-prefix <folder-within-bucket> \
      --template-file template.yml \
      --output-template-file template-out.yml

    aws cloudformation deploy --template-file template-out.yml
