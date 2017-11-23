A fairly simple Dockerfile for an executable jar, and a couple of scripts to
build a docker image and push the image to Amazon Elastic Container Registry
(ECR).

The Dockerfile assumes you will copy the required uber jar into the current
directory as `app.jar`.  Assumes further that this `app.jar` uses/respects the
property `server.port` and will listen on the specified port.

Also see: `../make_dist.sh`.
