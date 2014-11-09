name        "neo4j"
description "Configuration for neo4j nodes"

run_list    "role[base]",
			"recipe[neo4j-server::tarball]",
			"recipe[java]",
            "recipe[maven]",
             "recipe[parisaccessible::install]"

default_attributes(
	:neo4j => {
		:server => {
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
