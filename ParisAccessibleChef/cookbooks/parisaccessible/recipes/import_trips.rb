#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#


	bash "import trips" do
	  user "root"
	  cwd "#{node['parisaccessible']['home']}"
	  code <<-EOH
	 java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --trip trips\\.txt\\.+ > #{node['parisaccessible']['log']}/inject.trips.log  

	  EOH
	end
