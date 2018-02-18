# CodePipeline pipeline for creating Docker images from a Java web application and deploying to ECS Fargate as a highly-available service

## Application

The application is a very simple "Hello, World" application and its code
resides in the directory [app](app).  See the file
[app/README.md](app/README.md) for instructions on how to build, run and test
it.

## Building and pushing the Docker image to ECR, manually

Before creating a Docker image, choose a "name" for the image.  The same name
will be used for the name of the ECR repository to which the image will be
pushed, as well as in the local image repository.  Let's call this
*IMAGE_REPO_NAME*.

You can manually create a Docker image from the Jar file, like so:

```
./make_dist.sh IMAGE_REPO_NAME
```

This command builds the app using `maven`, creating an *uberjar* in `target/`.
It then creates a `dist` folder, and copies the uberjar and some other files
over.  It also creates a file `vars.sh` in the `dist` folder that contains some
environment variables that are used by subsequent shell scripts (mainly, the
ECR repository name, and a tag for the Docker image to be created which is
derived from the maven project version and the git hash of the source code).

Now, you can build a Docker image with an appropriate tag.

```
cd dist/docker
./docker_build.sh
```

You can then push the image to ECR (you must have the AWS CLI installed and
configured with credentials beforehand; the credentials should belong to a AWS
user with permissions to push images to ECR):

```
./ecr_push.sh
```

## CI/CD pipeline with CodePipeline

CloudFormation templates are provided to setup a basic CI system that will
build the app, make a Docker image, push the image to an Amazon Elastic
Container Registry (ECR) repository, and then deploy a running service from
that image to an ECS cluster.

The pipeline requires a running ECS Cluster.

To deploy the CloudFormation templates, you also need an existing S3 bucket
where the CloudFormation assets will be temporarily bundled and stored.

### Template Linting

Some `validate-*.sh` scripts are provided, and used by the `*-setup.sh` scripts.  They use the [cfn-lint](https://www.npmjs.com/package/cfn-lint) program.  You can install this program via [npm](https://www.npmjs.com/).

    $ npm install -g cfn-lint

If you don't want to do the use cfn-lint, comment out the calls to `validate-*`
scripts in the various `*-setup.sh` scripts.

### Infrastructure

CloudFormation templates are provided to create a VPC, an Application Load
Balancer, and a ECS Cluster.  They are all present in the `templates` directory
and are named `infrastructure.*`.  The main template,
`infrastructure.template.yml` orchestrates the other templates.

To create the cluster, go into the `templates` directory, view/edit the file
`infrastructure-setup.sh`, and edit the "--s3-bucket" and the "--s3-prefix"
parameters.

Optionally, view/edit the various `infrastructure*.yml` files.
You may want to tweak the ECS cluster instance type and number.  If you edit them, run `validate-infrastructure-templates.sh` to check for some common errors.  You need the 

Deploy the cluster like this:

    $ ./infrastructure-setup.sh

### CodePipeline pipeline

Optionally, view/edit the `templates/pipeline.template.yml` and
`app.template.yml` files.  Remember to run the `validate-pipeline-templates.sh`
to do basic checks.

You can create the pipeline like this:

    $ ./pipeline-setup.sh

### Triggering the pipeline

The CloudFormAtion stack for the pipeline creates a CodeCommit repository, with
the name `$AppName` as specified in the `pipeline-setup.sh` script.  We must
check this repo out, copy the contents of this directory (the one containing
this README; we will refer to it as `$PROJECT_DIR`) into it, and commit it.

First, find the value of `CloneHttpUrl` from the output of the CloudFormation stack for the pipeline.  Then:

    $ mkdir tmp
    $ cd tmp/
    $ git clone $CloneHttpUrl
    $ cd  $AppName
    $ cp -a $PROJECT_DIR/* .

CodePipeline itself deploys the app using a CloudFormation template, as well.  The template needs to be told where the ECS Repository to pull the Docker images from is, and some details of infrastructure.  This step only needs to be done once.  You can generate all these details and put them in a JSON file to be used later by the pipeline, like so:

    $ vi templates/gen-app-cofniguration-json.sh # Check/edit AppName and the arg to --stack-name option
    $ cd templates; ./gen-app-configuration.json.sh ; cd ..  # This will generate an app.configuration.json file
    $ git add -A .  && git commit -m "Some message" && git push

In a few moments, the pipeline should start running.  (The pipeline stack's
`PipelineConsoleUrl` output gives a link to the UI.)   When it is done, you will find the service running at:

    $ http://${LoadBalancerDNSName}/greeting

where LoadBalancerDNSName is found in the output of the `infrastructure` stack.

Now you can continue to edit your application code or other things, and every
time you do a `git push`, the pipeline should run and if the build passes,
deploy the updated Docker container to your ECS cluster.

### Troubleshooting

- cfn-lint should catch most syntax errors in the CloudFormation templates.
  Use the `validate-*.sh` scripts librarlly. Currently it does not handle
  constructs like 'Fn::Select', 'Fn::And', 'Fn::Or' etc.  If you are using
  those, especially with conditions, `cfn-lint` may misinterpret your template
  and give false errors.

- If you modify the templates so that it creates more or different resources
  (e.g., by adding new actions or stages to the pipeline, or by changing the
  locations of the log groups, etc.), the stack updates may fail due to
  insufficient IAM permissions.  The IAM permissions in the various roles are
  delibreately as narrow as possible.  You should examine the error and add
  more permissions as required.  The following roles are important:

  In `pipeline.template.yml`:

  + `CfnIAMRole`. This is given to CloudFormation when deploying the app, and
    allows it to create the resources required in `app.template.yml`.  If you
    add more resources there, this role may need additional permissions.  The
    trickies bit is the `iam:PassRole` permission, which is used by
    CloudFormation to pass a role to the ECS task (the ECS TaskRole).  If
    tasks, services, or other resources need more roles or different roles,
    then this needs to be modified.

  Other important roles are:

  + `CodePipelineIAMRole`: used by the pipeline itself.  May need modifications
    if you add more actions or stages.
  + `CodeBuildIAMRole`: used by the CodeBuild projects.  May need modifications
    if your build projects utilize more AWS services.
  + `CloudWatchLogsPolicy`. This is shared by other roles.  May need
    modifications if you change CodeBuild project names, or the log group name
    for the ECS tasks.

  In `app.template.yml`:

  + `TaskRole`: permission given to our ECS task for calling other AWS services
    (currently none)
  + `ECSService.Properties.Role`: used `ecsServiceRole`; this is a global role
    generated during ECS first run and common to all ECS clusters and services.
    If the template will be run in an AWS account or region, where ECS has not
    been used, then this may require replacement.
  
  In `infrastructure-ecs.template.yml`:

  + `ECSLaunchConfiguration.Properties.IamInstanceProle`: uses
    `ecsInstanceRole`.  Another global role generated during ECS first run.
    May require replacement if the template is to be run in an AWS account or
    region where ECS has never been run via the AWS Console before.

### Logs

The build steps send their logs to CloudWatch Logs, by default into the Log
Group `/aws/codebuild/${AppNmae}-*`.

When the service is running, it sends logs to Log Group `/ecs/${AppName}`.
