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
  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleApplication/target/*.war --base > #{node['parisaccessible']['log']}/inject.base.log  
  EOH
end
