#!/usr/bin/env bash
aws ecs register-task-definition --cli-input-json file://aws-fargate-task-definition-2.json
