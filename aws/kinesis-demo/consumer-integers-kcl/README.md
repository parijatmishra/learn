
### Configuration
Needs the following environment variables to be set:

 - APP_NAME: should be unique across all your Kinesis Client Library applications.  A dynamo DB table will be created using this name.
 - KINESIS_REGION: the AWS region where the Kinesis stream to be read lives
 - KINESIS_STREAM_NAME: the name of the Kinesis Data Stream to be read

