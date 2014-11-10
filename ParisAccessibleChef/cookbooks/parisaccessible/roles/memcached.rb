name        "memcached"
description "Configuration for memcached nodes"

run_list    "role[base]",
			"recipe[memcached]"

default_attributes(
	:rabbitmq => {
		:use_distro_version => false,
		:package => "http://www.rabbitmq.com/releases/rabbitmq-server/v3.4.1/rabbitmq-server_3.4.1-1_all.deb",
		:enabled_plugins => ['rabbitmq_management','rabbitmq_management_visualiser']

	}
)        

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
