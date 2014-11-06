#
# Cookbook Name:: exportcarrouf
# Recipe:: install
#
# Copyright (C) 2014 Bertrand Zanni
#
# All rights reserved - Do Not Redistribute
#
http_request "delete ratp_gtfs" do
  action :delete
  url "http://localhost:9200/ratp_gtfs"
end

http_request "delete accessibility" do
  action :delete
  url "http://localhost:9200/accessibility"
end

http_request "config repo" do
  action :put
  url "http://localhost:9200/_snapshot/bzanni"
  message ({:type => "s3", :settings => {
  	:bucket => "bzanni", :region => "us-east-1"
  	}}.to_json)
end

http_request "restore" do
  action :post
  url "http://localhost:9200/_snapshot/bzanni/20141105/_restore"
  message ({}.to_json)
end
