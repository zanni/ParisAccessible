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

if ::File.exists?("/etc/init.d/pa_webapp")
  service 'pa_webapp' do
    action :stop
    supports :start => true, :stop => true
  end
end
template "/etc/init.d/pa_webapp" do
  source "java_upstart.erb"
  action :create
  mode '0777'
  variables({
     :name => "pa_webapp",
     :commande => "ParisAccessibleApp/target/*.war",
     :log => "webapp.log"
  })
end
service 'pa_webapp' do
  action :start
  supports :start => true, :stop => true
end