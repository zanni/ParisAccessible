<!DOCTYPE html>
<html ng-app="demoapp">
  <head>
    <script src="/lib/angular.min.js"></script>
    <script src="/lib/leaflet.js"></script>
    <script src="/lib/leaflet.label.js"></script>
    <script src="/lib/angular-leaflet-directive.min.js"></script>
    <link rel="stylesheet" href="/lib/leaflet.css" />
    <link rel="stylesheet" href="/lib/leaflet.label.css" />
    <script>
        var app = angular.module("demoapp", ["leaflet-directive"]);
        
        app.controller("DemoController", [ "$scope", "leafletData", '$http', function($scope, $leafletData, $http) {
			angular.extend($scope, {
	        center: {
				            lat: 48.85131011191184,
				            lng: 2.344357967376709,
				            zoom: 18
				        },
	        
	        paths: {},
	        markers: {},
	        events: {
	            map: {
	                enable: ['click'],
	                logic: 'emit'
	            }
	        },
	        defaults: {
	            scrollWheelZoom: false
	        }
	    });
	    
	   /* 
	      $http.get("http://bzanni:bzanni@54.164.50.211:9200/accessibility/trottoir/_search")
	    	.success(function(data, status){
	    		console.log(data);
	    		var paths = {}
	    		var sidways = data.hits.hits
	    		for(var i in sidways){
		    		var sidway = sidways[i];
		    		var shape = sidway._source.shape.coordinates;
		    		
		    		var path = {
		    				weight: 3,
			          		type: "polyline",
			                latlngs: []
		                };
		           
		    		for(var line in shape){
		    			
		    		
		    			// path.latlngs.push({lat: shape[line][1]- 0.000060, lng: shape[line][0]- 0.00070});
		    			
		    			path.latlngs.push({lat: shape[line][0], lng: shape[line][1]});
		    			
		    			
		    					    			
		    		}
		    		 paths[i] = path;
	    		}
	    		
	    		
	    		$scope.paths = {p: paths[1], p2: paths[2], p3: paths[3]
	    		, p4: paths[4], p5: paths[5]};
	    		
		    			
	    	});
	    	
	    	*/
	    
	    
	    
	    
	    function drawPath(start, end){
	  
	    
	    
	    $http
			    .get('/path', {
			        params: {
			            start_lat: start.lng,
			            start_lon: start.lat,
			            end_lat: end.lng,
			            end_lon: end.lat
			        }
			     })
			     .success(function (data,status) {
					console.log(data);
			          if(data.length > 0){
				           var paths = {};
				          paths.p0 = {	type: "polyline",latlngs: [ ] }
				 
	
				                
				               
			                
				          for(var i in data){
				          	var loc = data[i];
				          	console.log(loc);
				          	paths.p0.latlngs.push({lng: (loc.lat ), lat: loc.lon });
				         
				          }
				          
				          				          
				          markers = [
				          {
					     		lat: start.lat,
					     		lng: start.lng
					     	},
					     	{
					     		lat: end.lat,
					     		lng: end.lng
					     	}
				          ];
				          $scope.paths = paths;
				          angular.extend($scope, {
						
					        paths: paths,
					        markers: markers
			
					    	});
					    	start = null;
			       			 end = null;
				          }
				          else {
				          angular.extend($scope, {
						
					        paths: paths,
					        markers: []
			
					    });
					    start = null;
			       		end = null;
			          }
			         
				   
			          
			     });
	    }
	    
	    $leafletData.getMap().then(function(map) {
	    	var bounds = map.getBounds();
	    	console.log(bounds.getNorthEast());
	    	
	    	$scope.markers.push({
	     		lng: bounds.getSouthEast().lng,
	     		lat: bounds.getSouthEast().lat
	     	});
					     	
	     	$scope.markers.push({
	     		lng: bounds.getNorthWest().lng,
	     		lat: bounds.getNorthWest().lat
	     	});
			
			$http.get('/envelope', {

			    params: {
			        p1Lat: bounds.getNorthWest().lat,
			        p1Lon: bounds.getNorthWest().lng,
			        p2Lat: bounds.getSouthEast().lat,
			        p2Lon: bounds.getSouthEast().lng
			    }
			 })
			 .success(function(data, status){ 
		     	 if(data){
		     	 console.log(data.length)
		     	 	for(var i in data){
		     	 		if(i > 20) return;
		     	 		
		     	 		$scope.markers.push({
				     		lng: data[i].lat,
				     		lat: data[i].lon
				     	});
		     	 	}
		     	}
		    })
			     
        });	
         
        var start = null;
        var end = null;
        $scope.markers = new Array();
        $scope.$on('leafletDirectiveMap.click', function(event, args){
       	
			$scope.eventDetected = args.leafletEvent.latlng
			
			console.log(args.leafletEvent.latlng)
       		$http
			    .get('/location', {
			        params: {
			            lon: args.leafletEvent.latlng.lng,
			            lat: args.leafletEvent.latlng.lat
			        }
			     })
			     .success(function(data, status){
			     	 if(data){
			     	
				     	$scope.markers.push({
				     		lng: data.lon,
				     		lat: data.lat
				     	});
				     	$scope.paths = {};

				     	
				     	if(start == null){
				     	
				     		start = {
					     		lng: data.lon,
					     		lat: data.lat
					     	}
				     	}
				     	
				     	else {
				     	
				     		end = {
					     		lng: data.lat,
					     		lat: data.lon
					     	}
					     	drawPath(start, end)
					     	 
				     	}
			     	}
			     	
			     })
      
	       
	    });
        	
		
        }]);
    </script>
    <style>
        .angular-leaflet-map {
            width: 640px;
            height: 400px;
        }
    </style>
  </head>
  <body ng-controller="DemoController">
    <form>
        Latitude : <input type="number" step="any" ng-model="center.lat">
        Longitude : <input type="number" step="any" ng-model="center.lng">
        Zoom : <input type="number" step="any" ng-model="center.zoom">
    </form>
    <ul>
        <li><strong ng-bind="eventDetected"></strong> caught in listener.</li>
    </ul>
    <leaflet center="center" paths="paths" markers="markers" defaults="defaults" height="500"></leaflet>
  </body>
</html>
