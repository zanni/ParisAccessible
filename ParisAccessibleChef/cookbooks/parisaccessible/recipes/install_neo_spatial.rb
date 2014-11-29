#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#

 git "/srv/spatial" do
  user 'ubuntu'
  group 'ubuntu'
   repository "#{node['parisaccessible']['neo_spatial_repository']}"
   reference "master"
   action :sync
 end
 

bash "Build spatial" do
  user "root"
  cwd "/srv/spatial"
  code <<-EOH
  chmod -R 777 ./
  mvn package install -DskipTests=true
  EOH
end

#mvn dependency:copy-dependencies
#  rm target/*docs.jar
#  mv target/*.jar #{node['neo4j']['server']['installation_dir']}/plugins
