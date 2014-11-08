name        "snapshot"
description "Configuration for snapshot nodes"

run_list    "role[base]",
			"recipe[awscli]",
			"recipe[parisaccessible::snapshot_neo]",
			"recipe[parisaccessible::snapshot_es]"

aws = Chef::DataBagItem.load('parisaccessible', 'aws') rescue {}

default_attributes(
  :awscli => {
    :config_profiles =>  {
      :default => {
        :region => "us-east-1",
        :aws_access_key_id => aws['access_key'],
        :aws_secret_access_key => aws['secret_key']
      }
    }
)