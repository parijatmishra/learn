# codebuild-docker

Demo of how to build a Docker image of a Java web service, and push it to an
Amazon EC2 Container Registry, using AWS CodeBuild.

Contents:
  - [spring-boot-rest-greeting/]: A Java project with a Apache Maven build
    definition (`pom.xml`).
  - [codebuild/]: AWS CodeBuild Build Specs, CodeBuild Project definitions, and
    IAM polocies.
  - [make_dist.sh]: step 1 of the build process (first CodeBuild project).
    Requires `maven` and `git`.  Builds the Java project using maven.
    Generated an image tag using maven project version and git commit hash.
    Sets up everything required to build Docker image and push to ECR
    repository in a generated `dist/` folder.
  - [docker/Dockerfile]]: the Dockerfile for creating the Docker image
  - [docker/docker_build.sh] and [docker/ecr_push.sh]: used in step 2 of the
    the build process (second CodeBuild project).  Creates a docker image and
    tags it with a version computed from the maven project version and the git
    commit hash. Pushes the docker image to EC2 Container repository.
    
## Building manually

Edit the variables at the top of the file `make_dist.sh`.  Then build JAR and Docker image with:

    $ make_dist.sh
    $ cd dist/
    $ ./docker/docker_build.sh

Optionally, push the Docker image to ECR:

    $ ./docker/ecr_push.sh

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

## Building using CodeBuild

To build the project, create a Docker image, and push the image to ECR, we will need a few resources.  All of them should be in the same AWS region.

1. A CodeCommit repository: create a code commit repository, and note down it's
   name.
1. An S3 bucket, where artifacts after doing a maven build will be stored.
1. An ECR repository, where we will push the Docker image.
1. An IAM role, that will allow CodeBuild read access to our CodeCommit
   repository, read/write to the S3 bucket to upload/download build artifacts,
   and write access to the ECR repository.
1. First CodeBuild project, to do a maven build and create an uberjar.
1. Second CodeBuild project, that will create a Docker image from the uberjar
   and push the image to ECR.

### Create a CodeCommit repository

Using the AWS console, create a CodeCommit repository.  Follow the instructions
to setup your git credentials.  Clone the repository somewhere.  We will assume
it is called `spring-boot-rest-greeting-codebuild`.

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

#### Option 1: Using the Console

Edit the file `codebuild/iam-service-policy.json` and substitute your AWS
account ID, the AWS region you are using, the AWS CodeBuild project names you
chose (in the policy they referenced in CloudWatch Logs permissions as
`log-group:/aws/codebuild/<project-name-prefix-*>`), the S3 bucket name, and
the ECR repository name you chose.

Using the AWS console, create an IAM role with "AWS Service -> CodeBuild" as
the *trusted entity*.  We will assume the name of the role is
`codebuild-spring-boot-rest-greeting-role`.  In the *Create Policy* step,
choose "Create Your Own Policy" and insert the contents of
`codebuild/iam-service-policy.json` into the *Policy Document* field.

#### Option 2: Using the CLI

```
$ aws iam create-role --role-name codebuild-spring-boot-rest-greeting-role --assume-role-policy-document file://codebuild/iam-trust-policy.json
$ aws iam put-role-policy --role-name codebuild-spring-boot-rest-greeting-role --policy-name spring-boot-rest-greeting-policy --policy-document file://codebuild/iam-service-policy.json
```

### Update CodeBuild project files

Edit `codebuild/project_*.json` files, and update them with your AWS account
id, AWS region, CodeCommit repository name, S3 bucket name, ECR repository
name, and IAM role name.

### Create CodeBuild project to build the project using maven

1. Create the "mavenize" CodeBuild project:

```
aws codebuild create-project --cli-input-json file://codebuild/project_mavenize.json
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

1.  You can see the progress of the build in the CodeBuild console.

1. When the build finishes, visit the S3 bucket you created above.  You should
   have a folder in it named `codebuild/spring-boot-rest-greeting` containing a
   `package.zip` object.

### Create CodeBuild project to build the Docker image and push to ECR

1. Create the "dockerize" CodeBuild project:

```
aws codebuild create-project --cli-input-json file://codebuild/project_dockerize.json
```

2. Start the build:
```
aws codebuild start-build --project-name spring-boot-rest-greeting-dockerize
```

If you changed the name of the project in `codebuild_dockerize.json`, then adapt the line above accordingly.

1. You can see the progress of the build in the CodeBuild console.

1.  When the build finishes, visit your ECR repository.  You should see a new image there.
