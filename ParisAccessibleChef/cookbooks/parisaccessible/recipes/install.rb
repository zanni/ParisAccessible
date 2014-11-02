#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
directory "#{node['parisaccessible']['home']}" do
  action :create
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
bash "Build Application" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  mvn clean package
  EOH
end
