#!/bin/bash
# Lint the template first, with dummy / made up param values
cfn-lint validate app.template.yml  --no-guess-parameters --parameters AppName="dummy",Path="/greeting",HealthCheckPath="/greeting",DesiredCount=2,ImageRepository=sbrg,ImageTag=2.0-12345,VPCID=vpc-123456,LoadBalancerListenerArn="arn:aws:elasticloadbalancing:us-east-1:825739414361:listener/app/infra-LoadB-1A2GU9B5DQEAA/9e50ef614b24c0d8/72927c16d47f7052",ECSClusterArn="arn:aws:ecs:us-east-1:825739414361:cluster/infrastructure-ECSStack-3SO9AUY66QIB-ECSCluster-81QKDWRWWND0"

