name        "haproxy"
description "Application load balancer settings"

run_list    "role[base]",
            "recipe[haproxy]"

override_attributes(
	:haproxy => {
          :admin => {
            :address_bind => "0.0.0.0"
            }
        }
)