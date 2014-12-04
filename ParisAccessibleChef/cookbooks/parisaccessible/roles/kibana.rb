name        "neo4j"
description "Configuration for kibana nodes"

run_list    "role[base]",
			"recipe[kibana::nginx]",
			"recipe[kibana::install]"
			

override_attributes(
	 :kibana => {
        :file => {
        	:version => "3.1.2",
        	:url => "https://download.elasticsearch.org/kibana/kibana/kibana-3.1.2.tar.gz?_ga=1.168164129.1658202471.1415810449"
        },
        :web_dir => "/opt/kibana/current",
        :webserver_listen => "0.0.0.0",
        :webserver_port => 8080
      }
)


