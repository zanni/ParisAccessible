#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
service "neo4j" do
  action :stop
end

bash "import trips" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
 java -jar -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=30504 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleIndexer/target/*.war --index_trottoir > #{node['parisaccessible']['log']}/inject.trips.log  
  EOH
end
