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

 git "/srv/Jest" do
  user 'ubuntu'
  group 'ubuntu'
   repository "#{node['parisaccessible']['jest_repository']}"
   reference "master"
   action :sync
 end

 git "/srv/spatial" do
  user 'ubuntu'
  group 'ubuntu'
   repository "#{node['parisaccessible']['neo_spatial_repository']}"
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


# if node[:parisaccessible][:compile] 
  # bash "Build Jest" do
  #   path ["/usr/local/maven/bin"]
  #   cwd "/srv/Jest"
  #   code <<-EOH
  #   sudo chmod -R 777 ./
  #    mvn clean package install -DskipTests=true
  #   EOH
  # end

  # bash "Build spatial" do
  #   cwd "/srv/spatial"
  #   code <<-EOH
  #   sudo chmod -R 777 ./
  #   mvn clean package install -DskipTests=true
  #   EOH
  # end

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

