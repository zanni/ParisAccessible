name        "injector"
description "Configuration for injector nodes"

run_list    "role[base]",
			"recipe[java]",
			"recipe[maven]",
			"recipe[parisaccessible::install]",
			"recipe[parisaccessible::ratp_gtfs]",
			"recipe[parisaccessible::import_trips]"

            

override_attributes(
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
