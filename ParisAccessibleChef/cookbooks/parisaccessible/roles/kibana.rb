name        "neo4j"
description "Configuration for kibana nodes"

run_list    "role[base]",
            "recipe[kibana::nginx]",
            "recipe[kibana::install]"
            
			
           
			

override_attributes(
	 :kibana => {
        :file => {
        	:version => "latest",
        	:url => "http://download.elasticsearch.org/kibana/kibana/kibana-latest.tar.gz",
            :checksum => "22f9f5e7500c55f3ebd27674a9d6402815214993b76504e78061ab748e497766"
        },
        :web_dir => "/opt/kibana/current",
        :webserver_listen => "0.0.0.0",
        :webserver_port => 8080
      }
)


