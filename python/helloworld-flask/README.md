# helloworld-flask
- A very simple Hello, World! python web app using Flask.  It does not use any external services.
- A Dockerfile to create docker container out of it.
- [Amazon ECS](https://aws.amazon.com/ecs/) [Task Definition](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definitions.html) file

# Running locally

Create a virtualenv, and install the dependencies:

    $ pip install -r requirements.txt

Run the app:

    $ python application.py

If you visit <http://localhost:8000/>, you should see a "Hello, World!"
message.

You can change the greeting:  visit <http://localhost:8000/Tom> to see the web
page greet Tom.

The port the app listens on can be changed using the "PORT" environment
variable:


    $ export PORT=5000
    $ python application.py

Now the application will listen on port `5000` instead. Visit
<http://localhost:5000> to see this in action.

# Docker
## Building a Docker image

Building an image: decide on the name of the image family and the version tag..
I use "flaskhello" as the image family name and "latest" as the version tag.

    $ docker build -t flaskhello:latest .

Running it: the Dockerfile sets the app to listen on port `8000` by default.
To get docker to map the app port to port 5000 on your Docker server:

    $ docker run -d --rm -p 5000:8000 flaskhello:latest

Visit <http://your-docker-ip:5000> to see this in action.

## Docker registry

I've pushed this image to Docker Hub as parijatmishra/flaskhello:latest.

# Running on Amazon ECS

The file [ecs-task-definition.json] contains an Amazon ECS task definition.  If
you have an ECS cluster ready, you can deploy it like so:

    aws ecs register-task-definition --cli-input-json file://ecs-task-definition.json

Note the value of the key `revision` in the returned output.  You will use
this, and the `family`, to run tasks or create services.


