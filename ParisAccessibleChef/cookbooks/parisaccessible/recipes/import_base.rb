#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
bash "import base" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms256m -Xmx512m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -r gtfs/routes.txt > #{node['parisaccessible']['log']}/inject.routes.log  
  java -jar -Xms256m -Xmx512m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -ra accessibility/route_accessibility.csv > #{node['parisaccessible']['log']}/inject.routes.accessibility.log    
  java -jar -Xms256m -Xmx512m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -c gtfs/calendar.txt > #{node['parisaccessible']['log']}/inject.calendar.log    
  java -jar -Xms256m -Xmx512m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -s gtfs/stops.txt > #{node['parisaccessible']['log']}/inject.stops.log    
  java -jar -Xms256m -Xmx512m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -sa accessibility/stop_accessibility.csv > #{node['parisaccessible']['log']}/inject.stops.accessibility.log    
  EOH
end