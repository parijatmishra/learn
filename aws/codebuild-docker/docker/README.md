A fairly simple Dockerfile for an executable jar.  Assumes someone will copy
the required uber jar into the current directory as `app.jar`.  Assumes
further that this `app.jar` uses/respects the property `server.port` and will
listen on the specified port.

See `../docker_build.sh`.
