# aws :: cloudformation

Cloudformation template examples.

- CFN_ECS_Cluster_Existing_VPC.json

  Launch an EC2 Container Service cluster in a pre-created VPC of your choice,
  using a pre-created security group.  You can SSH into the cluster instances
  using the SSH key specified.  See sample parameters in `CFN_ECS_Cluster_Existing_VPC.input.json`.

  You can run it like this:

      aws cloudformation create-stack --stack-name SomeStackName --parameters `cat CFN_ECS_Cluster_Existing_VPC_input.json` --capabilities CAPABILIT_IAM

- CFN_VPC_Basic.cfn.yaml

  A cloudformation template (in YAML format instead of JSON), that creates a basic VPC using two AZs,
  a public and private subnet in each AZ, and a managed NAT gateway in the first AZ to route all
  outbound traffic from the private subnets through.

- CFN_VPC_Basic.natinst.cfn.json

  A cloudformation template that creates a similar VPC to above, but uses a EC2
  instance as a NAT instance.  Obsoleted by the above.

- cfn-print-progress.py

  Given a stack name, will poll cloudformation repeatedly until the status of
  the stack transitions to either a succeed state or a failed state.  Useful,
  because when you kick off a cloudformation command, it returns immedidately
  and the stack creation/update happens in the backgrouns, leaving us to have
  to call `cloudformation describe-stack` repeatedly to figure out whether the
  stack creation finished.

  Run it like so:

    python cfn-print-progress.py <stack-name>

  Requires `boto3` to be installed.  Picks up the region from `~/.aws/config`
  or `AWS_DEFAULT_REGION` environment variable.
