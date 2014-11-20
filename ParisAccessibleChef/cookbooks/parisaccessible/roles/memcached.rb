name        "memcached"
description "Configuration for memcached nodes"

run_list    "role[base]",
			"recipe[memcached]"
     

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
