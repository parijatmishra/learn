#!/usr/bin/env python
#
# Demonstrates the use of the Count parameter in the query API
#
import sys
import boto3
import time
from botocore.exceptions import ClientError

TABLE_NAME = 'products'

def create_table(client):
    print "Creating [" + TABLE_NAME + "]"
    response = client.create_table(
        TableName=TABLE_NAME,
        KeySchema=[
            {
            'AttributeName': 'ProductName',
            'KeyType': 'HASH'
            }
        ],
        AttributeDefinitions=[
            {
                'AttributeName': 'ProductName',
                'AttributeType': 'S'
            }
        ],
        ProvisionedThroughput={
            'ReadCapacityUnits': 5,
            'WriteCapacityUnits': 5
        }
    )
    waiter = client.get_waiter('table_exists')
    waiter.wait(TableName=TABLE_NAME)

def insert_items(client):
    items = [
        ('Orange', '100', str(int(time.time() * 100))),
        ('Apple', '120', str(int(time.time() * 100)))
    ]
    for item in items:
        print "Inserting into [" + TABLE_NAME + "]: " + item[0]
        client.put_item(
            TableName=TABLE_NAME,
            Item={
                'ProductName': {'S': item[0]},
                'PriceCents': {'N': item[1]},
                'AvailableSince': {'N': item[2]}
            },
            ReturnValues='NONE'
        )

def query_count(client):
    print "Query records where ProductName = 'Orange' AND AvailableSince > 300 seconds ago. Fetch COUNT of matching records."
    # get items with ProductName set to Orange
    # but only if the product was available since > 300 secs
    # note that we are storing timestamps in milliseconds in the table
    # whereas time.time() returns a float value in seconds
    fromtime = str(int((time.time() - 300.0) * 100))
    response = client.query(
        TableName=TABLE_NAME,
        Select='COUNT',
        KeyConditionExpression="ProductName = :name",
        FilterExpression='AvailableSince > :fromtime',
        ExpressionAttributeValues={
            ':fromtime': {'N': fromtime},
            ':name': {'S': 'Orange'}
        }
    )
    return response

def query(client):
    print "Query records where ProductName = 'Orange' AND AvailableSince > 300 seconds ago. Fetch ALL_ATTRIUTES."
    # get items with ProductName set to Orange
    # but only if the product was available since > 300 secs
    # note that we are storing timestamps in milliseconds in the table
    # whereas time.time() returns a float value in seconds
    fromtime = str(int((time.time() - 300.0) * 100))
    response = client.query(
        TableName=TABLE_NAME,
        Select='ALL_ATTRIBUTES',
        KeyConditionExpression="ProductName = :name",
        FilterExpression='AvailableSince > :fromtime',
        ExpressionAttributeValues={
            ':fromtime': {'N': fromtime},
            ':name': {'S': 'Orange'}
        }
    )
    return response

def delete_table(client):
    print "Deleting [" + TABLE_NAME + "]"
    try:
        response = client.delete_table(TableName=TABLE_NAME)
    except ClientError, e:
        pass # table does not exist
    waiter = client.get_waiter('table_not_exists')
    waiter.wait(TableName=TABLE_NAME)

def pretty_print(obj):
    import json
    print json.dumps(obj, indent=4, separators=(',', ": "))

def main():
    client = boto3.client('dynamodb')
    delete_table(client)
    create_table(client)
    insert_items(client)
    pretty_print(query_count(client))
    pretty_print(query(client))
    delete_table(client)

if __name__ == "__main__":
    main()
