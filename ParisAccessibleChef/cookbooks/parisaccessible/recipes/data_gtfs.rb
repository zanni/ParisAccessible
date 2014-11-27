#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
package "unzip" do
  action :install
end

directory "#{node['parisaccessible']['home']}/inject" do
  action :create
end

directory "#{node['parisaccessible']['home']}/inject/gtfs" do
  action :create
end

remote_file "#{node['parisaccessible']['home']}/inject/gtfs/ratp_gtfs.zip" do
  source "#{node['parisaccessible']['ratp_gtfs_url']}"
  not_if { ::File.exists?("#{node['parisaccessible']['home']}/inject/gtfs/.done") }
end

cookbook_file "split_file.sh" do
  path "#{node['parisaccessible']['home']}/inject/gtfs/split_file.sh"
  action :create_if_missing
end

bash "unzip ratp_gtfs.zip" do
  user "root"
  cwd "#{node['parisaccessible']['home']}/inject/gtfs"
  code <<-EOH
  unzip ratp_gtfs.zip
  rm -rf ratp_gtfs.zip
   chmod +x split_file.sh
  chmod -R 777 ./
  EOH
end

if node[:parisaccessible][:injecting]
  bash "split ratp_gtfs.zip" do
    user "root"
    cwd "#{node['parisaccessible']['home']}/inject/gtfs"
    code <<-EOH
    ./split_file.sh stop_times.txt #{node['parisaccessible']['injecting']['total_worker']}
    ./split_file.sh trips.txt #{node['parisaccessible']['injecting']['total_worker']}
    chmod -R 777 ./
    EOH
  end
end


