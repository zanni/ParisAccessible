name        "indexer"
description "Configuration for indexer nodes"

run_list    "role[base]",
			"recipe[java]",
			"recipe[maven]",
			"recipe[parisaccessible::install]"
			# "recipe[parisaccessible::index_trottoir]"

            

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
