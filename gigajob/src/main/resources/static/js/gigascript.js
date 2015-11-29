
var locationDiv; 
function getLocation() {
	
	locationDiv = document.getElementById("autocomplete");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);      
        console.log("navigator.geoloaction: " + navigator.geolocation);       
    } else { 
    	locationDiv.innerHTML = "Geolocation is not supported by this browser.";
    }
}

function showPosition(position) {

	document.getElementById("loclat").value = position.coords.latitude;
	document.getElementById("loclon").value = position.coords.longitude;

	var address = 'https://maps.googleapis.com/maps/api/geocode/json?latlng=' +
 		position.coords.latitude + ',' + position.coords.longitude + 
 		'&key=AIzaSyCC8gwbO-U2QwEIM-8cs0DFiGBuj_c4QwY';	

	$.ajax({
	    url: address
	}).then(function(data) {
		var formattedAddress = data.results[0].formatted_address;
		locationDiv.value = formattedAddress;
		document.getElementById("formattedAddress").value = formattedAddress;
	}); 	
}

	   
function toggleAvail(){
    console.log('wtf');

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
	    
