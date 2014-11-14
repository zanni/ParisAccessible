name        "injector_worker"
description "Configuration for injector_worker nodes"

run_list    "role[base]",
			"recipe[java]",
			"recipe[maven]",
			"recipe[parisaccessible::install]",
			"recipe[parisaccessible::data_gtfs]",
			"recipe[parisaccessible::data_accessibility]",
			"recipe[parisaccessible::import_trottoir]",
			"recipe[parisaccessible::import_passagepieton]",
			"recipe[parisaccessible::import_trips]",
			"recipe[parisaccessible::import_stoptimes]"

            

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
