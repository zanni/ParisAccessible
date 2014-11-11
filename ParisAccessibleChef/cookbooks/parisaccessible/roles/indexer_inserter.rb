name        "indexer_inserter"
description "Configuration for indexer_inserter nodes"

run_list    "role[base]",
			"recipe[java]",
            "recipe[maven]",
             "recipe[parisaccessible::install]",
             "role[memcached]",
             "role[rabbitmq]",
			"recipe[parisaccessible::index_trottoir_inserter]"

