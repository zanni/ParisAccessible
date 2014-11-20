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
 
service 'neo4j' do
	  action :stop
	  supports :status => true, :start => true, :stop => true, :restart => true
	end
bash "Build spatial" do
  user "root"
  cwd "/srv/spatial"
  code <<-EOH
  chmod -R 777 ./
  mvn clean package install -DskipTests=true
  mvn dependency:copy-dependencies
  rm target/*docs.jar
  mv target/*.jar #{node['neo4j']['server']['installation_dir']}/plugins
  EOH
end
service 'neo4j' do
	  action :start
	  supports :status => true, :start => true, :stop => true, :restart => true
	end