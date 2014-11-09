#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
package "git"

 git "#{node['parisaccessible']['home']}" do
   repository "#{node['parisaccessible']['repository']}"
   reference "master"
   action :sync
 end

 git "/srv/Jest" do
   repository "#{node['parisaccessible']['jest_repository']}"
   reference "master"
   action :sync
 end

 git "/srv/spatial" do
   repository "#{node['parisaccessible']['neo_spatial_repository']}"
   reference "master"
   action :sync
 end

directory "#{node['parisaccessible']['log']}" do
  action :create
end
directory "#{node['parisaccessible']['home']}/inject" do
  action :create
end
template "#{node['parisaccessible']['home']}/parisaccessible.properties" do
  source "parisaccessible.properties.erb"
  action :create
end

bash "Build Jest" do
  user "root"
  cwd "/srv/Jest"
  code <<-EOH
  chmod -R 777 ./
  mvn clean package install -DskipTests=true
  EOH
end

bash "Build spatial" do
  user "root"
  cwd "/srv/spatial"
  code <<-EOH
  chmod -R 777 ./
  mvn clean package install -DskipTests=true
  EOH
end

bash "Build Application" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  mvn clean package
  EOH
end
