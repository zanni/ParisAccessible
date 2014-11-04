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

bash "import access_equipement" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --access_equipement > #{node['parisaccessible']['log']}/inject.access_equipement.log  
  EOH
end

bash "import access_trottoir" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --access_trottoir trottoir\\.csv\\.+ > #{node['parisaccessible']['log']}/inject.access_trottoir.log  
  EOH
end

bash "import access_passagepieton" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --access_passagepieton passagepieton\\.csv\\.+ > #{node['parisaccessible']['log']}/inject.access_passagepieton.log  
  EOH
end


