#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
bash "import trips" do
  user "root"
  cwd "#{node['parisaccessible']['home']}"
  code <<-EOH 
  curl -XPUT ["parisaccessible"]["elasticsearch_url"]/_snapshot/bzanni -d '{ \\
    	"type": "s3",													\\
    	"settings": {													\\
    	    "bucket": "bzanni",									\\
   	     "region": "us-east-1"											\\
   	 }																\\
	}'	

	curl -XPOST ["parisaccessible"]["elasticsearch_url"]/_snapshot/bzanni/20141105/_restore
  EOH
end
