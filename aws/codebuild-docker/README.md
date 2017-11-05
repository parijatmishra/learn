# codebuild-docker

Demo of how to build a Docker image of a Java web service, and push it to an
Amazon EC2 Container Registry, using AWS CodeBuild.

Contents:
  - [spring-boot-rest-greeting/]: application code.
  - [docker/Dockerfile]]: the Dockerfile for creating the Docker image
  - [codebuild/]: AWS CodeBuild build-spec and project.

## Building manually
### Building the service

The service is a spring boot based REST API that can be packaged as an single jar file like this:

    $ cd spring-boot-rest-greeting
    $ mvn package # produces target/spring-boot-rest-greeting-<version>.jar
    
### Building a Docker image

Edit `docker/vars.sh`.  You may want to change `IMAGE_REPO_NAME` although the
default will work just fine.

The following command will build the image and tag it:

    $ docker/docker_build.sh

Note: the docker tag's version is composed of two strings:
  - the maven project version ('project.version') specified in the
    java app's `pom.xml`.  This version is specified and managed
    manually by programmers, and usually follows semantic versioning
    conventions.
  - the git hash of the current HEAD.

This way of versioning means:
  - From the image tag, You can tell which exactly which git commit was used
    to build it;
  - Semantic versioning information specified by the programmer is also
    preserved;
  - Even if the programmer does not change `project.version`, but the code in
    git has changed, the Docker image revision *will* be different;
  - The downside is that if you have the source code and want to know what
    Docker image tag was constructed from a particular git commit, you need to
    also inspect the `pom.xml` in that commit version and inspect the
    `project.version` property's value.  But this extra effort it worth the
    readability of the Docker image version.

### Running the image

The Docker image exposes the port 8000 on which the Java service listens.  You can bind it to an arbitrary port on the host machine.

Start the container with something like this, binding the container port 8000 to host port 9000:

    $ docker run --rm -it -p 9000:8000 spring-boot-rest-greeting:<version>
    ....
    2017-11-05 07:06:13.716  INFO 1 ... Tomcat started on port(s): 8000 (http)
    2017-11-05 07:06:13.722  INFO 1 ... Started GreetingApplication ...

Note that inside the container, the app thinks it is binding to port 8000.
However, outside the container, on the host, that is mapped to the port 9000.

You can interact with the service like so:

    $ curl -S http://localhost:9000/greeting
    {"id":1, "content":"Hello, World!"}


    $ curl -S http://localhost:9000/greeting?name=Stranger
    {"id":2, "content":"Hello, Stranger!"}

### Pushing the Docker image to ECR

1. Create an ECR repository, in the ECR registry of your choice of AWS region.
   The name of the repository should be the same as `IMAGE_REPO_NAME` in
   `docker/vars.sh` that you set above.

1. Edit `docker/var.sh`.  Change `AWS_DEFAULT_REGION` to the AWS Region where
   you created your ECR repository, `AWS_ACCOUNT_ID` to the ID of the AWS
   account you are using.

1. Run:

```
docker/ecr_push.sh
```

## Building using CodeBuild

To build the project, create a Docker image, and push the image to ECR, we will need a few resources.  All of them should be in the same AWS region.

1. A CodeCommit repository: create a code commit repository, and note down it's name.
1. An S3 bucket, where artifacts after doing a maven build will be stored.
1. An ECR repository, where we will push the Docker image.
1. An IAM role, that will allow CodeBuild read access to our CodeCommit repository, read/write to the S3 bucket to upload/download build artifacts, and write access to the ECR repository.
1. A CodeBuild project, to do a maven build and create an uberjar.
1. Another CodeBuild project, that will create a Docker image from the uberjar

### Create the CodeCommit repository

Using the AWS console, create a CodeCommit repository.  Follow the instructions
to setup your git credentials.  Clone the repository somewhere.  We will assume
it is called `spring-boot-rest-greeting-codebuild`.  Change the name in the
instructions below to suit your own repository name.

### Create a S3 Bucket

Using the AWS console, create a S3 bucket.  We will assume it is called
`code-us-east-1-<your-account-id>`.

### Create an ECR repository

Using the AWS console, create an ECR repository.  Note its name.  We will
assume it is called `spring-boot-rest-greeting`.

### Create an IAM role, and attach a policy to it

Decide what you are going to name your two CodeBuild projects.  We will call
ours `spring-boot-rest-greeting-mavenize` and
`spring-boot-rest-greeting-dockerize`.

Using the AWS console, create an IAM role.  We will assume the name of the role
is `codebuild-spring-boot-rest-greeting-role`.  Edit the file
`codebuild/iam_policy.json` and substitute your AWS account ID, the AWS region
you are using, the AWS CodeBuild project names you chose (in the policy they
referenced in CloudWatch Logs permissions as
`log-group:/aws/codebuild/<project-name-prefix-*>`), the S3 bucket name, and
the ECR repository name you chose.  Create a new IAM policy and insert this
JSON into the (adjust resource names to adopt to your names):


### Create CodeBuild project to build the project using maven

1. Edit the file `codebuild/codebuild_dockerize.json`.  You likely need to set
   the `source.location`, `artifacts.location`, and `serviceRole` attributes.

1. Create the "mavenize" CodeBuild project:

```
cd codebuild/
aws codebuild create-project --cli-input-json file://codebuild_mavenize.json
```

1. In your checked out CodeCommit repository, copy over all files from this folder.  Then:

```
git add -A .
git commit -m "Committing project files"
git push
```

1. Start the build:

```
aws codebuild start-build --project-name spring-boot-rest-greeting-mavenize
```

If you changed the name of the project in `codebuild_mavenize.json`, then adapt the line above accordingly.


