#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
bash "import gtfs_route" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --gtfs_route > #{node['parisaccessible']['log']}/inject.gtfs_route.log  
  EOH
end

bash "import gtfs_stop" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --gtfs_stop > #{node['parisaccessible']['log']}/inject.gtfs_stop.log  
  EOH
end

bash "import gtfs_other" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --gtfs_other > #{node['parisaccessible']['log']}/inject.gtfs_other.log  
  EOH
end

bash "import accessibility" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --accessibility > #{node['parisaccessible']['log']}/inject.accessibility.log  
  EOH
end


