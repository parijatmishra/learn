#!/bin/bash
set -evx

cfn-lint validate infrastructure.template.yml  --no-guess-parameters --parameters KeyPairName="dummy"
## Currently, cfn-lint can't handle all the Fn:And etc in the VPC template
## cfn-lint validate infrastructure-vpc.template.json  --parameters KeyPairName="dummy",NumberOfAzs=2,AvailabilityZones="us-east-1a,us-east-1b"
cfn-lint validate infrastructure-alb.template.yml 
cfn-lint validate infrastructure-ecs.template.yml

