#!/bin/bash
cfn-lint validate pipeline.template.yml --no-guess-parameters --parameters CodePipelineS3BucketName=abcd,AppName=abcd
