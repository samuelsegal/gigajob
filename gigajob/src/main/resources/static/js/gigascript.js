/**
 * 
 */

var locationDiv; 
function getLocation() {
	locationDiv = document.getElementById("displayLocation");
	console.log("fuku gigajigs");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
        
        console.log("navigator.geoloaction: " + navigator.geolocation);
        
    } else { 
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
}

function showPosition(position) {
	locationDiv.innerHTML = "Latitude: " + position.coords.latitude + 
    "<br>Longitude: " + position.coords.longitude;	
}