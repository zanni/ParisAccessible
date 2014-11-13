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

template "/etc/init.d/pa_index_insert" do
  source "java_upstart.erb"
  action :create
  mode '0777'
  variables({
     :name => "pa_index_insert",
     :commande => "ParisAccessibleIndexer/target/*.war",
     :log => "index_inserter.log"
  })
end
service 'pa_index_insert' do
  action :start
  supports :start => true, :stop => true
end