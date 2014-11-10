#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#

if node[:parisaccessible][:indexing][:workers]
	while $i < node[:parisaccessible][:indexing][:workers]  do
	   $index = $i + node[:parisaccessible][:indexing][:index_worker]
	   bash "import trips" do
		  user "root"
		  cwd "#{node['parisaccessible']['home']}"
		  code <<-EOH
		  java -jar -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleIndexerWorker/target/*.war --index_trottoir --index_worker #{$index} --total_worker #{node['parisaccessible']['indexing']['total_worker']} > #{node['parisaccessible']['log']}/index.trottoir.log  &
		  EOH
		end
	   $i +=1
	end
end

