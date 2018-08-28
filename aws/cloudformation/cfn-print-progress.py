#!/usr/bin/env python
import boto3
from botocore.exceptions import ClientError
import sys
import time

stack_name=sys.argv[1]
client = boto3.client('cloudformation')

def get_status(stack_name):
    response = client.describe_stacks(StackName=stack_name)
    stacks = response['Stacks']
    stack = stacks[0]
    return stack['StackStatus']

def stack_events(stack_name):
    response = client.describe_stack_events(StackName=stack_name)
    all_events = response['StackEvents']
    resource_events = {}
    for event in all_events:
        rsc = event['LogicalResourceId']
        status = event['ResourceStatus']
        ts = event['Timestamp']
        reason = event.get('ResourceStatusReason')

        exists = resource_events.get(rsc)
        if not exists:
            resource_events[rsc] = {
                'LogicalResourceId': rsc,
                'ResourceStatus': status,
                'ResourceStatusReason': reason,
                'Timestamp': ts
            }
            continue
        else:
            old_ts = exists['Timestamp']
            if old_ts < ts:
                exists['ResourceStatus'] = status
                exists['Timestamp'] = ts
                exists['ResourceStatusReason'] = reason
                resource_events[rsc] = exists

    # print in chronological order
    lst = []
    for k, v in resource_events.items():
        lst.append(v)
    lst = sorted(lst, key=lambda k: k['Timestamp'])
    max_LogicalResourceId_len = max(len(i['LogicalResourceId']) for i in lst)
    max_ResourceStatus_len = max(len(i['ResourceStatus']) for i in lst)
    max_ResourceStatusReason_len = max(len(i['ResourceStatusReason'] or '') for i in lst)

    format = "%%%ds %%-%ds %%-%ds %%-%ds" % (33, max_LogicalResourceId_len, max_ResourceStatus_len, max_ResourceStatusReason_len)
    for i in lst:
        print(format % (i['Timestamp'], i['LogicalResourceId'], i['ResourceStatus'], i['ResourceStatusReason']))


HALT_STATES=['CREATE_FAILED', 'CREATE_COMPLETE', 'ROLLBACK_FAILED',
            'ROLLBACK_COMPLETE', 'DELETE_FAILED', 'DELETE_COMPLETE', 
            'UPDATE_COMPLETE', 'UPDATE_ROLLBACK_FAILED',
            'UPDATE_ROLLBACK_COMPLETE']
PROGRESS_STATES=['CREATE_IN_PROGRESS', 'ROLLBACK_IN_PROGRESS',
            'DELETE_IN_PROGRESS', 'UPDATE_IN_PROGRESS',
            'UPDATE_COMPLETE_CLEANUP_IN_PROGRESS',
            'UPDATE_ROLLBACK_IN_PROGRESS',
            'UPDATE_ROLLBACK_COMPLETE_CLEANUP_IN_PROGRESS']


try:
    while True:
        status=get_status(stack_name)
        print(status)
        print("===================================")
        try:
            stack_events(stack_name)
        except botocore.exceptions.ClientError:
            print("Stack does not exist.")
            sys.exit(0)

        if status is None or status in HALT_STATES:
            break
        print("")
        print("Sleeping...")
        print("")

        time.sleep(10)
except ClientError as e:
    if ("Stack with id " + stack_name + " does not exist") in str(e):
        print("Stack does not exist.")
        sys.exit(0)
    else:
        raise e

