#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
$index_worker = node[:parisaccessible][:injecting][:index_worker]
$total_worker = node[:parisaccessible][:injecting][:index_worker]
$JAVA_OPTS = "-Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']}"
$WAR = "ParisAccessibleInjector/target/*.war"
$WORKER_CMD = "--index_worker #{$index_worker} --total_worker #{$total_worker}"

$CMD = "#{$WORKER_CMD} --gtfs_trip trips\\.txt\\.+"
$LOG = "#{node['parisaccessible']['log']}/inject.gtfs_trip.log"


bash "import gtfs_trip" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} #{$CMD} > #{$LOG}
  EOH
end


# bash "import trips" do
#   user "root"
#   cwd "#{node['parisaccessible']['home']}"
#   code <<-EOH
#  java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleInjector/target/*.war --gtfs_trip trips\\.txt\\.+ > #{node['parisaccessible']['log']}/inject.trips.log  
#   EOH
# end
