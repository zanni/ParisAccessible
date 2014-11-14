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

$CMD = "#{$WORKER_CMD} --access_trottoir trottoir\\.csv\\.+"
$LOG = "#{node['parisaccessible']['log']}/inject.access_trottoir.log"

bash "import access_trottoir" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} #{$CMD} > #{$LOG}
  EOH
end



