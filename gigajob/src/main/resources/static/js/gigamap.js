
		if ( $( "#gigamap" ).length ){
			L.mapbox.accessToken = 'pk.eyJ1Ijoic2FtdWVsc2VnYWwiLCJhIjoiY2lmenN0dGJkNjNnNXV3a3I3cjB4ODhuYSJ9.VK6fL73lai0a3oTJ4ndu-w';
		    var map = L.mapbox.map('gigamap', 
		    		'samuelsegal.cifzsts6p62detglytdi46jvc');
		    map.setZoom(10);
		    L.control.locate().addTo(map);
		    /*<![CDATA[*/
		    
			    var data = /*[[${postsAsJSON}]]*/ null;  
				var json = JSON.parse(data);
				var jsonContent = json.content;
				console.log("JSON" + JSON.stringify(json));
				console.log("content length: " + jsonContent.length);
				for(var i = 0; i < jsonContent.length; i++){
					L.marker([jsonContent[i].location[0],jsonContent[i].location[1]])
					 .addTo(map)
					 .bindPopup("<b>"+jsonContent[i].title + "</b><br/>" +
							 "Location (lat / lon): " + 
							 jsonContent[i].location[0] + " / " +
							 jsonContent[i].location[1])
							 .openPopup();
				}			    
			    
		    /*]]>*/
		}

		if ( $( "#postLocationMap" ).length ){
			L.mapbox.accessToken = 'pk.eyJ1Ijoic2FtdWVsc2VnYWwiLCJhIjoiY2lmenN0dGJkNjNnNXV3a3I3cjB4ODhuYSJ9.VK6fL73lai0a3oTJ4ndu-w';
			var map = L.mapbox.map('postLocationMap', 
					'samuelsegal.cifzsts6p62detglytdi46jvc');
			L.control.locate().addTo(map);
			map.setZoom(10);
			//L.control.locate().addTo(map);
			//Initialize the geocoder control and add it to the map.
			var geocoderControl = L.mapbox.geocoderControl('mapbox.places', {autocomplete: true});
			geocoderControl.addTo(map);
			
			// Listen for the `found` result and display the first result
			// in the output container. For all available events, see
			// https://www.mapbox.com/mapbox.js/api/v2.2.2/l-mapbox-geocodercontrol/#section-geocodercontrol-on
			var output = document.getElementById('displayLocation');
			geocoderControl.on('found', function(res) {
			    output.innerHTML = JSON.stringify(res.results.features[0].geometry.coordinates);
			});
			geocoderControl.on('select', function(res) {
				console.dir("res: " + res.feature);
			    output.innerHTML = res.feature.place_name;
			    var lat = res.feature.geometry.coordinates[1];
			    var lon = res.feature.geometry.coordinates[0];
				document.getElementById("loclat").value = lat;
				document.getElementById("loclon").value = lon;
				var postTitle = $('#postForm #title');
				console.dir(postTitle.val());
				L.marker([lat,lon]).addTo(map).bindPopup(
						"<b>" + postTitle.val() + "</b><br/>" +
						 "Location (lat / lon): " + 
						 lat + " / " + lon)
						 .openPopup();;
			});
		}

