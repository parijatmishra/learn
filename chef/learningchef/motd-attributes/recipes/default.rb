#
# Cookbook Name:: motd-attributes
# Recipe:: default
#
# Copyright (c) 2015 The Authors, All Rights Reserved.
require 'pp'

node.default['ipaddress'] = '1.1.1.1'
pp node.debug_value('ipaddress')

node.default['motd-attributes']['company'] = 'Foo Corp'
node.default['motd-attributes']['message'] = "It's a wonderful day!"

include_recipe 'motd-attributes::message'

template "/etc/motd" do
  source 'motd.erb'
  mode '0644'
end

