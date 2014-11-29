name        "injector_worker"
description "Configuration for worker nodes"

run_list    "role[base]",
			"recipe[java]",
			"recipe[maven]",
			"recipe[parisaccessible::install]"

            

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
