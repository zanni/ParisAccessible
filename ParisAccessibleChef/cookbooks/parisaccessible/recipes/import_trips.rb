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
	directory "#{node['parisaccessible']['log']}/gtfs" do
  action :create
  mode '0777'
end
	bash "import trips" do
	  user "root"
	  cwd "#{node['parisaccessible']['home']}/inject"
	  code <<-EOH
	  find gtfs -name trips.txt.* -exec bash -c "sudo java -jar -Dparisaccessible_home=/srv/ParisAccessible/ ../ParisAccessibleApplication/target/*.war -t {} > /var/log/parisaccessible/{}.log &"  \\;

	  EOH
	end
# end