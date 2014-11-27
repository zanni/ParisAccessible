#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#


directory "#{node['parisaccessible']['home']}/inject/accessibility" do
  action :create
end

remote_file "#{node['parisaccessible']['home']}/inject/accessibility/trottoir.csv" do
  source "#{node['parisaccessible']['accessibility_trottoir_url']}"
  not_if { ::File.exists?("#{node['parisaccessible']['home']}/inject/accessibility/trottoir.csv") }
end

remote_file "#{node['parisaccessible']['home']}/inject/accessibility/passagepieton.csv" do
  source "#{node['parisaccessible']['accessibility_passagepieton_url']}"
  not_if { ::File.exists?("#{node['parisaccessible']['home']}/inject/accessibility/passagepieton.csv") }
end

remote_file "#{node['parisaccessible']['home']}/inject/accessibility/equipement.csv" do
  source "#{node['parisaccessible']['accessibility_equipement_url']}"
  not_if { ::File.exists?("#{node['parisaccessible']['home']}/inject/accessibility/equipement.csv") }
end

cookbook_file "route_accessibility.csv" do
  path "#{node['parisaccessible']['home']}/inject/accessibility/route_access.csv"
  action :create_if_missing
end

cookbook_file "stop_accessibility.csv" do
  path "#{node['parisaccessible']['home']}/inject/accessibility/stop_access.csv"
  action :create_if_missing
end

cookbook_file "split_file.sh" do
  path "#{node['parisaccessible']['home']}/inject/accessibility/split_file.sh"
  action :create_if_missing
end

if node[:parisaccessible][:injecting]
  bash "split trottoir.csv and passagepieton.csv" do
    user "root"
    cwd "#{node['parisaccessible']['home']}/inject/accessibility/"
    not_if { ::File.exists?("#{node['parisaccessible']['home']}/inject/accessibility/.done") }
    code <<-EOH
    chmod +x split_file.sh

    ./split_file.sh trottoir.csv #{node['parisaccessible']['injecting']['total_worker']}
    ./split_file.sh passagepieton.csv #{node['parisaccessible']['injecting']['total_worker']}
    ./split_file.sh route_access.csv #{node['parisaccessible']['injecting']['total_worker']}
    ./split_file.sh stop_access.csv #{node['parisaccessible']['injecting']['total_worker']}
    
    chmod -R 777 ./
    touch .done
    EOH
  end
end 



