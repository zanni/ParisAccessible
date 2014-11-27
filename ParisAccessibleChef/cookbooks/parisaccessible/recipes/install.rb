#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
package "git"

directory "/srv" do
  owner 'ubuntu'
  group 'ubuntu'
  action :create
end
 git "#{node['parisaccessible']['home']}" do
  user 'ubuntu'
  group 'ubuntu'
   repository "#{node['parisaccessible']['repository']}"
   reference "master"
   action :sync
 end

directory "#{node['parisaccessible']['log']}" do
  owner 'ubuntu'
  group 'ubuntu'
  mode '0777'
  action :create
end
directory "#{node['parisaccessible']['home']}/inject" do
  mode '0777'
  action :create
end
template "#{node['parisaccessible']['home']}/parisaccessible.properties" do
  mode '0777'
  source "parisaccessible.properties.erb"
  action :create
end

include_recipe "parisaccessible::install_neo_spatial"


# if node[:parisaccessible][:compile] 
  
  bash "Build Application" do
    cwd "#{node['parisaccessible']['home']}"
    code <<-EOH
    sudo chmod -R 777 ../
    mvn clean package
    EOH
  end

# else

#   directory "/srv/bin" do
#     owner 'ubuntu'
#     group 'ubuntu'
#     mode '0777'
#     action :create
#   end
#   bash "sync bin from s3" do
#       not_if { ::File.directory?("#{node['parisaccessible']['neo4j_data_path']}")  }
#       user "root"
#       cwd "/srv/bin"
#       code <<-EOH
#         aws s3 cp s3://bzanni/bin ./
#         install:install-file -Dfile=<path-to-file> -DpomFile=<path-to-pomfile
#       EOH
#     end
# end

