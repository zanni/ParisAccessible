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
		template "/etc/init.d/trottoir_worker_#{$index}" do
		  source "java_upstart.erb"
		  action :create
		  mode '0777'
		  variables({
		     :name => "trottoir_worker_#{$index}",
		     :commande => "ParisAccessibleIndexerWorker/target/*.war --index_trottoir --index_worker #{$index} --total_worker #{node['parisaccessible']['indexing']['total_worker']}",
		     :log => "trottoir_inserter.log"
		  })
		end
	   $i +=1
	end
	$i = 0
	while $i < node[:parisaccessible][:indexing][:workers]  do
		$index = $i + node[:parisaccessible][:indexing][:index_worker]
		bash "trottoir_worker_#{$index}" do
		  user "root"
		  cwd "#{node['parisaccessible']['home']}"
		  code <<-EOH
		  sudo service trottoir_worker_#{$index} start
		  EOH
		end
	   $i +=1
	end
end

