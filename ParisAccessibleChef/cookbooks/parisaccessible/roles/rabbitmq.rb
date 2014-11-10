name        "rabbitmq"
description "Configuration for rabbitmq nodes"

run_list    "role[base]",
			"recipe[rabbitmq]",
			"recipe[rabbitmq::plugin_management]",
			"recipe[rabbitmq::user_management]"

default_attributes(
	:rabbitmq => {
		:use_distro_version => false,
		:version => "3.4.1",
		:enabled_plugins => ['rabbitmq_management','rabbitmq_management_visualiser'],
		:enabled_users => [{
			:name => "bzanni",
			:password => "bzanni",
			:tag => "administrator",
			:rights => [
				{
					:vhost => "/",
  					:conf => ".*",
  					:write => ".*",
  					:read => ".*"
				}
			]
		}]

	}
)        

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
