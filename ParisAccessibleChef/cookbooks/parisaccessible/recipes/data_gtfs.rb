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

bash "unzip and split ratp_gtfs.zip" do
  user "root"
  cwd "#{node['parisaccessible']['home']}/inject/gtfs"
  not_if { ::File.exists?("#{node['parisaccessible']['home']}/inject/gtfs/.done") }
  code <<-EOH
  unzip ratp_gtfs.zip
  rm -rf ratp_gtfs.zip
  split --lines=1000000 stop_times.txt stop_times.txt.
  split --lines=50000 trips.txt trips.txt.
  chmod -R 777 ./
  rm stop_times.txt
  touch .done
  EOH
end


