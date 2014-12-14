name        "webapp"
description "Configuration for webapp nodes"

run_list    "role[base]",
			"recipe[java]",
            "recipe[maven]",
            "recipe[parisaccessible::install]",
			"recipe[parisaccessible::webapp]"

