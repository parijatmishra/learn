# simple-python-app

A simple Flask based web application.

## Dependencies

The application requires this other software:

 - MySQL

## Install

You need `pip` to install the dependencies.

    $ pip install -r requirements.txt

(You should be using a virtualenv, preferably.)

## Configuration

The application uses the following environment variables:

 - `SECRET_KEY`: a long, unguessable string that is used for many Flask
   crypto operations.  Should be separate for every deployment.

 - `SQLALCHEMY_DATABASE_URI`: a SQLAlchemy database URI. Optional. Default value
   (for development) is `sqlite:///data.sqlite` which creates a db file `data.sqlite`
   in the local directory.  The value for a production deployment should
   point to a MySQL database.  The application needs all permissions on
   the specified particular MySQL schema, including the ability to CREATE,
   DROP and ALTER tables.

 - Email sending related configuration: the application will send emails
   to users for registration confirmation, password resets and the like.
   It needs the following configuration information:


   - `MAIL_SUBJECT_PREFIX`: when the app sends emails (e.g., password reset,
     registration confirmation), this text will be prefixed to the actual
     subject of the email, to make it clear that the email is coming from
     this app. (Default: *Bloggy*.)

   - `MAIL_SENDER`: emails sent by the app will appear to come from this email
     address.  You must have permission to send emails from this address.
     (Default: *bloggy@example.com*.)

   - `MAIL_SERVER`: the SMTP server to connect to. (Default: `localhost`)

   - `MAIL_PORT`: the SMTP port to connect to. (Default: `25`)

   - `MAIL_USERNAME` and `MAIL_PASSWORD`: SMTP server credentials. (Default:
   `empty)


## Run

Configure the application by setting appropriate environment variables. (See
`Configuration` section.)

Stop the application if it is already running.

Create/update the database schema:

    $ python simple-python-app.py migrate

Start the application:

    $ python simple-python-app.py runserver -h 0.0.0.0

### Running with Nginx

TODO

### Using supervisord

TODO

## Development

### Unit Testing

Unit Tests are tests that don't need external software to run. The database
used is a temporary, file-based SQLite database.  You need to install some
additional dependencies to run tests.  Install them like this:

    $ pip install -r dev_requirements.txt

Then, tests can be run by:

    $ sh unittest.sh

This will run tests in the `test/unit` package, and produce a Jenkins readable
test result output at `nosetests.xml`, and a test coverage report in HTML
format at `out/test/unit/coverage/html/index.html`.

### Integration Testing

These are tests that run against real external systems that we will use in production.

Not written yet.

### Run server in development mode

Set the environment variable `SECRET_KEY` to something you don't use in
production.  Set `MAIL_SUPPRESS_SEND` to '1'.  Optionally, set the value of
`SQLALCHEMY_DATABASE_URI` to your preferred value if you don't want to use the
default development database location.  Optionally, set the other configuration values.

Create the database schema and some test data:

    $ python simple-python-app.py create_dev_database

You can start the application in debug mode, with automatic code-reloading (if
any files on disk are detected to have changed):

    $ python simple-python-app.py runserver -d -r

The application will listen on 127.0.0.1 port 5000.  The port and the listening
address can be changed by command line parameters.

