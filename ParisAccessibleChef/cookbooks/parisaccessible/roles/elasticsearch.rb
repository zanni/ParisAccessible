name        "elasticsearch"
description "Configuration for elasticsearch nodes"

run_list    "role[base]",
			"recipe[java]",
            "recipe[elasticsearch]",

            "recipe[elasticsearch::nginx]",
            "recipe[elasticsearch::proxy]",
            "recipe[elasticsearch::plugins]",
            "recipe[elasticsearch::aws]"
            

override_attributes(
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)