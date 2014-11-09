name        "elasticsearch"
description "Configuration for elasticsearch nodes"

run_list    "role[base]",
			"recipe[java]",
            "recipe[elasticsearch]",

            "recipe[elasticsearch::nginx]",
            "recipe[elasticsearch::proxy]",
            "recipe[elasticsearch::plugins]",
            "recipe[elasticsearch::aws]"
 

aws = Chef::DataBagItem.load('parisaccessible', 'aws')['_default'] rescue {}
default_attributes(
	:elasticsearch => {
          :discovery => {:type => "ec2"},
          :cloud => {
            :aws => {
              :access_key => aws['access_key'],
              :secret_key => aws['secret_key']
            },
            :ec2 => {
              :security_group => "default"
            }
          },
          :nginx => { 
            :users => [ {:username => "bzanni", :password => "bzanni"} ],
            :allow_cluster_api => true,
            :port => 80
          },
          :plugins => {
            "karmi/elasticsearch-paramedic" => {},
            "elasticsearch/elasticsearch-cloud-aws" => {
              :version => "2.3.0"
           },
          },
          :cluster => { :name => "elasticsearch" },
          :version => "1.3.2"
        }
)

override_attributes(
	 :java => {
          :install_flavor => "openjdk",
          :jdk_version =>  "7"
        },
)