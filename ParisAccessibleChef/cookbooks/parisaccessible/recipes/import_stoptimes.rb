#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#


bash "import stoptimes" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
 java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleInjector/target/*.war --gtfs_stoptime stop_times\\.txt\\.+ > #{node['parisaccessible']['log']}/inject.stop_times.log  
  EOH
end

$index_worker = node[:parisaccessible][:injecting][:index_worker]
$total_worker = node[:parisaccessible][:injecting][:index_worker]
$JAVA_OPTS = "-Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']}"
$WAR = "ParisAccessibleInjector/target/*.war"
$WORKER_CMD = "--index_worker #{$index_worker} --total_worker #{$total_worker}"

$CMD = "#{$WORKER_CMD} --gtfs_stoptime stop_times\\.txt\\.+"
$LOG = "#{node['parisaccessible']['log']}/inject.gtfs_stoptime.log"

bash "import gtfs_stoptime" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} #{$CMD} > #{$LOG}
  EOH
end