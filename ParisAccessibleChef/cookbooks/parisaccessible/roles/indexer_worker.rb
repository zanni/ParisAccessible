name        "indexer_worker"
description "Configuration for indexer nodes"

run_list    "role[base]",
			"recipe[java]",
			"recipe[maven]",
			"recipe[parisaccessible::install]",
			"recipe[parisaccessible::index_trottoir_worker]"

            

override_attributes(	
	:maven => {
		:setup_bin => true
	},
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
