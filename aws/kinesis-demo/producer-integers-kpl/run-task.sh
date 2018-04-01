#!/usr/bin/env bash
aws ecs run-task --cli-input-json file://aws-fargate-run-task.json
