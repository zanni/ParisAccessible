#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#

$JAVA_OPTS = "-Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']}"
$WAR = "ParisAccessibleInjector/target/*.war"

# import route
$NAME = "gtfs_route"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end


# import stop
$NAME = "gtfs_stop"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end


# import other
$NAME = "gtfs_other"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end


$NAME = "access_equipement"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end





