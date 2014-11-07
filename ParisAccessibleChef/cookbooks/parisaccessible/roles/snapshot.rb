name        "snapshot"
description "Configuration for snapshot nodes"

run_list    "role[base]",
			"recipe[parisaccessible::snapshot]"

