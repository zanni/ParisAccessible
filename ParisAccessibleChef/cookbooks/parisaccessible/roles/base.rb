name        "base"
description "Basic tools and utilities for all nodes"

run_list    "recipe[apt]", "recipe[hostnames]"



 override_attributes(
	 :hostname_cookbook => {
          :use_node_ip=> true
        }
)