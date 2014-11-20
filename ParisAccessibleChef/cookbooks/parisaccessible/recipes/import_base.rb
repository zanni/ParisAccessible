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

$NAME = "gtfs_route"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end



# bash "import gtfs_route" do
#   user "root"
#   cwd "#{node['parisaccessible']['home']}"
#   code <<-EOH
#   java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleInjector/target/*.war --gtfs_route > #{node['parisaccessible']['log']}/inject.gtfs_route.log  
#   EOH
# end

$NAME = "gtfs_stop"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end


# bash "import gtfs_stop" do
#   user "root"
#   cwd "#{node['parisaccessible']['home']}"
#   code <<-EOH
#   java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleInjector/target/*.war --gtfs_stop > #{node['parisaccessible']['log']}/inject.gtfs_stop.log  
#   EOH
# end

$NAME = "gtfs_other"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end

# bash "import gtfs_other" do
#   user "root"
#   cwd "#{node['parisaccessible']['home']}"
#   code <<-EOH
#   java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleInjector/target/*.war --gtfs_other > #{node['parisaccessible']['log']}/inject.gtfs_other.log  
#   EOH
# end

$NAME = "access_equipement"
$LOG = "#{node['parisaccessible']['log']}/inject.#{$NAME}.log"
bash "import #{$NAME}" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH
  java -jar #{$JAVA_OPTS} #{$WAR} --#{$NAME} > #{$LOG}
  EOH
end

# bash "import access_equipement" do
#   user "root"
#   cwd "#{node['parisaccessible']['home']}"
#   code <<-EOH
#   java -jar -Xms2048m -Xmx2048m -Dparisaccessible_home=#{node['parisaccessible']['home']} ParisAccessibleInjector/target/*.war --access_equipement > #{node['parisaccessible']['log']}/inject.access_equipement.log  
#   EOH
# end




