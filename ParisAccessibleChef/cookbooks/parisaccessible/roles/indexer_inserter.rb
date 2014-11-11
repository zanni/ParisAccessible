name        "indexer_inserter"
description "Configuration for indexer_inserter nodes"

run_list    "role[base]",
			"recipe[java]",
            "recipe[maven]",
             "recipe[parisaccessible::install]",
			"recipe[parisaccessible::index_trottoir_inserter]"

default_attributes(
	:maven => {
		:setup_bin => true
	},
	:neo4j => {
		:server => {
			:enabled => false,
			:version => "2.1.4",
			:plugins => {
				:spatial => {
					:enable => false
				}
			}
		}     
	}
)        

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
