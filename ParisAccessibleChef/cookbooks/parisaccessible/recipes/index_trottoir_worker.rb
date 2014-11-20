#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#

if node[:parisaccessible][:indexing][:workers]
	$i = 0
	while $i < node[:parisaccessible][:indexing][:workers]  do
	   $index = $i + node[:parisaccessible][:indexing][:index_worker]
	 #   bash "import trips" do
		#   user "root"
		#   cwd "#{node['parisaccessible']['home']}"
		#   code <<-EOH
		#   java -jar -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleIndexerWorker/target/*.war --index_trottoir --index_worker #{$index} --total_worker #{node['parisaccessible']['indexing']['total_worker']} > #{node['parisaccessible']['log']}/index.trottoir.log  &
		#   EOH
		# end
		template "/etc/init.d/pa_index_w#{$index}" do
		  source "java_upstart.erb"
		  action :create
		  mode '0777'
		  variables({
		     :name => "pa_index_w#{$index}",
		     :commande => "ParisAccessibleIndexerWorker/target/*.war --index_trottoir --index_worker #{$index} --total_worker #{node['parisaccessible']['indexing']['total_worker']}",
		     :log => "pa_index_w#{$index}.log"
		  })
		end
	   $i +=1
	end
	$i = 0
	while $i < node[:parisaccessible][:indexing][:workers]  do
		$index = $i + node[:parisaccessible][:indexing][:index_worker]
		bash "pa_index_w#{$index}" do
		  user "root"
		  cwd "#{node['parisaccessible']['home']}"
		  code <<-EOH
		  sudo service pa_index_w#{$index} start
		  EOH
		end
	   $i +=1
	end
end

