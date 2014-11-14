name        "injector_base"
description "Configuration for injector_base nodes"

run_list    "role[base]",
			"recipe[java]",
			"recipe[maven]",
			"recipe[parisaccessible::install]",
			"recipe[parisaccessible::data_gtfs]",
			"recipe[parisaccessible::data_accessibility]",
			"recipe[parisaccessible::import_base]"


            

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
