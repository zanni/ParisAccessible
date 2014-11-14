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
        app.controller("DemoController", [ "$scope", '$http', function($scope, $http) {
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
	    		
	    		console.log(paths);
	    		
	    		$scope.paths = {p: paths[1], p2: paths[2], p3: paths[3]
	    		, p4: paths[4], p5: paths[5]};
	    		
		    			
	    	});
	    
	    function drawPath(start, end){
	  
	    
	    
	    $http
			    .get('/path', {
			        params: {
			            start_lat: start.lat,
			            start_lon: start.lng,
			            end_lat: end.lat,
			            end_lon: end.lng
			        }
			     })
			     .success(function (data,status) {
			          console.log(data);
			          var paths = {};
			          paths.p1 = {	type: "polyline",latlngs: [ ] }
			           paths.p2 = {	type: "polyline",latlngs: [ ] }
			           paths.p1 = {	type: "polyline",latlngs: [ ] }	

			                
			               
		                
			          for(var i in data){
			          	var loc = data[i];
			          	paths.p0.latlngs.push({lat: (loc.lon ), lng: loc.lat });
			          	paths.p1.latlngs.push({lat: (loc.lon + 0.00008), lng: loc.lat + 0.00077});
			          	paths.p2.latlngs.push({lat: (loc.lon + 0.00008), lng: loc.lat + 0.00077});
			          }
			          
			          
			          
			          start = null;
			          console.log(paths);
			          $scope.paths = paths;
			          angular.extend($scope, {
		
				        paths: paths,
		
				    });
			          
			     });
	    }
         
        var start = null;
        var end = null;
        $scope.markers = new Array();
        $scope.$on('leafletDirectiveMap.click', function(event, args){
       	
			$scope.eventDetected = args.leafletEvent.latlng
			
       		$http
			    .get('/location', {
			        params: {
			            lon: args.leafletEvent.latlng.lat,
			            lat: args.leafletEvent.latlng.lng
			        }
			     })
			     .success(function(data, status){
			     	console.log(data, status);
			     	 if(data){
			     	
				     	$scope.markers.push({
				     		lng: data.lat,
				     		lat: data.lon
				     	});
				     	
				     	if(!start){
				     		start = {
					     		lng: data.lat,
					     		lat: data.lon
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
