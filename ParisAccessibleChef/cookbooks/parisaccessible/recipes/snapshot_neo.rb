#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#

if node[:parisaccessible][:snapshot][:neo]

	    
 #    directory "/root/.aws/" do
	#   action :create
	# end
 #    template "/root/.aws/config" do
	#     source 'config.erb'
	#     cookbook "awscli"
 #  	end
  	
	service 'neo4j' do
	  action :stop
	  supports :status => true, :start => true, :stop => true, :restart => true
	end

	if node[:parisaccessible][:snapshot][:neo][:restore] != ""
		bash "restore neo backup #{node['parisaccessible']['snapshot']['neo']['restore']}" do
		  not_if { ::File.directory?("#{node['parisaccessible']['neo4j_data_path']}")  }
		  user "root"
		  cwd "#{node['parisaccessible']['neo4j_data_path']}"
		  code <<-EOH
		  	aws s3 cp s3://bzanni/neo_backup/#{node['parisaccessible']['snapshot']['neo']['restore']}.tar.gz ./
		  	tar xvzf #{node['parisaccessible']['snapshot']['neo']['restore']}.tar.gz
		  	rm #{node['parisaccessible']['snapshot']['neo']['restore']}.tar.gz
		  	chmod -R 777 #{node['parisaccessible']['neo4j_data_path']}
		  EOH
		end
	end

	if node[:parisaccessible][:snapshot][:neo][:export] != ""

		bash "export neo backup #{node['parisaccessible']['snapshot']['neo']['export']}" do
		  only_if { ::File.directory?("#{node['parisaccessible']['neo4j_data_path']}")  }
		  user "root"
		  cwd "#{node['parisaccessible']['neo4j_data_path']}"
		  code <<-EOH
		  	tar cvzf #{node['parisaccessible']['snapshot']['neo']['export']}.tar.gz #{node['parisaccessible']['snapshot']['neo']['export']}
		  	aws s3 cp #{node['parisaccessible']['snapshot']['neo']['export']}.tar.gz  s3://bzanni/neo_backup/
		  	rm #{node['parisaccessible']['snapshot']['neo']['export']}.tar.gz
		  EOH
		end

	end

	service 'neo4j' do
	  action :start
	  supports :status => true, :start => true, :stop => true, :restart => true
	end

end