#!/bin/bash
AppName=sbrg

# Get the output of the main stack as a JSON dict of name-value pairs
# i.e.:
# 
# [
#   {
#     "Description": "Name of the Auto-Scaling Group used to create the ECS cluster.",
#     "ExportName": "infrastructure-ECSAutoScalingGroupName",
#     "OutputKey": "ECSAutoScalingGroupName",
#     "OutputValue": "infrastructure-ECSStack-3SO9AUY66QIB-ECSAutoScalingGroup-LBAWQ8DUI8OO"
#   },
#   {
#     "Description": "Name of the Auto-Scaling Launch Configuration used by the ECS cluster auto-scaling group.",
#     "ExportName": "infrastructure-ECSLaunchConfigurationName",
#     "OutputKey": "ECSLaunchConfigurationName",
#     "OutputValue": "infrastructure-ECSStack-3SO9AUY66QIB-ECSLaunchConfiguration-UXGAFDCXUSVP"
#   }
# ]
# 
# becomes
#
# {
#    "ECSAutoScalingGroupName": "infrastructure-ECSStack-3SO9AUY66QIB-ECSAutoScalingGroup-LBAWQ8DUI8OO",
#    "ECSLaunchConfigurationName": "infrastructure-ECSStack-3SO9AUY66QIB-ECSLaunchConfiguration-UXGAFDCXUSVP"
# }
INFOUT=$(aws cloudformation describe-stacks --stack-name infrastructure | jq '.Stacks[0].Outputs | map({(.OutputKey|tostring): .OutputValue}) | add')
# Convert the JSON into shell variables
for i in VPCID LoadBalancerListenerArn ECSClusterArn; do
	declare "${i}"=$(echo $INFOUT | jq --raw-output ".$i")
done


cat > app.configuration.json <<EOF
{
    "Parameters": {
        "AppName": "${AppName}"
        "Path": "/greeting",
        "HealthCheckPath": "/greeting",
        "DesiredCount": "2",
        "ImageRepository": "${AppName}",
        "ImageTag": "TO-BE-OVERRIDDEN-IN-PIPELINE",
        "VPCID": "${VPCID}",
        "LoadBalancerListenerArn": "${LoadBalancerListenerArn}",
        "ECSClusterArn": "${ECSClusterArn}"
    }
}
EOF
