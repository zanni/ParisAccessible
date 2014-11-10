name        "neo4j"
description "Configuration for neo4j nodes"

run_list    "role[base]",
			"recipe[neo4j-server::tarball]",
			"recipe[java]",
            "recipe[maven]",
             "recipe[parisaccessible::install]",
			"recipe[parisaccessible::index_trottoir_inserter]",
			"recipe[parisaccessible::index_trottoir_worker]"

default_attributes(
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
