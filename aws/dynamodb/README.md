# aws :: dynamodb

Some experiments with DynamoDB.

## py-ddb-query-simple.py

A Python program to create a DynamoDB table, insert a couple of items, and
retrive them in various ways.  Shows different ways of specifying parameters in
queries, and how to COUNT rows.  Also shows the *waiters* functionality, which
allows the code to wait until the table status changes.

Requires `boto3` library to be installed.


Usage:

    $ mkvirtualenv venv
    $ pip install boto3
    $ python py-ddb-query-simple.py
