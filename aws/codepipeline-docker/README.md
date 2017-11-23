# CodePipeline pipeline for creating Docker images from a Java web application

## The application

The application is a very simple "Hello, World" application and its code
resides in the directory
[spring-boot-rest-greeting](spring-boot-rest-greeting).

### Building

Building an executable Jar is as easy as:

```
cd spring-boot-rest-greeting
mvn clean package
```

This will compile the code, run unit tests, and produce an "uberjar" in the
`target` directory, named `spring-boot-rest-greeting-<version>.jar`, where
version is the value of `project.version` in `pom.xml`.

### Running

In the code directory, you can run the program as:

```
java -jar target/spring-boot-rest-greeting-1.0.jar
```

This will start a web server listening on `0.0.0.0:8080`.  You can interact with the service like this:

```
$ curl http://localhost:8080/greeting
{"id":1,"content":"Hello, World!"} 
$ curl http://localhost:8080/greeting?name=Parijat
{"id":2,"content":"Hello, Parijat!"}
```

You can change the port on which the web server listens by passing a command line argument:

```
java -Dserver.port=9000 -jar target/spring-boot-rest-greeting-1.0.jar
```

## Docker

You can manually create a Docker image from the Jar file, like so:

```
./make_dist.sh
cd dist/docker
./docker_build.sh
```

You can then push the image to ECR (you must have the aws CLI installed and
confirured with credentials beforehand; the credentials should belong to a AWS
user with permissions to push images to ECR):

```
./ecr_push.sh
```

## Pipeline

The above steps can be automated via a Pipeline.  You need to first ensure you
have an existing S3 Bucket in the same region as the pipeline, in which
CodePipeline will store intermediate artifacts.   (If you have used
CodePipeline from the UI before, you would have a bucket named
`codepipeline-<region>-<random-string>` that already exists, and can be used
for this purpose).  Then you can create the pipeline using the provided
CloudFormation template like so:

```
aws cloudformation create-stack --stack-name <stack-name> --template-body file://codepipeline/project.cfn.yml --parameters ParameterKey=CodePipelineS3BucketName,ParameterValue=<bucketname> --capabilities CAPABILITY_NAMED_IAM
```

Once the pipeline is created, get the value of the CodeCommit repository URL by examining the stack output:

```
aws cloudformation describe-stacks --stack-name <stack-name>
```

and looking for the value of `CloneUrlHttp`.  Then clone this git repository to a local folder.  For e.g.:

```
git clone https://git-codecommit.us-east-1.amazonaws.com/v1/repos/<stack-name> <local-dir>
```

Copy the contents of this folder (the folder that contains this README) into the just cloned repository.  Commit and push the contents.

```
cp -a $PROJECT_DIR/* <local-dir>
cd <local-repo>
git add -A .
git commit -m "First commit"
git push
```

Now your pipeline should run in a few moments (you can visit the
`PipelineConsoleUrl` to see it in action), and after some time, a new Docker
image should be pushed to an ECR repository with the same name as the name of
the stack (i.e., `<stack-name>`).
