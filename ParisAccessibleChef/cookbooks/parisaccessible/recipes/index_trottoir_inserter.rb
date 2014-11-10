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
   
  java -jar -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleIndexer/target/*.war > #{node['parisaccessible']['log']}/index.trottoir_listener.log &  
  EOH
end
