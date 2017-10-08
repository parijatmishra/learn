# config.py: configuration information from environment variables/defaults
import os
basedir = os.path.abspath(os.path.dirname(__file__))
DEFAULT_SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, 'data.sqlite')
SQLALCHEMY_DATABASE_URI = os.environ.get('SQLALCHEMY_DATABASE_URI', DEFAULT_SQLALCHEMY_DATABASE_URI)
SQLALCHEMT_COMMIT_ON_TEARDOWN = True
SQLALCHEMY_TRACK_MODIFICATIONS = False
SECRET_KEY = os.environ['SECRET_KEY']

# Mail configuration
if os.environ.get('MAIL_SUPPRESS_SEND'):
	MAIL_SUPPRESS_SEND = True
MAIL_SUBJECT_PREFIX = os.environ.get('MAIL_SUBJEXT_PREFIX') or 'BLOGGY'
MAIL_SENDER = os.environ.get('MAIL_SENDER') or "bloggy@example.com"
MAIL_SERVER = os.environ.get('MAL_SERVER') or 'localhost'
MAIL_PORT = int(os.environ.get('MAIL_PORT') or '25')
MAIL_USE_TLS = True
MAIL_USERNAME = os.environ.get('MAIL_USERNAME')
MAIL_PASSWORD = os.environ.get('MAIL_PASSWORD')
