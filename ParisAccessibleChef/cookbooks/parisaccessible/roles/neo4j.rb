name        "neo4j"
description "Configuration for neo4j nodes"

run_list    "role[base]",
			"recipe[neo4j-server::tarball]"

            

override_attributes(	
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)
