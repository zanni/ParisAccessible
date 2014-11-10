#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#

# bash "import trips" do
#   user "root"
#   cwd "#{node['parisaccessible']['home']}"
#   code <<-EOH
   
#   java -jar -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleIndexer/target/*.war > #{node['parisaccessible']['log']}/index.trottoir_listener.log &  
#   EOH
# end

template "/etc/init.d/trottoir_inserter" do
  source "java_upstart.erb"
  action :create
  variables({
     :name => "trottoir_inserter",
     :commande => "ParisAccessibleIndexer/target/*.war",
     :log => "trottoir_inserter.log"
  })
end
service 'trottoir_inserter' do
  action :start
  supports :start => true, :stop => true
end