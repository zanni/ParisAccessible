name        "neo4j"
description "Configuration for kibana nodes"

run_list    "role[base]",
            "recipe[kibana::nginx]",
            "recipe[kibana::install]"
            
			
           
			

override_attributes(
	 :kibana => {
        :file => {
        	:version => "latest",
        	:url => "http://download.elasticsearch.org/kibana/kibana/kibana-latest.tar.gz"
        },
        :web_dir => "/opt/kibana/current",
        :webserver_listen => "0.0.0.0",
        :webserver_port => 8080
      }
)


