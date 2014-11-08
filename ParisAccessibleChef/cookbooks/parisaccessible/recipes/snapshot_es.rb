#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
if node[:parisaccessible][:snapshot][:es]
	service 'elasticsearch' do
	  action :start
	  supports :status => true, :start => true, :stop => true, :restart => true
	end

	if node[:parisaccessible][:snapshot][:es][:erase]
		http_request "delete ratp_gtfs" do
		  action :delete
		  url "http://localhost:9200/ratp_gtfs"
		end

		http_request "delete accessibility" do
		  action :delete
		  url "http://localhost:9200/accessibility"
		end
	end

	http_request "config repo" do
	  action :put
	  url "http://localhost:9200/_snapshot/#{node['parisaccessible']['snapshot']['bucket']}"
	  message ({:type => "s3", :settings => {
	  	:bucket => "bzanni", :region => "#{node['parisaccessible']['snapshot']['region']}"
	  	}}.to_json)
	end

	if node[:parisaccessible][:snapshot][:es][:restore]
		http_request "restore #{node['parisaccessible']['snapshot']['es']['restore']}" do
		  action :post
		  url "http://localhost:9200/_snapshot/#{node['parisaccessible']['snapshot']['bucket']}/#{node['parisaccessible']['snapshot']['es']['restore']}/_restore"
		  message ({}.to_json)
		end
	end

	if node[:parisaccessible][:snapshot][:es][:export]
		http_request "export #{node['parisaccessible']['snapshot']['es']['restore']}" do
		  action :put
		  url "http://localhost:9200/_snapshot/#{node['parisaccessible']['snapshot']['bucket']}/#{node['parisaccessible']['snapshot']['es']['export']}??wait_for_completion=true"
		  message ({}.to_json)
		end
	end
end



