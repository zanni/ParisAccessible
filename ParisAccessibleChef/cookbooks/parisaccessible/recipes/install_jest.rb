#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#

 git "/srv/Jest" do
  user 'ubuntu'
  group 'ubuntu'
   repository "#{node['parisaccessible']['jest_repository']}"
   reference "master"
   action :sync
 end

 bash "Build Jest" do
    path ["/usr/local/maven/bin"]
    cwd "/srv/Jest"
    code <<-EOH
    sudo chmod -R 777 ./
     mvn clean package install -DskipTests=true
    EOH
  end