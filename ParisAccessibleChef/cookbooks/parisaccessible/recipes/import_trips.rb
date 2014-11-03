#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#


# Dir["#{node['parisaccessible']['home']}/inject/gtfs/trips.txt.*"].each do |file | 
	# bash "import base" do
	#   user "root"
	#   cwd "#{node['parisaccessible']['home']}"
	#   code <<-EOH
	#   java -jar -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -r gtfs/routes.txt > #{node['parisaccessible']['log']}/inject.routes.log
	 
	#   EOH
	# end

	bash "import trips" do
	  user "root"
	  cwd "#{node['parisaccessible']['home']}/inject"
	  code <<-EOH
	 java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --trip trips\\.txt\\.+ > #{node['parisaccessible']['log']}/inject.trips.log  

	  EOH
	end
# end