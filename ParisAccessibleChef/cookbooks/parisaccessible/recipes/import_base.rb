#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
bash "import route" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -r gtfs/routes.txt > #{node['parisaccessible']['log']}/inject.routes.log  
  EOH
end

bash "import route accessibility" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -ra accessibility/route_accessibility.csv > #{node['parisaccessible']['log']}/inject.routes.accessibility.log    
  EOH
end

bash "import service" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -c gtfs/calendar.txt > #{node['parisaccessible']['log']}/inject.calendar.log    
  EOH
end

bash "import stops" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -s gtfs/stops.txt > #{node['parisaccessible']['log']}/inject.stops.log    
  EOH
end

bash "import stops accessibility" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war -sa accessibility/stop_accessibility.csv > #{node['parisaccessible']['log']}/inject.stops.accessibility.log    
  EOH
end