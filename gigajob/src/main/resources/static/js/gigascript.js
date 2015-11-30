
var locationDiv; 
function getLocation() {
	
	locationDiv = document.getElementById("displayLocation");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition,errorCallback_highAccuracy,
        		{maximumAge:600000, timeout:5000, enableHighAccuracy:true});      
        console.log("navigator.geoloaction: " + navigator.geolocation);       
    } else { 
    	locationDiv.innerHTML = "Geolocation is not supported by this browser.";
    }
}

function showPosition(position) {
	
	var crd = position.coords;
	console.log('Your current position is:');
	console.log('Latitude : ' + crd.latitude);
	console.log('Longitude: ' + crd.longitude);
	console.log('More or less ' + crd.accuracy + ' meters.');
	document.getElementById("loclat").value = position.coords.latitude;
	document.getElementById("loclon").value = position.coords.longitude;

	var address = 'https://maps.googleapis.com/maps/api/geocode/json?latlng=' +
 		position.coords.latitude + ',' + position.coords.longitude + 
 		'&key=AIzaSyCC8gwbO-U2QwEIM-8cs0DFiGBuj_c4QwY';	

	$.ajax({
	    url: address
	}).then(function(data) {
		var formattedAddress = data.results[0].formatted_address;
		locationDiv.innerHTML = formattedAddress;
		document.getElementById("formattedAddy").value = formattedAddress;
	}); 	
}

function errorCallback_highAccuracy(position) {
    if (error.code == error.TIMEOUT)
    {
        // Attempt to get GPS loc timed out after 5 seconds, 
        // try low accuracy location
        $('body').append("attempting to get low accuracy location");
        navigator.geolocation.getCurrentPosition(
               successCallback, 
               errorCallback_lowAccuracy,
               {maximumAge:600000, timeout:10000, enableHighAccuracy: false});
        return;
    }
    
    var msg = "<p>Can't get your location (high accuracy attempt). Error = ";
    if (error.code == 1)
        msg += "PERMISSION_DENIED";
    else if (error.code == 2)
        msg += "POSITION_UNAVAILABLE";
    msg += ", msg = "+error.message;
    
    $('body').append(msg);
}

function errorCallback_lowAccuracy(position) {
    var msg = "<p>Can't get your location (low accuracy attempt). Error = ";
    if (error.code == 1)
        msg += "PERMISSION_DENIED";
    else if (error.code == 2)
        msg += "POSITION_UNAVAILABLE";
    else if (error.code == 3)
        msg += "TIMEOUT";
    msg += ", msg = "+error.message;
    
    $('body').append(msg);
}

function setGigaLocation() {
	
	locationDiv = document.getElementById("displayLocation");
	var gigaLocation = $("#autocomplete").val();
	document.getElementById("formattedAddy").value = gigaLocation;
    locationDiv.innerHTML = gigaLocation;

}

function toggleAvail(){

 var token = $("input[name='_csrf']").val();
 var header = "X-CSRF-TOKEN";
 $(document).ajaxSend(function(e, xhr, options) {
     xhr.setRequestHeader(header, token);
 });

console.log('trickle');
$.ajax({
 url: "/toggleProviderAvail?available=false",
 type: "POST",
 success:function(response) {
     //alert(response);
 }
});
}
	    
