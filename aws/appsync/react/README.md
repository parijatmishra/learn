# aws/appsync/react

Integrating AppSync into a ReactJS application.  The application allows *authors* to make *posts*.  No authentication.  Storage backend is DynamoDB.

## Creating DynamoDB tables and AppSync IAM roles

The file `backend/AmazonDynamoDBCFTemplate.yaml` is a CloudFormation template that creates a DynamoDB table and an IAM role that allows AppSync to read/write to the table.

The table has the following structure:

- `id`: String, HASH key
- `author`: String

There is also a Global Secondary Index, `author-index`, on the table:

- `author`: String, HASH key
- all attributes are projected into the GSI

To create the table, GSI and the role:

```
aws cloudformation create-stack \
    --region <your-favourite-region>
    --stack-name AWSAppSyncTutorialForAmazonDynamoDB \
    --template-body file://AmazonDynamoDBCFTemplate.yaml \
    --capabilities CAPABILITY_NAMED_IAM
```

(The command above uses the default value `AppSyncTutorial-` for the parameter `TableNamePrefix`.)

## Deploying the app
